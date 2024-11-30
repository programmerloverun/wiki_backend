package com.leo.wiki.controller;

import com.leo.wiki.resp.CommonResp;
import com.leo.wiki.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leijiong
 * @version 1.0
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService DemoService;

    @RequestMapping("/list")
    public CommonResp Demo() {
        CommonResp commonResp = new CommonResp();
        commonResp.setContent(DemoService.list());
        return commonResp;
    }


}
