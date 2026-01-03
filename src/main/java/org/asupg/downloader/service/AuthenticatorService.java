package org.asupg.downloader.service;

import org.asupg.downloader.model.AuthDTO;
import org.asupg.downloader.model.SessionDTO;

public interface AuthenticatorService {

    public AuthDTO authenticate(SessionDTO session);

}
