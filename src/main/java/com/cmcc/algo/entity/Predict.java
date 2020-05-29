package com.cmcc.algo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
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
@TableName("tb_predict")
public class Predict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预测记录ID
     */
    private Integer id;

    /**
     * 预测状态(0:等待，1:就绪，2:运行中，3:成功，4:失败)
     */
    private Boolean status;

    /**
     * 预测详情URL
     */
    private String jobUrl;

    /**
     * 预测开始时间
     */
    private LocalDateTime startTime;

    /**
     * 预测耗时
     */
    private LocalDateTime duration;

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
