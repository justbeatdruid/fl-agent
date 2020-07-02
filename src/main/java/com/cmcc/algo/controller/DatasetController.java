package com.cmcc.algo.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cmcc.algo.common.exception.APIException;
import com.cmcc.algo.common.response.CommonResult;
import com.cmcc.algo.common.response.ResultCode;
import com.cmcc.algo.entity.FederationDataset;
import com.cmcc.algo.service.IDatasetService;
import com.cmcc.algo.service.IFederationDatasetService;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 数据集接口
 *
 * @author Hao Jinyao
 * @since 2020/05/26
 */
@Api(tags = "数据集接口")
@RestController
@RequestMapping("/dataset")
public class DatasetController {
    @Autowired
    IFederationDatasetService federationDatasetService;

    /**
     * 上传接口
     *
     * @param request JSON格式,包含federationUuid, dataType, partyId
     * @return
     */
    @ApiOperation(value = "上传数据", notes = "上传数据")
    @ApiImplicitParam(name = "request", value = "请求JSON,包含三个字段'federation','dataType','partyId',都不可为空")
    @PostMapping(value = "/upload")
    public CommonResult upload(@RequestBody String request) {
        String federationUuid = JSONUtil.parseObj(request).getStr("federationUuid");
        Integer dataType = JSONUtil.parseObj(request).getInt("dataType");
        Integer partyId = JSONUtil.parseObj(request).getInt("partyId");

        Preconditions.checkArgument(ObjectUtil.isAllNotEmpty(federationUuid, dataType, partyId));

        federationDatasetService.uploadDataset(federationUuid, dataType, partyId);
        return CommonResult.success("上传成功");
    }
}
