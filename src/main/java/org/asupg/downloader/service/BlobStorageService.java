package org.asupg.downloader.service;

import java.io.InputStream;

public interface BlobStorageService {

    public void upload(String blobName, InputStream data, long length);

}
