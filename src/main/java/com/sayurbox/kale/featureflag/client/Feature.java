package com.sayurbox.kale.featureflag.client;

import com.google.gson.annotations.SerializedName;

public class Feature {
    @SerializedName("feature_id")
    private String featureId;
    @SerializedName("feature_name")
    private String featureName;

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureId() {
        return featureId;
    }

    public String getFeatureName() {
        return featureName;
    }
}
