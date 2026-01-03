package org.asupg.downloader.component;

import dagger.Component;
import org.asupg.downloader.HttpTriggerJava;
import org.asupg.downloader.TimerTriggerJava;
import org.asupg.downloader.config.DaggerModule;
import org.asupg.downloader.config.ServiceBindingsModule;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
                DaggerModule.class,
                ServiceBindingsModule.class
        }
)
public interface FunctionComponent {

    HttpTriggerJava getHttpTrigger();
    TimerTriggerJava getTimerTrigger();

}
