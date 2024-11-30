package com.leo.wiki.controller;


import com.leo.wiki.req.CategoryQueryReq;
import com.leo.wiki.req.CategorySaveReq;
import com.leo.wiki.resp.CommonResp;
import com.leo.wiki.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author leijiong
 * @version 1.0
 */
@RestController
@RequestMapping("/category")
@Validated
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/all")
    public CommonResp category(CategoryQueryReq req) {
        CommonResp resp = new CommonResp();
        resp.setContent(categoryService.list(req));
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody CategorySaveReq req) {
        CommonResp resp = new CommonResp();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp();
        categoryService.delete(id);
        return resp;
    }


}
