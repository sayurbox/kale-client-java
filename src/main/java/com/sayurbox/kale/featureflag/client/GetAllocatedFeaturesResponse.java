package com.sayurbox.kale.featureflag.client;

import java.util.Set;

public class GetAllocatedFeaturesResponse {

    private Set<Feature> features;

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    public Set<Feature> getFeatures() {
        return features;
    }
}
