package com.cmcc.algo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cmcc.algo.common.exception.APIException;
import com.cmcc.algo.common.response.ResultCode;
import com.cmcc.algo.config.FateFlowConfig;
import com.cmcc.algo.entity.*;
import com.cmcc.algo.mapper.TrainMapper;
import com.cmcc.algo.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcc.algo.util.TemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
 */
@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements ITrainService {
    @Autowired
    IDatasetService datasetService;

    @Autowired
    IFederationService federationService;

    @Autowired
    IUserFederationService userFederationService;

    @Autowired
    IFederationDatasetService federationDatasetService;

    @Autowired
    IAlgorithmService algorithmService;

    @Autowired
    FateFlowConfig fateFlowConfig;

    @Override
    public Boolean submitTrainTask(String federationUuid) {
        FederationEntity federation = federationService.getOne(Wrappers.<FederationEntity>lambdaQuery().eq(FederationEntity::getUuid, federationUuid));
        Algorithm algorithm = algorithmService.getOne(Wrappers.<Algorithm>lambdaQuery().eq(Algorithm::getId, federation.getAlgorithmId()));

        String label = Optional.ofNullable(JSONUtil.parseObj(federation.getDataFormat()).getStr("label")).orElseThrow(()->new APIException(ResultCode.NOT_FOUND, "数据标签丢失"));
        Map<String, Object> algorithmParam = BeanUtil.beanToMap(JSONUtil.parseObj(federation.getParam()));

        List<UserFederation> usList = userFederationService.list(Wrappers.<UserFederation>lambdaQuery().eq(UserFederation::getFederationUUid, federationUuid));
        Integer guestId = null;
        List<Integer> hostIdList = new ArrayList<>();
        for (UserFederation userFederation : usList) {
            if (userFederation.getStatus().equals("0")) {
                hostIdList.add(userFederation.getUserId());
                continue;
            }
            if (userFederation.getStatus().equals("1")) {
                guestId = userFederation.getId();
                continue;
            }
        }
        if (guestId == null) {
            throw new APIException(ResultCode.NOT_FOUND, "联邦创建者丢失");
        }
        algorithmParam.put("guest_party_id", guestId);
        algorithmParam.put("host_party_id_list", hostIdList);
        algorithmParam.put("federation_id", federationUuid);
        algorithmParam.put("data_type", 0);
        algorithmParam.put("label_name", label);

        String fateUrl = "http://" + fateFlowConfig.getHost() + ":" + fateFlowConfig.getPort();
//        FederationDataset guestDataset = federationDatasetService.getOne(Wrappers.<FederationDataset>lambdaQuery()
//                .eq(FederationDataset::getFederationUuid, federationUuid)
//                .eq(FederationDataset::getType, 0)
//                .eq(FederationDataset::getPartyId, guestId));
//        String guestTableName = "table_" + guestId + "_" + guestDataset.getType();
//        String guestNamespace = "namespace_" + federationUuid;
//
//        List<FederationDataset> hostDatasetList = federationDatasetService.list(Wrappers.<FederationDataset>lambdaQuery()
//                .eq(FederationDataset::getFederationUuid, federationUuid)
//                .eq(FederationDataset::getType, 0)
//                .in(FederationDataset::getPartyId, hostIdList));
//        List<String> hostTableNameList = hostDatasetList.stream().map(x-> "table_" + x.getPartyId() + "_" + x.getType()).collect(Collectors.toList());
//        String hostNamespace = "namespace_" + federationUuid;

        switch (algorithm.getAlgorithmName()){
            case "HeteroLR":
                String heteroLrDsl = TemplateUtils.useTemplate(null, "HeteroLR_dsl.ftl");
                String heteroLrConf = TemplateUtils.useTemplate(algorithmParam, "HeteroLR_conf.ftl");
                JSONObject fateFlowRequest = new JSONObject();
                fateFlowRequest.putOnce("job_dsl", heteroLrDsl).putOnce("job_runtime_conf", heteroLrConf);
                String fateFlowResponse = HttpUtil.post(fateUrl + "/v1/job/submit", JSONUtil.toJsonStr(fateFlowRequest));
                // TODO 根据返回结果判断开启与否，拿到url等结果保存到数据库中，修改多个表状态等
                return true;
        }

        return false;
    }
}
