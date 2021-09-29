package com.sayurbox.kale.abtest;

import java.util.List;

import com.google.gson.JsonObject;

public interface ABTestClient {

    List<JsonObject> getAllUniverseAllocations(String userId);
    JsonObject getUniverseAllocation(String userId, String universeId);

}
