package com.cmcc.algo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcc.algo.entity.FederationDataset;


public interface IFederationDatasetService extends IService<FederationDataset> {
    boolean uploadDataset(String federationUuid, Integer dataType, Integer partyId);
}
