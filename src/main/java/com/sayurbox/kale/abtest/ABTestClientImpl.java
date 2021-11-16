package com.sayurbox.kale.abtest;

import com.sayurbox.kale.config.KaleConfig;

import java.util.List;

import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.abtest.command.GetAllUniverseAllocationsCommand;
import com.sayurbox.kale.abtest.command.GetUniverseAllocationCommand;
import okhttp3.OkHttpClient;

public class ABTestClientImpl implements ABTestClient {

    private final KaleConfig kaleConfig;
    private final OkHttpClient httpClient;

    public ABTestClientImpl(KaleConfig kaleConfig) {
        this.kaleConfig = kaleConfig;
        this.httpClient = new OkHttpClient();
    }

    @Override
    public GetUniverseAllocationResponse getUniverseAllocation(String userId, String universeId) {
        GetUniverseAllocationCommand cmd = new GetUniverseAllocationCommand(this.kaleConfig.getHystrixParams(),
            this.httpClient, this.kaleConfig.getBaseUrl(),
            userId, universeId);
        return cmd.execute();
    }

    @Override
    public List<GetUniverseAllocationResponse> getAllUniverseAllocations(String userId) {
        GetAllUniverseAllocationsCommand cmd = new GetAllUniverseAllocationsCommand(this.kaleConfig.getHystrixParams(),
                this.httpClient, this.kaleConfig.getBaseUrl(),
                userId);
        return cmd.execute();
    }
}
