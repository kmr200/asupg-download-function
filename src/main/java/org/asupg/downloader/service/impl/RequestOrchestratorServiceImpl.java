package org.asupg.downloader.service.impl;

import org.asupg.downloader.config.BankClientConfig;
import org.asupg.downloader.model.AuthDTO;
import org.asupg.downloader.model.SessionDTO;
import org.asupg.downloader.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RequestOrchestratorServiceImpl implements RequestOrchestratorService {

    private static final Logger logger = LoggerFactory.getLogger(RequestOrchestratorServiceImpl.class);

    private final BankClientConfig bankClientConfig;
    private final SessionInitializerService sessionInitializerService;
    private final AuthenticatorService authenticatorService;
    private final RequestReportService requestReportService;
    private final FileDownloadService fileDownloadService;
    private final BlobStorageService blobStorageService;

    @Inject
    public RequestOrchestratorServiceImpl(
            BankClientConfig bankClientConfig,
            SessionInitializerService sessionInitializerService,
            AuthenticatorService authenticatorService,
            RequestReportService requestReportService,
            FileDownloadService fileDownloadService,
            BlobStorageService blobStorageService
    ) {
        this.bankClientConfig = bankClientConfig;
        this.sessionInitializerService = sessionInitializerService;
        this.authenticatorService = authenticatorService;
        this.requestReportService = requestReportService;
        this.fileDownloadService = fileDownloadService;
        this.blobStorageService = blobStorageService;
    }

    public void requestReport() {

        SessionDTO session = sessionInitializerService.requestSession(bankClientConfig.getHost());

        AuthDTO authDTO = authenticatorService.authenticate(session);

        String downloadUrl = requestReportService.requestReport(session, authDTO);
        logger.info("Extracted download url: {}", downloadUrl);

        String blobName =
                "report-" + System.currentTimeMillis() + ".xlsx";

        try {
            var download =
                    fileDownloadService.download(downloadUrl);

            blobStorageService.upload(
                    blobName,
                    download.stream,
                    download.length
            );

            download.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to download/upload report", e);
        }
    }

}
