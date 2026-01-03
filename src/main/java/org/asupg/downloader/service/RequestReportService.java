package org.asupg.downloader.service;

import org.asupg.downloader.model.AuthDTO;
import org.asupg.downloader.model.SessionDTO;

public interface RequestReportService {

    public String requestReport(SessionDTO session, AuthDTO auth);

}
