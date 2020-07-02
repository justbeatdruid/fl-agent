package com.cmcc.algo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmcc.algo.entity.FederationEntity;
import com.cmcc.algo.entity.UserFederation;
import com.cmcc.algo.mapper.FederationRepository;
import com.cmcc.algo.service.IFederationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcc.algo.service.IUserFederationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联邦信息表 服务实现类
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
 */
@Service
public class FederationServiceImpl implements IFederationService {
    @Autowired
    private FederationRepository federationRepository;
    @Autowired
    private IUserFederationService userFederationService;

    @Override
    public List<FederationEntity> queryFederations(Map<String, Object> params, String userId) {
        String name = (String) params.get("name");
        Boolean pri = Boolean.parseBoolean((String) params.get("private"));
        List<String> uuidList = new ArrayList<String>();
        if (pri) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("user_id", userId);
            List<UserFederation> userFederationList = userFederationService.list(queryWrapper);
            uuidList = new ArrayList<String>(userFederationList.size());
            for ( UserFederation userFederation : userFederationList ) {
                uuidList.add(userFederation.getFederationUUid());
            }
        }
        if (StrUtil.isNotBlank(name) && !pri) {
            return federationRepository.findByNameLike('%' + name + '%');
        }
        if (StrUtil.isNotBlank(name) && pri) {
            return federationRepository.findByNameLikeAndUuidIn('%' + name + '%', uuidList);
        }
        if (pri) {
            return federationRepository.findByUuidIn(uuidList);
        }
        return federationRepository.findAll();
    }

    @Override
    public List<FederationEntity> findListByGuest(String guest) {
        if (StrUtil.isNotBlank(guest)) {
            return federationRepository.findByGuest(guest);
        }
        return federationRepository.findAll();
    }

    @Override
    public FederationEntity getOne(String uuid) {
        return federationRepository.findByUuid(uuid);
    }

    @Override
    public FederationEntity userCountIncrease(String uuid) {
        FederationEntity federation = federationRepository.findByUuid(uuid);
        federation.setUserCount(new Short((short) (federation.getUserCount() + 1)));
        federationRepository.save(federation);
        return federation;
    }

    @Override
    public FederationEntity userCountDecrease(String uuid) {
        FederationEntity federation = federationRepository.findByUuid(uuid);
        federation.setUserCount(new Short((short) (federation.getUserCount() - 1)));
        federationRepository.save(federation);
        return federation;
    }
}
