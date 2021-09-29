package com.sayurbox.kale.abtest.client;

import java.util.List;
import java.util.Map;

public class GetUniverseAllocationResponse {

    private String user_id;
    private String universe_id;
    private String experiment_id;
    private String variant_id;
    private List<Map<String, String>> configs;

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setUniverseId(String universe_id) {
        this.universe_id = universe_id;
    }

    public void setExperimentId(String experiment_id) {
        this.experiment_id = experiment_id;
    }

    public void setVariantId(String variant_id) {
        this.variant_id = variant_id;
    }

    public void setConfigs(List<Map<String, String>> configs) {
        this.configs = configs;
    }

    public String getUserId() {
        return user_id;
    }

    public String getUniverseId() {
        return universe_id;
    }
    
    public String getExperimentId() {
        return experiment_id;
    }

    public String getVariantId() {
        return variant_id;
    }

    public List<Map<String, String>> getConfigs() {
        return configs;
    }
}
