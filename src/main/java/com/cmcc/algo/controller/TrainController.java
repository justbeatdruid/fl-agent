package com.cmcc.algo.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cmcc.algo.common.exception.APIException;
import com.cmcc.algo.common.response.CommonResult;
import com.cmcc.algo.common.response.ResultCode;
import com.cmcc.algo.entity.Dataset;
import com.cmcc.algo.entity.FederationDataset;
import com.cmcc.algo.entity.FederationEntity;
import com.cmcc.algo.entity.UserFederation;
import com.cmcc.algo.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 训练接口
 *
 * @author Hao Jinyao
 * @since  2020/05/26
 */
@Api(tags = "训练任务接口")
@RestController
@RequestMapping("/train")
public class TrainController {
    @Autowired
    ITrainService trainService;

    /**
     * 提交训练任务接口
     *
     * @param federationUuid
     * @return
     */
    @ApiOperation(value = "提交任务", notes = "提交任务")
    @ApiImplicitParam(name = "federationUuid", value = "联邦UUID")
    @PostMapping(value = "/submit")
    public CommonResult submitTrainTask(@RequestBody String federationUuid){
        if (StrUtil.isBlank(federationUuid)) {
            throw new APIException(ResultCode.PARAMETER_CHECK_ERROR,"联邦UUID为空");
        }
        trainService.submitTrainTask(federationUuid);
        return CommonResult.success();
    }
}
