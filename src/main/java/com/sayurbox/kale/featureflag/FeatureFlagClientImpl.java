package com.sayurbox.kale.featureflag;

import com.sayurbox.kale.common.KaleClientImpl;
import com.sayurbox.kale.config.KaleConfig;
import com.sayurbox.kale.featureflag.command.GetAllocateCommand;
import com.sayurbox.kale.featureflag.command.GetAllocateCommandV2;

public class FeatureFlagClientImpl extends KaleClientImpl implements FeatureFlagClient {

    public FeatureFlagClientImpl(KaleConfig kaleConfig) {
        super("kaleApiFeatureFlag", kaleConfig);
    }

    @Override
    public boolean isAllocate(String featureId, String userId) {
        GetAllocateCommand cmd = new GetAllocateCommand(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().isEnabled(),
                this.kaleConfig.getBaseUrl(), userId, featureId);
        return cmd.execute().getRollout();
    }

    @Override
    public boolean isAllocateV2(String featureName, String userId) {
        GetAllocateCommandV2 cmd = new GetAllocateCommandV2(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().isEnabled(),
                this.kaleConfig.getBaseUrl(), userId, featureName);
        return cmd.execute().getRollout();
    }

}
