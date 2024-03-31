package com.sayurbox.kale.featureflag;

import java.util.Set;

public interface FeatureFlagClient {

    boolean isAllocate(String featureId, String userId);

    boolean isAllocateV2(String featureName, String userId);

    Set<String> getAllocatedFeatureNames(String userId);

}
