package com.cmcc.algo.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cmcc.algo.common.exception.APIException;
import com.cmcc.algo.common.response.ResultCode;
import com.cmcc.algo.config.CommonConfig;
import com.cmcc.algo.config.FateFlowConfig;
import com.cmcc.algo.constant.URLConstant;
import com.cmcc.algo.entity.FederationDataset;
import com.cmcc.algo.mapper.FederationDatasetRepository;
import com.cmcc.algo.service.IFederationDatasetService;
import com.cmcc.algo.service.IUserFederationService;
import com.cmcc.algo.util.TemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class FederationDatasetServiceImpl implements IFederationDatasetService {
    @Autowired
    FederationDatasetRepository federationDatasetRepository;

    @Autowired
    IUserFederationService userFederationService;

    @Override
    public boolean uploadDataset(String federationUuid, Short dataType, Integer partyId) {
        log.info("begin to generate upload request json");
        FederationDataset dataset = federationDatasetRepository.findByFederationUuidAndPartyIdAndType(federationUuid, partyId, dataType);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("file", CommonConfig.filePath + dataset.getName());
        requestMap.put("head", 1);
        requestMap.put("partition", 10);
        requestMap.put("work_mode", 1);
        requestMap.put("table_name", "table_" + partyId + "_" + dataType);
        requestMap.put("namespace", "namespace_" + federationUuid);

        // 生成模板，写文件
        String predictConf = TemplateUtils.useTemplate(requestMap, "upload.ftl");
        log.info("request is \n{}", predictConf);

        String uploadJsonPath = CommonConfig.filePath + "/" + partyId + "_upload.json";
        log.info("begin to create file");
        File uploadJson = FileUtil.exist(uploadJsonPath) ? FileUtil.file(uploadJsonPath) : FileUtil.touch(uploadJsonPath);
        log.info("begin to write str in file");
        FileUtil.writeString(predictConf, uploadJson, Charset.defaultCharset());

        log.info("begin to exec upload command");
        String[] cmd = {CommonConfig.pythonPath, CommonConfig.cliPyPath, "-f", "upload", "-c", uploadJsonPath};
        String response = RuntimeUtil.execForStr(cmd);

//        String uploadURL = "http://" + FateFlowConfig.fateFlowHost + ":" + FateFlowConfig.fateFlowPort + URLConstant.DATA_UPLOAD_URL;
//        String response = HttpUtil.post(uploadURL, JSONUtil.toJsonStr(requestMap));

        if (JSONUtil.parseObj(response).getInt("retcode") != 0) {
            throw new APIException(ResultCode.NOT_FOUND,"上传失败");
        }

        return true;
    }
}
