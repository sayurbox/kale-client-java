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
public abstract class ApiClientCommand<T> {

    private static final Logger logger = LoggerFactory.getLogger(ApiClientCommand.class);

    private final OkHttpClient okHttpClient;
    private final CircuitBreaker circuitBreaker;
    private final boolean isCircuitBreakerEnabled;
    protected final Gson gson;

    protected ApiClientCommand(CircuitBreaker circuitBreaker,
                               OkHttpClient okHttpClient,
                               boolean isCircuitBreakerEnabled) {
        this.okHttpClient = okHttpClient;
        this.circuitBreaker = circuitBreaker;
        this.isCircuitBreakerEnabled = isCircuitBreakerEnabled;
        this.gson = new Gson();
    }

    protected T run() {
        Response response = null;
        try {
            Request request = createRequest();
            logger.info("requesting kale: " + request.url());
            response = okHttpClient.newCall(request).execute();
            return handleResponse(response);
        } catch (IOException e) {
            logger.error("failed request from kale, ", e);
            System.out.println("failed request from kale, "+ e.getMessage());
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
