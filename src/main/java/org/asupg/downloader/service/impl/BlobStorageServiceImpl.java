package org.asupg.downloader.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.asupg.downloader.service.BlobStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.InputStream;

@Singleton
public class BlobStorageServiceImpl implements BlobStorageService {

    private static final Logger logger = LoggerFactory.getLogger(BlobStorageServiceImpl.class);

    private final BlobContainerClient blobContainerClient;

    @Inject
    public BlobStorageServiceImpl(
            @Named("BLOB_STORAGE_CONN_STR") String blobStorageConnStr,
            @Named("BLOB_CONTAINER_NAME") String blobContainerName
    ) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(blobStorageConnStr)
                .buildClient();

        this.blobContainerClient = blobServiceClient.getBlobContainerClient(blobContainerName);

        if (!this.blobContainerClient.exists()) {
            this.blobContainerClient.create();
        }
    }

    @Override
    public void upload(
            String blobName,
            InputStream data,
            long length
    ) {
        BlobClient blobClient = this.blobContainerClient.getBlobClient(blobName);
        blobClient.upload(data, length, true);
        logger.info("Uploaded blob: {}", blobName);
    }

}
