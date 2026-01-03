package org.asupg.downloader.service.impl;

import org.asupg.downloader.config.BankClientConfig;
import org.asupg.downloader.model.AuthDTO;
import org.asupg.downloader.model.SessionDTO;
import org.asupg.downloader.service.ExternalApiService;
import org.asupg.downloader.service.RequestReportService;
import org.asupg.downloader.util.ConstantsUtil;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.asupg.downloader.util.ExtractorUtil.extractPatternFromBody;

@Singleton
public class RequestReportServiceImpl implements RequestReportService {

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
        Map<String, String> reportFormBody = Map.of(
                "dtid", session.getDtUuid(),
                "cmd_0", ConstantsUtil.CMD_ON_CLICK,
                "uuid_0", auth.getStatementBtnUuid(),
                "data_0", ConstantsUtil.REQUEST_REPORT_DATA
        );

        String responseBody = externalApiService.performPost(
                bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT,
                reportFormBody
        );

        String reportWindowUuid = extractPatternFromBody(responseBody, ConstantsUtil.REPORT_WINDOW_PATTERN, "report window uuid");
        String startDateUuid = extractPatternFromBody(responseBody, ConstantsUtil.START_DATE_PATTERN, "start date uuid");
        String endDateUuid = extractPatternFromBody(responseBody, ConstantsUtil.END_DATE_PATTERN, "end date uuid");
        String excelBtnUuid = extractPatternFromBody(responseBody, ConstantsUtil.EXCEL_BUTTON_PATTERN, "excel button uuid");

        //Set from date
        Map<String, String> setFromFormBody = setFromFormBody(session.getDtUuid(), reportWindowUuid, startDateUuid);

        externalApiService.performPost(bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT, setFromFormBody);

        //Set to date
        Map<String, String> setToFormBody = setToFormBody(session.getDtUuid(), endDateUuid);

        externalApiService.performPost(bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT, setToFormBody);

        //Request report
        String reportResponseBody = externalApiService.performPost(
                bankClientConfig.getHost() + ConstantsUtil.ZKAU_ENDPOINT,
                requestReportFormBody(session.getDtUuid(), reportWindowUuid, excelBtnUuid));

        return extractDownloadUrl(reportResponseBody, bankClientConfig.getHost());
    }

    private Map<String, String> setFromFormBody(String dtUuid, String reportWindowUuid, String startDateUuid) {
        Map<String, String> setFromFormBody = new LinkedHashMap<>();

        setFromFormBody.put("dtid", dtUuid);

        setFromFormBody.put("cmd_0", ConstantsUtil.CMD_ON_MOVE);
        setFromFormBody.put("opt_0", ConstantsUtil.REQUEST_REPORT_WINDOWS_OPT);
        setFromFormBody.put("uuid_0", reportWindowUuid);
        setFromFormBody.put("data_0", ConstantsUtil.REQUEST_REPORT_WINDOWS_MOVE_DATA);

        setFromFormBody.put("cmd_1", ConstantsUtil.CMD_ON_ZINDEX);
        setFromFormBody.put("opt_1", ConstantsUtil.REQUEST_REPORT_WINDOWS_OPT);
        setFromFormBody.put("uuid_1", reportWindowUuid);
        setFromFormBody.put("data_1", ConstantsUtil.REQUEST_REPORT_WINDOWS_ZINDEX_DATA);

        setFromFormBody.put("cmd_2", ConstantsUtil.CMD_ON_MOVE);
        setFromFormBody.put("opt_2", ConstantsUtil.REQUEST_REPORT_WINDOWS_OPT);
        setFromFormBody.put("uuid_2", reportWindowUuid);
        setFromFormBody.put("data_2", ConstantsUtil.REQUEST_REPORT_WINDOWS_MOVE_DATA);

        setFromFormBody.put("cmd_3", ConstantsUtil.CMD_ON_CHANGE);
        setFromFormBody.put("uuid_3", startDateUuid);
        setFromFormBody.put("data_3", buildDateChangeData(LocalDate.now().minusDays(ConstantsUtil.DAY_DIFFERENCE)));

        setFromFormBody.put("cmd_4", ConstantsUtil.CMD_ON_BLUR);
        setFromFormBody.put("uuid_4", startDateUuid);

        return setFromFormBody;
    }

    private Map<String, String> setToFormBody(String dtUuid, String endDateUuid) {
        Map<String, String> setToFormBody = new LinkedHashMap<>();

        setToFormBody.put("dtid", dtUuid);

        setToFormBody.put("cmd_0", ConstantsUtil.CMD_ON_CHANGE);
        setToFormBody.put("uuid_0", endDateUuid);
        setToFormBody.put("data_0", buildDateChangeData(LocalDate.now()));

        setToFormBody.put("cmd_1", ConstantsUtil.CMD_ON_BLUR);
        setToFormBody.put("uuid_1", endDateUuid);

        return setToFormBody;
    }

    private Map<String, String> requestReportFormBody(String dtUuid, String reportWindowUuid, String excelBtnUuid) {
        Map<String, String> requestReportFormBody = new LinkedHashMap<>();

        requestReportFormBody.put("dtid", dtUuid);

        requestReportFormBody.put("cmd_0", ConstantsUtil.REQUEST_REPORT_WINDOWS_OPT);
        requestReportFormBody.put("uuid_0", reportWindowUuid);
        requestReportFormBody.put("data_0", ConstantsUtil.REQUEST_REPORT_WINDOWS_MOVE_DATA);

        requestReportFormBody.put("cmd_1", ConstantsUtil.CMD_ON_ZINDEX);
        requestReportFormBody.put("opt_1", ConstantsUtil.REQUEST_REPORT_WINDOWS_OPT);
        requestReportFormBody.put("uuid_1", reportWindowUuid);
        requestReportFormBody.put("data_1", ConstantsUtil.REQUEST_REPORT_WINDOWS_ZINDEX_DATA);

        requestReportFormBody.put("cmd_2", ConstantsUtil.CMD_ON_MOVE);
        requestReportFormBody.put("uuid_2", reportWindowUuid);
        requestReportFormBody.put("data_2", ConstantsUtil.REQUEST_REPORT_WINDOWS_MOVE_DATA);

        requestReportFormBody.put("cmd_3", ConstantsUtil.CMD_ON_CLICK);
        requestReportFormBody.put("uuid_3", excelBtnUuid);
        requestReportFormBody.put("data_3", ConstantsUtil.REQUEST_REPORT_EXCEL_BTN_DATA);

        return requestReportFormBody;
    }

    private String buildDateChangeData(LocalDate date) {
        String zkDate = date.getYear() + "." +
                date.getMonthValue() + "." +
                date.getDayOfMonth() + ".0.0.0.0";

        return String.format(
                ConstantsUtil.DATE_CHANGE_DATA,
                zkDate
        );
    }

    private String extractDownloadUrl(
            String responseBody,
            String baseUrl
    ) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode rsNode = root.path("rs");

            if (!rsNode.isArray()) {
                throw new IllegalStateException("Response does not contain 'rs' array");
            }

            for (JsonNode entry : rsNode) {
                if (entry.isArray()
                        && entry.size() >= 2
                        && "download".equals(entry.get(0).asText())
                        && entry.get(1).isArray()
                        && entry.get(1).size() > 0) {

                    String downloadUrl = entry.get(1).get(0).asText();

                    // Build absolute URL if needed
                    if (downloadUrl.startsWith("/")) {
                        downloadUrl = baseUrl + downloadUrl;
                    }

                    return downloadUrl;
                }
            }

            throw new IllegalStateException("Download URL not found in response");

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ZK response", e);
        }
    }

}
