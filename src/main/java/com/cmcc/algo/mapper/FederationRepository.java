package com.cmcc.algo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmcc.algo.entity.FederationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 联邦信息表 Mapper 接口
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
 */
@Repository
public interface FederationRepository extends JpaRepository<FederationEntity, Long> {
    FederationEntity findByUuid(String uuid);

    void deleteByUuid(String uuid);

    List<FederationEntity> findByGuest(String guest);

    List<FederationEntity> findByNameLike(String name);

    List<FederationEntity> findByName(String name);

    List<FederationEntity> findByUuidIn(List<String> uuids);

    List<FederationEntity> findByNameLikeAndUuidIn(String name, List<String> uuids);
}
