package org.asupg.downloader.service.impl;

import org.asupg.downloader.config.BankClientConfig;
import org.asupg.downloader.model.AuthDTO;
import org.asupg.downloader.model.SessionDTO;
import org.asupg.downloader.service.ExternalApiService;
import org.asupg.downloader.service.RequestReportService;
import org.asupg.downloader.util.ConstantsUtil;
import org.asupg.downloader.util.ExtractorUtil;
import org.asupg.downloader.util.FormBodyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import static org.asupg.downloader.util.ExtractorUtil.extractPatternFromBody;

@Singleton
public class RequestReportServiceImpl implements RequestReportService {

    private static final Logger logger = LoggerFactory.getLogger(RequestReportServiceImpl.class);

    private final ExternalApiService externalApiService;
    private final BankClientConfig bankClientConfig;
    private final ObjectMapper objectMapper;

    @Inject
    public RequestReportServiceImpl(
            ExternalApiService externalApiService,
            BankClientConfig bankClientConfig,
            ObjectMapper objectMapper
    ) {
        this.externalApiService = externalApiService;
        this.bankClientConfig = bankClientConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    public String requestReport(SessionDTO session, AuthDTO auth) {

        //Open the report window
        logger.info("Sending 'open report windows' request");
        Map<String, String> reportFormBody = FormBodyUtil.openReportFormBody(session.getDtUuid(), auth.getStatementBtnUuid());

        String responseBody = externalApiService.performPost(
                bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT,
                reportFormBody
        );

        String reportWindowUuid = extractPatternFromBody(responseBody, ConstantsUtil.REPORT_WINDOW_PATTERN, "report window uuid");
        String startDateUuid = extractPatternFromBody(responseBody, ConstantsUtil.START_DATE_PATTERN, "start date uuid");
        String endDateUuid = extractPatternFromBody(responseBody, ConstantsUtil.END_DATE_PATTERN, "end date uuid");
        String excelBtnUuid = extractPatternFromBody(responseBody, ConstantsUtil.EXCEL_BUTTON_PATTERN, "excel button uuid");

        //Set from date
        logger.info("Sending 'set from date' request");
        Map<String, String> setFromFormBody = FormBodyUtil.setFromFormBody(session.getDtUuid(), reportWindowUuid, startDateUuid);

        externalApiService.performPost(bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT, setFromFormBody);

        //Set to date
        logger.info("Sending 'set end date' request");
        Map<String, String> setToFormBody = FormBodyUtil.setToFormBody(session.getDtUuid(), endDateUuid);

        externalApiService.performPost(bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT, setToFormBody);

        //Request report
        logger.info("Sending 'get report download url' request");
        String reportResponseBody = externalApiService.performPost(
                bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT,
                FormBodyUtil.requestReportFormBody(session.getDtUuid(), reportWindowUuid, excelBtnUuid));

        return ExtractorUtil.extractDownloadUrl(reportResponseBody, bankClientConfig.getHost(), objectMapper);
    }

}
