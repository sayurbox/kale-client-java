package com.sayurbox.kale.featureflag;

import com.sayurbox.kale.common.KaleClientImpl;
import com.sayurbox.kale.config.KaleConfig;
import com.sayurbox.kale.featureflag.command.GetAllocateCommand;

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

}
