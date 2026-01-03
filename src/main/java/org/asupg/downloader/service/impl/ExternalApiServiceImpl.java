package org.asupg.downloader.service.impl;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.asupg.downloader.service.ExternalApiService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class ExternalApiServiceImpl implements ExternalApiService {

    private final CloseableHttpClient httpClient;

    @Inject
    public ExternalApiServiceImpl(
            CloseableHttpClient httpClient
    ) {
        this.httpClient = httpClient;
    }

    public String performGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        addBaseHeaders(httpGet);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("HTTP request failed", e);
        }
    }

    public String performPost(String url, Map<String, String> formBody) {
        HttpPost httpPost = new HttpPost(url);
        addBaseHeaders(httpPost);

        List<NameValuePair> params = new ArrayList<>(formBody.size());
        for (Map.Entry<String, String> entry : formBody.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("HTTP request failed", e);
        }
    }

    private void addBaseHeaders(HttpUriRequestBase httpUriRequestBase) {
        httpUriRequestBase.addHeader("Accept", "*/*");
        httpUriRequestBase.addHeader("Accept-Encoding", "gzip, deflate, br");
        httpUriRequestBase.addHeader("Connection", "keep-alive");
    }

}
