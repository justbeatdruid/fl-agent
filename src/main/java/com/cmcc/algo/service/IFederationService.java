package com.cmcc.algo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcc.algo.entity.FederationEntity;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联邦信息表 服务类
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
 */
public interface IFederationService {

    List<FederationEntity> queryFederations(Map<String, Object> params, String userId);

    List<FederationEntity> findListByGuest(String guest);

    FederationEntity getOne(String uuid);

    FederationEntity userCountIncrease(String uuid);

    FederationEntity userCountDecrease(String uuid);
}
