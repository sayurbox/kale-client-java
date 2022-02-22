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
import java.util.Arrays;
import java.util.function.Supplier;

public abstract class ClientCommand<T> {

    private static final Logger logger = LoggerFactory.getLogger(ClientCommand.class);

    private final OkHttpClient okHttpClient;
    private final CircuitBreaker circuitBreaker;
    private final boolean isCircuitBreakerEnabled;
    protected final Gson gson;

    protected ClientCommand(CircuitBreaker circuitBreaker,
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
        Supplier<T> supplier = this::run;
        Decorators.DecorateSupplier<T> decorated = Decorators
                .ofSupplier(supplier)
                .withCircuitBreaker(this.circuitBreaker)
                .withFallback(Arrays.asList(CallNotPermittedException.class),
                        e -> getFallback());

        if (this.isCircuitBreakerEnabled) {
            System.out.println("enabled>>");
            //decorated.withCircuitBreaker(this.circuitBreaker);
        }

        Supplier<T> decorator = decorated.decorate();
        return decorator.get();
    }

    public abstract T getFallback();

    protected abstract T handleResponse(Response response) throws IOException;

    protected abstract Request createRequest();
}
