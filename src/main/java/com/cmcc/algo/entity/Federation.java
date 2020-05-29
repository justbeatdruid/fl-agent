package com.cmcc.algo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 联邦信息表
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_federation")
public class Federation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联邦ID
     */
    private String id;

    /**
     * 联邦名称
     */
    private String name;

    /**
     * 联邦类型(0:横向联邦，1:纵向联邦)
     */
    private Boolean type;

    /**
     * 联邦描述
     */
    private String desc;

    /**
     * 联邦所有者
     */
    private String guest;

    /**
     * 联邦参与者列表
     */
    private String hosts;

    /**
     * 联邦状态(0:等待，1:就绪，2:运行中，3:成功，4:失败)
     */
    private Boolean status;

    /**
     * 数据格式
     */
    private String dataDesc;

    /**
     * 算法ID
     */
    private Integer algorithmId;

    /**
     * 联邦参数（同训练参数）
     */
    private String param;


}
