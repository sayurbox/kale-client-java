package com.sayurbox.kale.abtest;

import com.sayurbox.kale.config.KaleConfig;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.abtest.command.GetAllUniverseAllocationsCommand;
import com.sayurbox.kale.abtest.command.GetUniverseAllocationCommand;
import okhttp3.OkHttpClient;

public class ABTestClientImpl implements ABTestClient {

    private final KaleConfig kaleConfig;
    private final OkHttpClient httpClient;
    private final Gson gson;

    public ABTestClientImpl(KaleConfig kaleConfig) {
        this.kaleConfig = kaleConfig;
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    @Override
    public JsonObject getUniverseAllocation(String userId, String universeId) {
        GetUniverseAllocationCommand cmd = new GetUniverseAllocationCommand(this.kaleConfig.getHystrixParams(),
                this.httpClient, this.kaleConfig.getBaseUrl(),
                userId, universeId);
    
        GetUniverseAllocationResponse response = cmd.execute();
        JsonObject json = constructABTestJson(response);

        return json;
    }

    @Override
    public JsonArray getAllUniverseAllocations(String userId) {
        GetAllUniverseAllocationsCommand cmd = new GetAllUniverseAllocationsCommand(this.kaleConfig.getHystrixParams(),
                this.httpClient, this.kaleConfig.getBaseUrl(),
                userId);
    
        List<GetUniverseAllocationResponse> response = cmd.execute();

        JsonArray jsons = new JsonArray();
        for (GetUniverseAllocationResponse res : response) {
            JsonObject json = constructABTestJson(res);
            jsons.add(json);
        }

        return jsons;
    }

    private JsonObject constructABTestJson(GetUniverseAllocationResponse response) {
        JsonObject json = new JsonObject();
        json.addProperty("universeId", response.getUniverseId());
        json.addProperty("experimentId", response.getExperimentId());
        json.add("configs", gson.toJsonTree(response.getConfigs()));
        return json;
    }
}
