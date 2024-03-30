package com.sayurbox.kale.common.command;

import com.google.gson.Gson;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Supplier;

// Helper for http client wrapped by circuit breaker
public abstract class KaleCommand<T> {

    private static final Logger logger = LoggerFactory.getLogger(KaleCommand.class);

    private final OkHttpClient okHttpClient;
    private final CircuitBreaker circuitBreaker;
    private final boolean isCircuitBreakerEnabled;
    protected final Gson gson;
    protected final String baseUrl;

    protected KaleCommand(CircuitBreaker circuitBreaker,
                          OkHttpClient okHttpClient,
                          boolean isCircuitBreakerEnabled,
                          String baseUrl) {
        this.okHttpClient = okHttpClient;
        this.circuitBreaker = circuitBreaker;
        this.isCircuitBreakerEnabled = isCircuitBreakerEnabled;
        this.gson = new Gson();
        this.baseUrl = baseUrl;
    }

    protected T run() {
        Response response = null;
        Request request = createRequest();

        try {
            response = okHttpClient.newCall(request).execute();
            return handleResponse(response);
        } catch (Exception e) {
            logger.error("failed request {} from kale, ", request.url(), e);
            return getFallback();
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

    public T execute() {
        // ref code examples:
        // https://github.com/thombergs/code-examples/blob/master/resilience4j/timelimiter/src/main/java/io/reflectoring/resilience4j/timelimiter/Examples.java
        Supplier<T> supplier = this::run;
        if (!this.isCircuitBreakerEnabled) {
            return supplier.get();
        }
        Decorators.DecorateSupplier<T> decorated = Decorators
                .ofSupplier(supplier)
                .withCircuitBreaker(this.circuitBreaker)
                .withFallback(Collections.singletonList(CallNotPermittedException.class), e -> {
                    logger.warn("execute fallback CallNotPermittedException");
                    return getFallback();
                });
        Supplier<T> decorator = decorated.decorate();
        return decorator.get();
    }

    public abstract T getFallback();

    protected abstract T handleResponse(Response response) throws IOException;

    protected abstract Request createRequest();

}
