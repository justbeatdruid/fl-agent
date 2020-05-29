package com.cmcc.algo.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.cmcc.algo.common.CommonResult;
import com.cmcc.algo.service.ITrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 训练接口
 *
 * @author Hao Jinyao
 * @since  2020/05/26
 */
@RestController
@RequestMapping("/train")
public class TrainController {
    @Autowired
    ITrainService trainService;

    /**
     * 提交训练任务接口
     *
     * @param request JSON格式,包含训练记录id（先写数据库）|联邦训练参数、数据集参数等（需要联邦表保存，否则退出再进全部丢失）
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/submit")
    public CommonResult submitTrainTask(@RequestBody JSONObject request){
        // TODO 根据参数组装相应json，通过rest请求fate-flow，然后根据返回结果（新增）修改训练记录表
        return null;
    }

//    @RequestBody
//    @PostMapping(value = "/status/update")
//    public CommonResult updateTrainTaskStatus(@RequestBody JSONArray request){
//        // 对每一个记录id，调用相应fate-flow接口，得到是否执行到最后并成功，更新训练记录表状态
//        return null;
//    }
}
