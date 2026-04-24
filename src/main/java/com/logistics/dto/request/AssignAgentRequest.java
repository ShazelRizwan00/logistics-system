package com.logistics.dto.request;

import jakarta.validation.constraints.NotNull;

/** POST /api/admin/shipments/{shipmentId}/assign */
public class AssignAgentRequest {

    @NotNull(message = "Agent ID is required")
    private Long agentId;

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
}
