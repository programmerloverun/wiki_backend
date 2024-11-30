package com.leo.wiki.controller;

import com.alibaba.fastjson.JSON;
import com.leo.wiki.domain.User;
import com.leo.wiki.req.UserLoginReq;
import com.leo.wiki.req.UserQueryReq;
import com.leo.wiki.req.UserSaveReq;
import com.leo.wiki.resp.CommonResp;
import com.leo.wiki.resp.UserLoginResp;
import com.leo.wiki.service.UserService;
import com.leo.wiki.util.CopyUtil;
import com.leo.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;


/**
 * @author leijiong
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SnowFlake snowFlake;

    @GetMapping("/list")
    public CommonResp list(UserQueryReq req) {
        CommonResp resp = new CommonResp();
        resp.setContent(userService.list(req));
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody UserSaveReq req) {
        CommonResp resp = new CommonResp();
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        User user = CopyUtil.copy(req, User.class);
        userService.save(user);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp();
        userService.delete(id);
        return resp;
    }


    @PostMapping("/reset-password")
    public CommonResp resetPassword(@RequestBody UserSaveReq req) {

        CommonResp resp = new CommonResp();
        User user = CopyUtil.copy(req, User.class);
        userService.resetPassword(user);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@RequestBody UserLoginReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp();
        UserLoginResp userLoginResp = userService.login(req);
        String token = snowFlake.nextId() + "";
        userLoginResp.setToken(token);
        redisTemplate.opsForValue().set(token, JSON.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);
        resp.setContent(userLoginResp);
        return resp;
    }

    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp();
        redisTemplate.delete(token);
        return resp;

    }


}
