package com.cmcc.algo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

//import lombok.EqualsAndHashCode;
//import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author hjy
 * @since 2020-05-25
 */
//@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
@ApiModel
@Data
@Entity
@Table(name = "tb_dataset")
public class Dataset implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据ID
     */
    @Id
    @GeneratedValue
    @Column
    @ApiModelProperty(value = "自增id")
    private Short id;

    /**
     * 上传文件路径
     */
    @Column
    @ApiModelProperty(value = "文件名字")
    private String name;

    /**
     * 文件更新时间
     */
    @Column
    @ApiModelProperty(value = "文件更新时间")
    private Date updatedAt;

    /**
     * 数据大小
     */
    @Column
    @ApiModelProperty(value = "数据大小")
    private String size;

    /**
     * 数据行数
     */
    @Column
    @ApiModelProperty(value = "数据行数")
    private Integer rows;

    /**
     * 数据party
     */
    @Column
    @ApiModelProperty(value = "party id")
    private Integer partyId;
}
