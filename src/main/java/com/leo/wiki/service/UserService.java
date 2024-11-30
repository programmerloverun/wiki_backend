package com.leo.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leo.wiki.domain.User;
import com.leo.wiki.domain.UserExample;


import com.leo.wiki.exception.BusinessException;
import com.leo.wiki.exception.BusinessExceptionCode;
import com.leo.wiki.mapper.UserMapper;

import com.leo.wiki.req.UserLoginReq;
import com.leo.wiki.req.UserQueryReq;
import com.leo.wiki.resp.PageResp;
import com.leo.wiki.resp.UserLoginResp;
import com.leo.wiki.resp.UserQueryResp;
import com.leo.wiki.util.CopyUtil;
import com.leo.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author leijiong
 * @version 1.0
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();

        if(!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }

        // 设置分页参数
        PageHelper.startPage(req.getPage(), req.getSize());

        // 根据Example对象执行查询并返回结果列表
        List<User> list = userMapper.selectByExample(userExample);

        // 使用PageInfo封装分页信息，注意这里应该传入查询的结果list
        PageInfo<User> pageInfo = new PageInfo<>(list);

        // 将user列表转换为userResp列表
        List<UserQueryResp> userRespList = CopyUtil.copyList(list, UserQueryResp.class);

        // 创建PageResp对象并设置数据
        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setList(userRespList);
        pageResp.setTotal(pageInfo.getTotal());

        return pageResp;
    }

    public void save(User user) {
        if (user.getId() == null) {
            User userDB = selectByLoginName(user.getLoginName());
            if(ObjectUtils.isEmpty(userDB)) {
                // 新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                // 用户名已经存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        } else {
            // 更新
            user.setLoginName(null);
            user.setPassword(null);
            userMapper.updateByPrimaryKeySelective(user);
        }

    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String loginName) {

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if (!userList.isEmpty()) {
            return userList.get(0);
        }
        return null;
    }

    public void resetPassword(User user) {
        System.out.println(user);
        userMapper.updateByPrimaryKeySelective(user);
    }

    public UserLoginResp login(UserLoginReq req) {
        User userDB = selectByLoginName(req.getLoginName());
        if(ObjectUtils.isEmpty(userDB)) {
            // 用户名不存在
            throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
        } else {

            if(userDB.getPassword().equals(req.getPassword())) {
                // 登录成功
                UserLoginResp resp = CopyUtil.copy(userDB, UserLoginResp.class);
                return resp;
            } else {
                // 密码错误
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        }
    }
}
