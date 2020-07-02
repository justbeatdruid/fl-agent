package com.cmcc.algo.entity;

import lombok.Data;

@Data
public class FedPredict {
    private String federationUuid;

    private FederationEntity federationEntity;

    private Predict predict;
}
