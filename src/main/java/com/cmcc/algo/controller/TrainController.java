package com.cmcc.algo.controller;

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
    @PostMapping(value = "/submit")
    public CommonResult submitTrainTask(@RequestBody String federationUuid){





        // TODO 根据参数组装相应json，通过rest请求fate-flow，然后根据返回结果（新增）修改训练记录表
        return CommonResult.success(null);
    }

//    @RequestBody
//    @PostMapping(value = "/status/update")
//    public CommonResult updateTrainTaskStatus(@RequestBody JSONArray request){
//        // 对每一个记录id，调用相应fate-flow接口，得到是否执行到最后并成功，更新训练记录表状态
//        return null;
//    }
}
