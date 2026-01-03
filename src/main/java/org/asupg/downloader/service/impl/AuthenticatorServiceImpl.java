package org.asupg.downloader.service.impl;

import org.asupg.downloader.config.BankClientConfig;
import org.asupg.downloader.model.AuthDTO;
import org.asupg.downloader.model.SessionDTO;
import org.asupg.downloader.service.AuthenticatorService;
import org.asupg.downloader.service.ExternalApiService;
import org.asupg.downloader.util.ConstantsUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.regex.Matcher;

@Singleton
public class AuthenticatorServiceImpl implements AuthenticatorService {

    private final BankClientConfig bankClientConfig;
    private final ExternalApiService externalApiService;

    @Inject
    public AuthenticatorServiceImpl(BankClientConfig bankClientConfig, ExternalApiService externalApiService) {
        this.bankClientConfig = bankClientConfig;
        this.externalApiService = externalApiService;
    }

    @Override
    public AuthDTO authenticate(SessionDTO session) {

        String host = bankClientConfig.getHost() + ConstantsUtil.AUTH_ENDPOINT + session.getJsessionId();

        Map<String, String> formBody = Map.of(
                "dtid", session.getDtUuid(),
                "cmd_0", ConstantsUtil.CMD_ON_CHANGE,
                "uuid_0", session.getPasswordUuid(),
                "data_0", buildOnChangeData(bankClientConfig.getPassword()),
                "cmd_1", ConstantsUtil.CMD_ON_CHANGE,
                "uuid_1", session.getUsernameUuid(),
                "data_1", buildOnChangeData(bankClientConfig.getUsername()),
                "cmd_2", ConstantsUtil.CMD_ON_CLICK,
                "uuid_2", session.getLoginBtnUuid(),
                "data_2", ConstantsUtil.AUTH_BTN_DATA
        );

        String responseBody = externalApiService.performPost(host, formBody);

        String statementButtonUuid = extractStatementButtonUuid(responseBody, ConstantsUtil.ACCOUNT);

        AuthDTO authDTO = new AuthDTO(statementButtonUuid);

        System.out.println("Authenticated to the service: " + authDTO.toString());

        return authDTO;

    }

    private String buildOnChangeData(String value) {
        return String.format(ConstantsUtil.AUTH_DATA, value, value.length());
    }

    private String extractStatementButtonUuid(String body, String account) {
        String accountMarker = "label:'" + account + "'";
        int accIndex = body.indexOf(accountMarker);

        if (accIndex == -1) {
            throw new IllegalStateException("Account number not found: " + account);
        }

        int sliceEnd = Math.min(body.length(), accIndex + ConstantsUtil.SAFE_SLICE_LENGTH);
        String slice = body.substring(accIndex, sliceEnd);

        Matcher matcher =
                ConstantsUtil.STATEMENT_BUTTON_PATTERN.matcher(slice);

        if (!matcher.find()) {
            throw new IllegalStateException(
                    "Выписка за период button not found for account " + account
            );
        }

        return matcher.group(1);
    }



}
