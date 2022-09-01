package com.sayurbox.kale.abtest;

import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;

import java.util.HashMap;
import java.util.List;

public interface ABTestClient {

    List<GetUniverseAllocationResponse> getAllUniverseAllocations(String userId);
    GetUniverseAllocationResponse getUniverseAllocation(String userId, String universeId);

    List<GetUniverseAllocationResponse> getAllUniverseAllocations(String userId, HashMap<String, String> properties);
    GetUniverseAllocationResponse getUniverseAllocation(String userId, String universeId, HashMap<String, String> properties);
}
