package com.cmcc.algo.service.impl;

import com.cmcc.algo.entity.User;
import com.cmcc.algo.mapper.UserMapper;
import com.cmcc.algo.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author hjy
 * @since 2020-05-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
