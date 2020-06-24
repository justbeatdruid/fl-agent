package com.cmcc.algo.controller;

import cn.hutool.json.JSONObject;
import com.cmcc.algo.common.response.CommonResult;
import com.cmcc.algo.service.IDatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据集接口
 *
 * @author Hao Jinyao
 * @since  2020/05/26
 */
@RestController
@RequestMapping("/dataset")
public class DatasetController {
    @Autowired
    IDatasetService datasetService;

    /**
     * 上传接口
     *
     * @param federationUuid JSON格式,包含federationUuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/upload")
    public CommonResult upload(@RequestBody String federationUuid){
        // TODO 根据datasetId得到path和生成table_name、namespace的依据（federation）
        return null;
    }
}
