package com.sayurbox.kale.featureflag;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sayurbox.kale.api.GetAllocateDataResponse;
import com.sayurbox.kale.api.KaleApi;
import com.sayurbox.kale.common.client.DataResponse;
import com.sayurbox.kale.config.CircuitBreakerParams;
import com.sayurbox.kale.config.KaleConfig;
import com.sayurbox.kale.featureflag.client.GetAllocateResponse;
import com.sayurbox.kale.featureflag.command.GetAllocateCommand;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class FeatureFlagClientImpl implements FeatureFlagClient {

    private final KaleConfig kaleConfig;
    private final OkHttpClient httpClient;
    private final KaleApi kaleApi;
    private final CircuitBreaker circuitBreaker;

    public FeatureFlagClientImpl(KaleConfig kaleConfig) {
        this.kaleConfig = kaleConfig;
        CircuitBreakerParams circuitBreakerParams = kaleConfig.getCircuitBreakerParams();;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(circuitBreakerParams.getExecutionTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(kaleConfig.getCircuitBreakerParams().getExecutionTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(kaleConfig.getCircuitBreakerParams().getExecutionTimeout(), TimeUnit.MILLISECONDS)
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
                .circuitBreaker("kaleApiFeatureflag");

        Retrofit retrofit = new Retrofit.Builder()
                //.addCallAdapterFactory(CircuitBreakerCallAdapter.of(circuitBreaker))
                .addConverterFactory(GsonConverterFactory.create())
                //.baseUrl("https://kale-api-consumer-dev-ina.apps.aws.sayurbox.io")
                .baseUrl("http://localhost:5000")
                .client(this.httpClient)
                .build();

        // Get an instance of your service with circuit breaking built-in.
        this.kaleApi = retrofit.create(KaleApi.class);
    }

    @Override
    public boolean isAllocate(String featureId, String userId) {
        // code examples: https://github.com/thombergs/code-examples/blob/master/resilience4j/timelimiter/src/main/java/io/reflectoring/resilience4j/timelimiter/Examples.java
        ThreadPoolBulkheadConfig threadPoolBulkheadConfig = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(10)
                .coreThreadPoolSize(2)
                .queueCapacity(20)
                .build();

        ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry = ThreadPoolBulkheadRegistry.of(threadPoolBulkheadConfig);

        ThreadPoolBulkhead bulkheadWithDefaultConfig = threadPoolBulkheadRegistry.bulkhead("name1");

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(500))
                .build();

        TimeLimiterRegistry registry = TimeLimiterRegistry.of(config);
        TimeLimiter limiter = registry.timeLimiter("flightSearch");

        Supplier<Boolean> flightsSupplier = () -> request(userId, featureId);
        Supplier<CompletionStage<Boolean>> origCompletionStageSupplier = () -> CompletableFuture
                .supplyAsync(flightsSupplier);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        Supplier<Boolean> decorated = Decorators
                .ofSupplier(flightsSupplier)
                .withCircuitBreaker(circuitBreaker)
                .withFallback(Arrays.asList(CallNotPermittedException.class),
                        e -> false)
                .decorate();

        GetAllocateCommand cmd = new GetAllocateCommand(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().isEnabled(),
                this.kaleConfig.getBaseUrl(), userId, featureId);
        return cmd.execute().getRollout();

        //return decorated.get();
/*

        Supplier<CompletionStage<Boolean>> future = Decorators.ofSupplier(flightsSupplier)
                .withThreadPoolBulkhead(bulkheadWithDefaultConfig)
                //.withTimeLimiter(limiter, scheduler)
                .withCircuitBreaker(circuitBreaker)
                .decorate();
        try {
            future.get().toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;

 */
        /*
        Call<GetAllocateDataResponse> res = kaleApi.getFeatureAllocation(userId, featureId);
        try {
            Response<GetAllocateDataResponse> r = res.execute();
            System.out.println("here" + r.raw());
            return r.body().getData().getRollout();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
         */
        /*
        GetAllocateCommand cmd = new GetAllocateCommand(this.kaleConfig.getHystrixParams(),
                this.httpClient, this.kaleConfig.getBaseUrl(),
                userId, featureId);
        return cmd.execute().getRollout();
         */
    }

    private boolean execute(String featureId, String userId) {
        Call<GetAllocateDataResponse> res = kaleApi.getFeatureAllocation(userId, featureId);
        try {
            Response<GetAllocateDataResponse> r = res.execute();
            System.out.println("here " + r.raw());
            return r.body().getData().getRollout();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean request(String featureId, String userId) {
        String url = String.format("%s/v1/featureflag/allocation/%s/%s",
                "http://localhost:5000", userId, featureId);
        Request request = new Request.Builder().get().url(url).build();
        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            return handleResponse(response).getRollout();
        } catch (IOException e) {
            System.out.println("failed call feature alocation, " + e.getMessage());
            //throw new RuntimeException(e);
            return false;
        }
    }

    protected GetAllocateResponse handleResponse(okhttp3.Response response) throws IOException {
        String body = response.body().string();
        if (!response.isSuccessful()) {
            //throw new KaleException("Failed response from kale status: " +
            //        response.code() + " body: " + response.body().string());
            return defaultResponse();
        }
        DataResponse<GetAllocateResponse> t = new Gson().fromJson(body,
                new TypeToken<DataResponse<GetAllocateResponse>>() {}.getType());
        return t.getData();
    }

    protected GetAllocateResponse defaultResponse() {
        GetAllocateResponse response = new GetAllocateResponse();
        response.setRollout(false);
        return response;
    }
}
