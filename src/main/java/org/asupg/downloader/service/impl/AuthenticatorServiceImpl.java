package org.asupg.downloader.service.impl;

import org.asupg.downloader.config.BankClientConfig;
import org.asupg.downloader.model.AuthDTO;
import org.asupg.downloader.model.SessionDTO;
import org.asupg.downloader.service.AuthenticatorService;
import org.asupg.downloader.service.ExternalApiService;
import org.asupg.downloader.util.ConstantsUtil;
import org.asupg.downloader.util.ExtractorUtil;
import org.asupg.downloader.util.FormBodyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class AuthenticatorServiceImpl implements AuthenticatorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticatorServiceImpl.class);

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

        Map<String, String> formBody = FormBodyUtil.authFormBody(session, bankClientConfig);

        String responseBody = externalApiService.performPost(host, formBody);

        String statementButtonUuid = ExtractorUtil.extractStatementButtonUuid(responseBody, bankClientConfig.getAccount());

        AuthDTO authDTO = new AuthDTO(statementButtonUuid);

        logger.info("Authenticated to the service: {}", authDTO.toString());

        return authDTO;

    }

}
