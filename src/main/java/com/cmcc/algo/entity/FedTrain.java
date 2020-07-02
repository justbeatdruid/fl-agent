package com.cmcc.algo.entity;

import lombok.Data;

@Data
public class FedTrain {
    private String federationUuid;

    private FederationEntity federationEntity;

    private Train train;
}
