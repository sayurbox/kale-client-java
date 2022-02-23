package com.sayurbox.kale.featureflag;

import com.sayurbox.kale.config.CircuitBreakerParams;
import com.sayurbox.kale.config.KaleConfig;
import com.sayurbox.kale.featureflag.command.GetAllocateCommand;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FeatureFlagClientImpl implements FeatureFlagClient {

    private final KaleConfig kaleConfig;
    private final OkHttpClient httpClient;
    private final CircuitBreaker circuitBreaker;

    public FeatureFlagClientImpl(KaleConfig kaleConfig) {
        this.kaleConfig = kaleConfig;
        CircuitBreakerParams circuitBreakerParams = kaleConfig.getCircuitBreakerParams();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (kaleConfig.isLogEnabled()) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(kaleConfig.getExecutionTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(kaleConfig.getExecutionTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(kaleConfig.getExecutionTimeout(), TimeUnit.MILLISECONDS)
                .addInterceptor(logging)
                .build();

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(circuitBreakerParams.getSlidingWindowSize())
                .slowCallRateThreshold(70.0f)
                .slowCallDurationThreshold(Duration.ofMillis(
                        circuitBreakerParams.getSlowCallDurationThreshold()))
                .waitDurationInOpenState(Duration.ofMillis(
                        circuitBreakerParams.getWaitDurationInOpenState()))
                .recordExceptions(IOException.class, TimeoutException.class)
                .build();

        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);
        this.circuitBreaker = circuitBreakerRegistry
                .circuitBreaker("kaleApiFeatureFlag");
    }

    @Override
    public boolean isAllocate(String featureId, String userId) {
        GetAllocateCommand cmd = new GetAllocateCommand(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().isEnabled(),
                this.kaleConfig.getBaseUrl(), userId, featureId);
        return cmd.execute().getRollout();
    }

}
