package com.leo.wiki.controller;

import com.leo.wiki.domain.Ebook;
import com.leo.wiki.req.EbookQueryReq;
import com.leo.wiki.req.EbookSaveReq;
import com.leo.wiki.resp.CommonResp;
import com.leo.wiki.service.EbookService;
import com.leo.wiki.util.CopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author leijiong
 * @version 1.0
 */
@RestController
@RequestMapping("/ebook")
@Validated
public class EbookController {

    @Autowired
    private EbookService EbookService;

    @GetMapping("/list")
    public CommonResp list(EbookQueryReq req) {
        CommonResp resp = new CommonResp();
        resp.setContent(EbookService.list(req));
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq req) {
        CommonResp resp = new CommonResp();
        req.setCover("https://leiboedu.oss-cn-beijing.aliyuncs.com/0921cf2d-fcc3-4a57-8c90-1f5d15b81e90.png");
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        EbookService.save(ebook);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp();
        EbookService.delete(id);
        return resp;
    }


}
