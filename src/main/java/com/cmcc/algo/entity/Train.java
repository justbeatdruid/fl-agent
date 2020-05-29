package com.cmcc.algo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
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
    private Integer id;

    /**
     * 联邦ID
     */
    private String federationId;

    /**
     * 训练状态(0:等待，1:就绪，2:运行中，3:成功，4:失败)
     */
    private Boolean status;

    /**
     * 训练详情URL
     */
    private String jobUrl;

    /**
     * 训练开始时间
     */
    private LocalDateTime startTime;

    /**
     * 训练耗时
     */
    private LocalDateTime duration;

    /**
     * AUC值
     */
    @TableField("AUC")
    private Float auc;

    /**
     * 准确率
     */
    private Float accuracy;

    /**
     * 算法模型ID
     */
    private Integer algorithmId;

    /**
     * 训练参数（包括数据与运行时参数）
     */
    private String trainParam;

    /**
     * 输出模型
     */
    private String model;


}
