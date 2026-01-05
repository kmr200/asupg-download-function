package org.asupg.downloader.service;

import org.asupg.downloader.service.impl.FileDownloadServiceImpl;

import java.io.IOException;

public interface FileDownloadService {

    public FileDownloadServiceImpl.InputStreamWithLength download(String url) throws IOException;

}
