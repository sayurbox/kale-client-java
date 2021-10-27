package com.sayurbox.kale.featureflag;

import com.sayurbox.kale.config.KaleConfig;
import com.sayurbox.kale.featureflag.command.GetAllocateCommand;
import okhttp3.OkHttpClient;

public class FeatureFlagClientImpl implements FeatureFlagClient {

    private final KaleConfig kaleConfig;
    private final OkHttpClient httpClient;

    public FeatureFlagClientImpl(KaleConfig kaleConfig) {
        this.kaleConfig = kaleConfig;
        this.httpClient = new OkHttpClient();
    }

    @Override
    public boolean isAllocate(String featureId, String userId) {
        GetAllocateCommand cmd = new GetAllocateCommand(this.kaleConfig.getHystrixParams(),
                this.httpClient, this.kaleConfig.getBaseUrl(),
                userId, featureId);
        System.out.println("just a test");
        return cmd.execute().getRollout();
    }
}
