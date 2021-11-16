package com.sayurbox.kale.abtest.client;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class GetUniverseAllocationResponse {

    @SerializedName("user_id")
    private String userId;
    @SerializedName("universe_id")
    private String universeId;
    @SerializedName("experiment_id")
    private String experimentId;
    @SerializedName("variant_id")
    private String variantId;
    private List<Map<String, String>> configs;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUniverseId(String universeId) {
        this.universeId = universeId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public void setConfigs(List<Map<String, String>> configs) {
        this.configs = configs;
    }

    public String getUserId() {
        return userId;
    }

    public String getUniverseId() {
        return universeId;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public String getVariantId() {
        return variantId;
    }

    public List<Map<String, String>> getConfigs() {
        return configs;
    }
}
