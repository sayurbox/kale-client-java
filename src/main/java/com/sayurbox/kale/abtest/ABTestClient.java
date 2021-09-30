package com.sayurbox.kale.abtest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface ABTestClient {

    JsonArray getAllUniverseAllocations(String userId);
    JsonObject getUniverseAllocation(String userId, String universeId);

}
