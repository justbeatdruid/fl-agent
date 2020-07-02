package com.cmcc.algo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("tb_train")
public class Train implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 训练任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 训练记录UUID
     */
    private String uuid;

    /**
     * 联邦UUID
     */
    private String federationUuid;

    /**
     * 训练状态(0:运行中，1:成功，2:失败)
     */
    private Integer status;

    /**
     * 训练详情URL
     */
    private String jobUrl;

    /**
     * 训练开始时间
     */
    private String startTime;

    private Long startTimestamp;
    /**
     * 训练耗时
     */
    private String duration;

    /**
     * AUC值
     */
    @TableField("AUC")
    private Float auc;
    /**
     * KS值
     */
    @TableField("KS")
    private Float ks;
    /**
     * 准确率
     */
    private Float accuracy;

    /**
     * 算法模型ID
     */
    private Integer algorithmId;

    public String jobId;
    /**
     * 训练参数（包括数据与运行时参数）
     */
    private String trainParam;

    /**
     * 输出模型
     */
    private String model;


}
