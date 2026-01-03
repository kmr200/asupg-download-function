package org.asupg.downloader.service;

import java.util.Map;

public interface ExternalApiService {

    public String performGet(String url);
    public String performPost(String url, Map<String, String> formBody);

}
