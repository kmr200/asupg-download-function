package org.asupg.downloader.service;

import org.asupg.downloader.model.SessionDTO;

public interface SessionInitializerService {

    public SessionDTO requestSession(String host);

}
