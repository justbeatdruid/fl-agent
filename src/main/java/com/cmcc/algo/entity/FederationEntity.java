package com.cmcc.algo.entity;

import com.cmcc.algo.common.annotation.CheckRange;
import com.cmcc.algo.common.validator.group.AddGroup;
import com.cmcc.algo.common.validator.group.UpdateGroup;

import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;
/**
 * <p>
 * 联邦信息表
 * </p>
 *
 * @author hjy
 * @since 2020-05-25
 */
//@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
@Data
@Entity
@Table(name = "tb_federation")
public class FederationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联邦ID
     */
    @Id
    @GeneratedValue
    @Column
    private Short id;

    /**
     * 联邦名称
     */
    @NotBlank(message="名字不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Column(length = 32, unique = true, nullable = false)
    private String name;

    /**
     *联邦唯一ID
     */
    @NotBlank(message="uuid不能为空")
    @Column(length = 32, unique = true, nullable = false)
    private String uuid;

    /**
     * 联邦创建时间
     */
    @Column(nullable = false)
    private Date createdAt;

    /**
     * 联邦类型(0:横向联邦，1:纵向联邦)
     */
    @Column(nullable = false)
    private Boolean type;

    /**
     * 联邦描述
     */
    @Column
    private String description;

    /**
     * 联邦所有者
     */
    @Column
    private String guest;

    /**
     * 联邦参与者列表
     */
    @Column
    private String hosts;

    /**
     * 联邦状态(0:等待，1:就绪，2:运行中，3:成功，4:失败)
     */
    @CheckRange(values = {0,1,2,3,4})
    @Column
    private Integer status;

    /**
     * 数据格式
     */
    @Column(length = 65536)
    private String dataFormat;

    /**
     * 算法ID
     */
    @Column
    private Integer algorithmId;

    /**
     * 联邦参数（同训练参数）
     */
    @Column(length = 65536)
    private String param;

    /**
     * UserCount
     */
    @Column
    private Short userCount;
}
