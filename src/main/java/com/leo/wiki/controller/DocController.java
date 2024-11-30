package com.leo.wiki.controller;


import com.leo.wiki.req.DocQueryReq;
import com.leo.wiki.req.DocSaveReq;

import com.leo.wiki.resp.CommonResp;
import com.leo.wiki.service.DocService;


import com.leo.wiki.util.RedisUtil;
import com.leo.wiki.util.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author leijiong
 * @version 1.0
 */
@RestController
@RequestMapping("/doc")
@Validated
public class DocController {

    @Autowired
    private DocService docService;



    @GetMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId) {
        System.out.println("ebookId:" + ebookId);
        CommonResp resp = new CommonResp();
        resp.setContent(docService.all(ebookId));
        return resp;
    }

    @RequestMapping("/list")
    public CommonResp doc(DocQueryReq req) {
        CommonResp resp = new CommonResp();
        resp.setContent(docService.list(req));
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody DocSaveReq req) {
        CommonResp resp = new CommonResp();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{idStr}")
    public CommonResp delete(@PathVariable String idStr) {
        CommonResp resp = new CommonResp();
        List<String> list = Arrays.asList(idStr.split(","));
        docService.delete(list);
        return resp;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }

    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        docService.vote(id);
        return new CommonResp();
    }


}
