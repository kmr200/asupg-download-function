package org.asupg.downloader.config;

import com.microsoft.azure.functions.spi.inject.FunctionInstanceInjector;
import org.asupg.downloader.HttpTriggerJava;
import org.asupg.downloader.TimerTriggerJava;
import org.asupg.downloader.component.FunctionComponent;
import org.asupg.downloader.component.DaggerFunctionComponent;

public class InstanceInjector implements FunctionInstanceInjector {

    private static final FunctionComponent COMPONENT = DaggerFunctionComponent.create();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(Class<T> functionClass) throws Exception {
        if (functionClass.equals(HttpTriggerJava.class)) {
            return (T) COMPONENT.getHttpTrigger();
        }
        if (functionClass.equals(TimerTriggerJava.class)) {
            return (T) COMPONENT.getTimerTrigger();
        }
        throw new IllegalArgumentException("Unsupported function class " + functionClass);
    }

}
