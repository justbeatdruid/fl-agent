package com.cmcc.algo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("tb_dataset")
public class Dataset implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据ID
     */
    private Integer id;

    /**
     * 联邦ID
     */
    private String federationId;

    /**
     * 数据类型（0:训练，1:预测）
     */
    private Boolean type;

    /**
     * 用户名
     */
    private String username;

    /**
     * 上传文件路径
     */
    private String uploadPath;

    /**
     * 数据大小
     */
    private String size;

    /**
     * 数据状态（0:未完成，1:完成）
     */
    private Boolean status;

    /**
     * 数据标识（table_name和namespace）
     */
    private String dataIndicator;


}
