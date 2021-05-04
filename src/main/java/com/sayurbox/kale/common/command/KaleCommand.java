package com.sayurbox.kale.common.command;

import com.google.gson.Gson;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandProperties;
import com.sayurbox.kale.config.KaleHystrixParams;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public abstract class KaleCommand<T> extends HystrixCommand<T> {

    private static final Logger logger = LoggerFactory.getLogger(KaleCommand.class);

    private final OkHttpClient okHttpClient;
    protected final Gson gson;
    protected final String baseUrl;

    protected KaleCommand(KaleHystrixParams hystrixParams,
                          OkHttpClient okHttpClient,
                          String baseUrl) {
        super(hystrixSetter(hystrixParams));
        this.okHttpClient = okHttpClient;
        this.gson = new Gson();
        this.baseUrl = baseUrl;
    }

    @Override
    public T run() {
        Response response = null;
        try {
            Request request = createRequest();
            logger.info("requesting kale: " + request.url());
            response = okHttpClient.newCall(request).execute();
            return handleResponse(response);
        } catch (Exception e) {
            logger.error("failed request from kale, ", e);
            throw new RuntimeException(e);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }


    @Override
    public abstract T getFallback();

    protected abstract T handleResponse(Response response) throws Exception;

    protected abstract Request createRequest() throws UnsupportedEncodingException;

    private static Setter hystrixSetter(KaleHystrixParams params) {
        HystrixCommand.Setter config = HystrixCommand.Setter.withGroupKey(params.getHystrixCommandGroupKey());
        HystrixCommandProperties.Setter properties = HystrixCommandProperties.Setter();
        properties.withExecutionTimeoutInMilliseconds(params.getExecutionTimeout());
        properties.withCircuitBreakerSleepWindowInMilliseconds(params.getCircuitBreakerSleepWindow());
        properties.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);
        properties.withCircuitBreakerEnabled(true);
        properties.withCircuitBreakerRequestVolumeThreshold(params.getCircuitBreakerRequestVolumeThreshold());
        properties.withMetricsRollingStatisticalWindowInMilliseconds(params.getMetricRollingStatisticalWindow());
        properties.withMetricsHealthSnapshotIntervalInMilliseconds(params.getMetricsHealthSnapshotInterval());
        config.andCommandPropertiesDefaults(properties);
        return config;
    }

}
