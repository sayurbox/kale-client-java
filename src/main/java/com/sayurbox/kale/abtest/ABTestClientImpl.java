package com.sayurbox.kale.abtest;

import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.abtest.command.GetAllUniverseAllocationsCommand;
import com.sayurbox.kale.abtest.command.GetUniverseAllocationByNameCommand;
import com.sayurbox.kale.abtest.command.GetUniverseAllocationCommand;
import com.sayurbox.kale.common.KaleClientImpl;
import com.sayurbox.kale.config.KaleConfig;

import java.util.List;
import java.util.Map;

public class ABTestClientImpl extends KaleClientImpl implements ABTestClient {

    public ABTestClientImpl(KaleConfig kaleConfig) {
        super("kaleApiABTest", kaleConfig);
    }

    @Override
    public GetUniverseAllocationResponse getUniverseAllocation(String userId, String universeId) {
        GetUniverseAllocationCommand cmd = new GetUniverseAllocationCommand(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().isEnabled(),
                this.kaleConfig.getBaseUrl(),
                userId, universeId, null);
        return cmd.execute();
    }

    @Override
    public List<GetUniverseAllocationResponse> getAllUniverseAllocations(String userId) {
        GetAllUniverseAllocationsCommand cmd = new GetAllUniverseAllocationsCommand(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().isEnabled(),
                this.kaleConfig.getBaseUrl(),
                userId, null);
        return cmd.execute();
    }

    @Override
    public List<GetUniverseAllocationResponse> getAllUniverseAllocations(String userId, Map<String, String> properties) {
        GetAllUniverseAllocationsCommand cmd = new GetAllUniverseAllocationsCommand(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().isEnabled(),
                this.kaleConfig.getBaseUrl(),
                userId, properties);
        return cmd.execute();
    }

    @Override
    public GetUniverseAllocationResponse getUniverseAllocation(String userId, String universeId, Map<String, String> properties) {
        GetUniverseAllocationCommand cmd = new GetUniverseAllocationCommand(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().isEnabled(),
                this.kaleConfig.getBaseUrl(),
                userId, universeId, properties);
        return cmd.execute();
    }

    @Override
    public GetUniverseAllocationResponse getUniverseAllocationByName(String userId, String universeName) {
        GetUniverseAllocationByNameCommand cmd = new GetUniverseAllocationByNameCommand(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().isEnabled(),
                this.kaleConfig.getBaseUrl(),
                userId, universeName, null);
        return cmd.execute();
    }

    @Override
    public GetUniverseAllocationResponse getUniverseAllocationByName(String userId, String universeName, Map<String, String> properties) {
        GetUniverseAllocationByNameCommand cmd = new GetUniverseAllocationByNameCommand(this.circuitBreaker,
                this.httpClient, this.kaleConfig.getCircuitBreakerParams().isEnabled(),
                this.kaleConfig.getBaseUrl(),
                userId, universeName, properties);
        return cmd.execute();
    }
}
