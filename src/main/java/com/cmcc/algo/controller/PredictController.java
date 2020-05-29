package com.cmcc.algo.controller;

import cn.hutool.json.JSONObject;
import com.cmcc.algo.common.CommonResult;
import com.cmcc.algo.service.IPredictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预测接口
 *
 * @author Hao Jinyao
 * @since  2020/05/26
 */
@RestController
@RequestMapping("/predict")
public class PredictController {
    @Autowired
    IPredictService predictService;

    /**
     * 提交预测任务接口
     *
     * @param request JSON格式,包含预测参数
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/submit")
    public CommonResult submitPredictTask(@RequestBody JSONObject request){
        // TODO 根据参数组装相应json，通过rest请求fate-flow，然后根据返回结果（新增）修改预测记录表
        return null;
    }
}
