package com.sayurbox.kale.abtest;

import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;

import java.util.List;
import java.util.Map;

public interface ABTestClient {

    List<GetUniverseAllocationResponse> getAllUniverseAllocations(String userId);

    GetUniverseAllocationResponse getUniverseAllocation(String userId, String universeId);

    List<GetUniverseAllocationResponse> getAllUniverseAllocations(String userId, Map<String, String> properties);

    GetUniverseAllocationResponse getUniverseAllocation(String userId, String universeId, Map<String, String> properties);
}
