package com.cmcc.algo.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
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

    @Autowired
    CommonConfig commonConfig;

    @Override
    public boolean uploadDataset(String federationUuid, Short dataType, Integer partyId) {
        log.info("begin to generate upload request json");
        FederationDataset dataset = federationDatasetRepository.findByFederationUuidAndPartyIdAndType(federationUuid, partyId, dataType);

        Map<String, Object> rMap = new HashMap<>();
        // 生成模板，写文件
        try {
            rMap.put("file", commonConfig.getFilePath() + "/" + dataset.getName());
            rMap.put("head", 1);
            rMap.put("partition", 10);
            rMap.put("work_mode", 1);
            rMap.put("table_name", "table_" + partyId + "_" + dataType);
            rMap.put("namespace", "namespace_" + federationUuid);

            log.info("begin to use template");
            String predictConf = TemplateUtils.useTemplate(rMap, "upload.ftl");

            String uploadJsonPath = commonConfig.getFilePath() + "/" + partyId + "_upload.json";
            log.info("begin to create file");
            File uploadJson = FileUtil.exist(uploadJsonPath) ? FileUtil.file(uploadJsonPath) : FileUtil.touch(uploadJsonPath);
            log.info("begin to write str in file");
            FileUtil.writeString(predictConf, uploadJson, Charset.defaultCharset());

            log.info("begin to exec upload command");
            String[] cmd = {commonConfig.getPythonPath(), commonConfig.getCliPyPath(), "-f", "upload", "-c", uploadJsonPath};
            String response = RuntimeUtil.execForStr(cmd);

            if (JSONUtil.parseObj(response).getInt("retcode") != 0) {
                throw new APIException(ResultCode.NOT_FOUND, "上传失败");
            }
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            log.warn("error is {}", baos.toString());
        }


//        String uploadURL = "http://" + FateFlowConfig.fateFlowHost + ":" + FateFlowConfig.fateFlowPort + URLConstant.DATA_UPLOAD_URL;
//        String response = HttpUtil.post(uploadURL, JSONUtil.toJsonStr(requestMap));


        return true;
    }
}
