package com.cmcc.algo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcc.algo.entity.Predict;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
 */
public interface IPredictService extends IService<Predict> {
    Boolean submitPredictTask(String federationUuid);

    Boolean exportResult(String predictUuid);
}
