package com.sayurbox.kale.featureflag;

import com.sayurbox.kale.common.KaleClientImpl;
import com.sayurbox.kale.config.KaleConfig;
import com.sayurbox.kale.featureflag.client.Feature;
import com.sayurbox.kale.featureflag.command.GetAllocateCommand;
import com.sayurbox.kale.featureflag.command.GetAllocateCommandV2;
import com.sayurbox.kale.featureflag.command.GetAllocatedFeatureNamesCommand;

import java.util.Set;
import java.util.stream.Collectors;

public class FeatureFlagClientImpl extends KaleClientImpl implements FeatureFlagClient {

    public FeatureFlagClientImpl(KaleConfig kaleConfig) {
        super("kaleApiFeatureFlag", kaleConfig);
    }

    @Override
    public boolean isAllocate(String featureId, String userId) {
        GetAllocateCommand cmd = new GetAllocateCommand(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().enabled(),
                this.kaleConfig.getBaseUrl(), userId, featureId);
        return cmd.execute().getRollout();
    }

    @Override
    public boolean isAllocateV2(String featureName, String userId) {
        GetAllocateCommandV2 cmd = new GetAllocateCommandV2(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().enabled(),
                this.kaleConfig.getBaseUrl(), userId, featureName);
        return cmd.execute().getRollout();
    }

    @Override
    public Set<String> getAllocatedFeatureNames(String userId) {
        GetAllocatedFeatureNamesCommand cmd = new GetAllocatedFeatureNamesCommand(circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().enabled(),
                this.kaleConfig.getBaseUrl(), userId);
        return cmd.execute().getFeatures()
                .stream()
                .map(Feature::getFeatureName)
                .collect(Collectors.toSet());
    }
}
