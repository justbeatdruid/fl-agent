package com.cmcc.algo.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.cmcc.algo.common.exception.APIException;
import com.cmcc.algo.common.response.CommonResult;
import com.cmcc.algo.common.response.ResultCode;
import com.cmcc.algo.config.CommonConfig;
import com.cmcc.algo.service.IPredictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预测接口
 *
 * @author Hao Jinyao
 * @since  2020/05/26
 */
@Api(tags = "预测接口")
@RestController
@RequestMapping("/predict")
public class PredictController {
    @Autowired
    IPredictService predictService;

    /**
     * 提交预测任务接口
     *
     * @param federationUuid JSON格式,包含预测参数
     * @return
     */
    @ApiOperation(value = "提交预测任务", notes = "提交预测任务")
    @ApiImplicitParam(name = "federationUuid", value = "联邦UUID")
    @PostMapping(value = "/submit")
    public CommonResult submitPredictTask(@RequestBody String federationUuid){
        if (StrUtil.isBlank(federationUuid)) {
            throw new APIException(ResultCode.PARAMETER_CHECK_ERROR,"联邦UUID为空");
        }
        predictService.submitPredictTask(federationUuid);
        return CommonResult.success();
    }

    @ApiOperation(value = "导出结果数据", notes = "导出结果数据")
    @ApiImplicitParam(name = "predictUuid", value = "预测记录UUID")
    @PostMapping(value = "/export")
    public CommonResult exportResult(@RequestBody String predictUuid){
        if (StrUtil.isBlank(predictUuid)) {
            throw new APIException(ResultCode.PARAMETER_CHECK_ERROR,"预测记录ID为空");
        }
        System.out.println(CommonConfig.filePath);
        predictService.exportResult(predictUuid);
        return CommonResult.success();
    }
}
