# Kale-client-java
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sayurbox_kale-client-java&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=sayurbox_kale-client-java)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sayurbox_kale-client-java&metric=coverage)](https://sonarcloud.io/summary/new_code?id=sayurbox_kale-client-java)

Java client library to call kale API

## Feature

- [Circuit-Breaker](https://resilience4j.readme.io/docs/circuitbreaker)
- Http Retry (TBD)

## Usage

Add the JitPack repository to your root build file
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Add the dependency

```groovy
dependencies {
    implementation 'com.github.sayurbox:kale-client-java:<release-version>'
}
```

## Example

```java
KaleConfig config = new KaleConfig.Builder()
        // required
        .withBaseUrl("http://localhost:9494")
        // optional: default is 5_000ms (5s) 
        .withExecutionTimeout(1000)
        // optional: default is true 
        .withCircuitBreakerEnabled(true)
        // optional: default is 10, equivalent to "slidingWindowSize"
        .withCircuitBreakerFailureVolumeThreshold(5)
        // optional: default is 10_000ms (1s), equivalent to "slowCallDurationThreshold"
        .withCircuitBreakerSlowResponseThreshold(500)
        // optional: default is 20_000ms (20s), equivalent to "waitDurationInOpenState"
        .withCircuitBreakerWaitDurationOpenState(15_000)
        .build();

FeatureFlagClient ff = new FeatureFlagClientImpl(config);
// check if user is allocated to a feature ?
boolean isAllocate = ff.isAllocate("ae2802be-86b6-47dd-a17a-864e4c76b49d",
        "cSExFZtCP8ee9cfr7yJVVmcsi5A3");
if (isAllocate) {
    System.out.println("user is allocated");
} else {
    System.out.println("user is not allocated");
}

```