package org.asupg.downloader.service.impl;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.asupg.downloader.service.FileDownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;

@Singleton
public class FileDownloadServiceImpl implements FileDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadServiceImpl.class);

    private final CloseableHttpClient httpClient;

    @Inject
    public FileDownloadServiceImpl(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public InputStreamWithLength download(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if (entity == null) {
            throw new IllegalStateException("Empty response");
        }

        logger.info("Received InputStream for: {}", url);

        return new InputStreamWithLength(entity.getContent(), entity.getContentLength(), response);
    }

    public static class InputStreamWithLength {
        public final InputStream stream;
        public final long length;
        private final CloseableHttpResponse response;

        InputStreamWithLength(
                InputStream stream,
                long length,
                CloseableHttpResponse response
        ) {
            this.stream = stream;
            this.length = length;
            this.response = response;
        }

        public void close() throws IOException {
            response.close();
        }
    }

}
