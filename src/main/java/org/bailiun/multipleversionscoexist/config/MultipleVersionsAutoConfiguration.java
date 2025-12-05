package org.bailiun.multipleversionscoexist.config;

import org.bailiun.multipleversionscoexist.Aspect.SynchronousOperationAspect;
import org.bailiun.multipleversionscoexist.Properties.MultiVersionFile;
import org.bailiun.multipleversionscoexist.Properties.MultiVersionInfo;
import org.bailiun.multipleversionscoexist.Properties.MultiVersionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties({
        MultiVersionProperties.class,
        MultiVersionInfo.class,
        MultiVersionFile.class
})
public class MultipleVersionsAutoConfiguration {

    @Bean
    public ExecutorService syncOperationExecutor() {
        return Executors.newFixedThreadPool(4);
    }

    @Bean
    public RetryPolicy syncOperationRetryPolicy() {
        return new RetryPolicy(3, 1000, 5000);
    }

    @Bean
    public SynchronousOperationAspect synchronousOperationAspect(
            @Autowired(required = false) List<SynOpeImplementation> implementations,
            ExecutorService syncOperationExecutor,
            RetryPolicy syncOperationRetryPolicy) {
        SynchronousOperationAspect aspect = new SynchronousOperationAspect();
        aspect.setImplementations(Objects.requireNonNullElse(implementations, Collections.emptyList()),
                syncOperationExecutor,
                syncOperationRetryPolicy);
        return aspect;
    }

    @Bean
    @ConditionalOnMissingBean
    public VersionEnvironmentLoader versionEnvironmentLoader() {
        return new VersionEnvironmentLoader();
    }
    @Bean
    @ConditionalOnMissingBean
    public SynOpeImplementationInitialization synOpeImplementationInitialization() {
        return new SynOpeImplementationInitialization();
    }


    @Bean
    @ConditionalOnMissingBean
    public DualControllerMappingConfig dualControllerMappingConfig(MultiVersionProperties mp,
                                                                   MultiVersionInfo mi,
                                                                   MultiVersionFile mf) {
        return new DualControllerMappingConfig(mp, mi, mf);
    }
    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
