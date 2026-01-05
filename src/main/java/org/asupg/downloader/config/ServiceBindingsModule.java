package org.asupg.downloader.config;

import dagger.Binds;
import dagger.Module;
import org.asupg.downloader.service.*;
import org.asupg.downloader.service.impl.*;

import javax.inject.Singleton;

@Module
public abstract class ServiceBindingsModule {

    @Binds
    @Singleton
    abstract RequestOrchestratorService bindRequestOrchestratorService(RequestOrchestratorServiceImpl service);

    @Binds
    @Singleton
    abstract AuthenticatorService bindAuthenticatorService(AuthenticatorServiceImpl service);

    @Binds
    @Singleton
    abstract ExternalApiService bindExternalApiService(ExternalApiServiceImpl service);

    @Binds
    @Singleton
    abstract SessionInitializerService bindSessionInitializerService(SessionInitializerServiceImpl service);

    @Binds
    @Singleton
    abstract RequestReportService bindRequestReportService(RequestReportServiceImpl service);

    @Binds
    @Singleton
    abstract BlobStorageService bindBlobStorageService(BlobStorageServiceImpl service);

    @Binds
    @Singleton
    abstract FileDownloadService bindFileDownloadService(FileDownloadServiceImpl service);

}
