package com.sayurbox.kale.featureflag;

public interface FeatureFlagClient {

    boolean isAllocate(String featureId, String userId);

}
