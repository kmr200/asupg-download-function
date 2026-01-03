package org.asupg.downloader.service.impl;

import org.asupg.downloader.config.BankClientConfig;
import org.asupg.downloader.model.AuthDTO;
import org.asupg.downloader.model.SessionDTO;
import org.asupg.downloader.service.AuthenticatorService;
import org.asupg.downloader.service.RequestOrchestratorService;
import org.asupg.downloader.service.RequestReportService;
import org.asupg.downloader.service.SessionInitializerService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RequestOrchestratorServiceImpl implements RequestOrchestratorService {

    private final BankClientConfig bankClientConfig;
    private final SessionInitializerService sessionInitializerService;
    private final AuthenticatorService authenticatorService;
    private final RequestReportService requestReportService;

    @Inject
    public RequestOrchestratorServiceImpl(
            BankClientConfig bankClientConfig,
            SessionInitializerService sessionInitializerService,
            AuthenticatorService authenticatorService,
            RequestReportService requestReportService
    ) {
        this.bankClientConfig = bankClientConfig;
        this.sessionInitializerService = sessionInitializerService;
        this.authenticatorService = authenticatorService;
        this.requestReportService = requestReportService;
    }

    public void requestReport() {

        SessionDTO session = sessionInitializerService.requestSession(bankClientConfig.getHost());

        AuthDTO authDTO = authenticatorService.authenticate(session);

        String downloadUrl = requestReportService.requestReport(session, authDTO);

        System.out.println(downloadUrl);
    }

}
