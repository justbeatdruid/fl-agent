package com.cmcc.algo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcc.algo.entity.FederationDataset;
import com.cmcc.algo.mapper.FederationDatasetMapper;
import com.cmcc.algo.service.IFederationDatasetService;
import org.springframework.stereotype.Service;

@Service
public class FederationDatasetServiceImpl extends ServiceImpl<FederationDatasetMapper, FederationDataset> implements IFederationDatasetService {
}
