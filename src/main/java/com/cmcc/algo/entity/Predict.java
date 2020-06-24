package com.cmcc.algo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author hjy
 * @since 2020-05-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_predict")
public class Predict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预测记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 预测任务UUID
     */
    private String uuid;

    /**
     * 联邦UUID
     */
    private String federationUuid;

    /**
     * 训练记录UUID
     */
    private String trainUuid;

    private Integer algorithmId;

    /**
     * 预测状态(0:运行中，1:成功，2:失败)
     */
    private Integer status;

    /**
     * 预测详情URL
     */
    private String jobUrl;

    /**
     * 预测开始时间
     */
    private String startTime;

    /**
     * 预测耗时
     */
    private String duration;

    /**
     * 预测参数(包括模型、数据、运行时参数)
     */
    private String predictParam;

    /**
     * 预测任务ID（用于导出数据）
     */
    private Integer jobId;

    /**
     * 导出文件路径
     */
    private String outputPath;


}
