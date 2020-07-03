package com.cmcc.algo.service;

//import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcc.algo.entity.FederationDataset;


public interface IFederationDatasetService {
    boolean uploadDataset(String federationUuid, Short dataType, Integer partyId);
}
