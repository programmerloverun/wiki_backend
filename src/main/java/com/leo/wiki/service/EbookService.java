package com.leo.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leo.wiki.domain.Ebook;
import com.leo.wiki.domain.EbookExample;
import com.leo.wiki.mapper.EbookMapper;
import com.leo.wiki.req.EbookQueryReq;
import com.leo.wiki.resp.EbookResp;
import com.leo.wiki.resp.PageResp;
import com.leo.wiki.util.CopyUtil;
import com.leo.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author leijiong
 * @version 1.0
 */
@Service
public class EbookService {

    @Autowired
    private EbookMapper ebookMapper;

    public PageResp<EbookResp> list(EbookQueryReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();

        if (req.getName() != null) {
            // 添加模糊查询条件
            criteria.andNameLike("%" + req.getName() + "%");
        }

        if(req.getCategoryId2() != null) {
            criteria.andCategory2IdEqualTo(req.getCategoryId2());
        }

        // 设置分页参数
        PageHelper.startPage(req.getPage(), req.getSize());

        // 根据Example对象执行查询并返回结果列表
        List<Ebook> list = ebookMapper.selectByExample(ebookExample);

        // 使用PageInfo封装分页信息，注意这里应该传入查询的结果list
        PageInfo<Ebook> pageInfo = new PageInfo<>(list);

        // 将Ebook列表转换为EbookResp列表
        List<EbookResp> ebookRespList = CopyUtil.copyList(list, EbookResp.class);

        // 创建PageResp对象并设置数据
        PageResp<EbookResp> pageResp = new PageResp<>();
        pageResp.setList(ebookRespList);
        pageResp.setTotal(pageInfo.getTotal());

        return pageResp;
    }

    public void save(Ebook ebook) {
        if (ebook.getId() == null) {
            // 新增
            SnowFlake snowFlake = new SnowFlake();
            long id = snowFlake.nextId();
            ebook.setId(id);
            ebook.setViewCount(0);
            ebook.setVoteCount(0);
            ebook.setDocCount(0);
            ebookMapper.insert(ebook);
        } else {
            // 更新
            ebookMapper.updateByPrimaryKey(ebook);
        }

    }

    public void delete(Long id) {
        ebookMapper.deleteByPrimaryKey(id);
    }
}
