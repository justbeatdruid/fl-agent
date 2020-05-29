package com.cmcc.algo.service.impl;

import com.cmcc.algo.entity.Algorithm;
import com.cmcc.algo.mapper.AlgorithmMapper;
import com.cmcc.algo.service.IAlgorithmService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 算法信息表 服务实现类
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
 */
@Service
public class AlgorithmServiceImpl extends ServiceImpl<AlgorithmMapper, Algorithm> implements IAlgorithmService {

}
