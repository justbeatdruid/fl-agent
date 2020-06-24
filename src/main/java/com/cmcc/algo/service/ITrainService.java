package com.cmcc.algo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcc.algo.entity.Train;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
 */
public interface ITrainService extends IService<Train> {
    Boolean submitTrainTask(String federationUuid);
}
