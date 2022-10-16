package com.sayurbox.kale.featureflag;

public interface FeatureFlagClient {

    boolean isAllocate(String featureId, String userId);

    boolean isAllocateV2(String featureName, String userId);

}
