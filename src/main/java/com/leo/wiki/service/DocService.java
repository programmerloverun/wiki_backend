package com.leo.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leo.wiki.domain.Content;
import com.leo.wiki.domain.Doc;
import com.leo.wiki.domain.DocExample;
import com.leo.wiki.exception.BusinessException;
import com.leo.wiki.exception.BusinessExceptionCode;
import com.leo.wiki.mapper.ContentMapper;
import com.leo.wiki.mapper.DocMapper;
import com.leo.wiki.mapper.DocMapperCust;
import com.leo.wiki.req.DocQueryReq;
import com.leo.wiki.req.DocSaveReq;
import com.leo.wiki.resp.DocQueryResp;
import com.leo.wiki.resp.PageResp;
import com.leo.wiki.util.CopyUtil;
import com.leo.wiki.util.RedisUtil;
import com.leo.wiki.util.RequestContext;
import com.leo.wiki.util.SnowFlake;
import com.leo.wiki.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private DocMapperCust docMapperCust;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WebSocketServer webSocketServer;

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> DocList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(DocList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(DocList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 保存
     */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            if (doc.getEbookId() == null) {
                doc.setEbookId(0L);
            }
            docMapper.insert(doc);
            content.setId(doc.getId());
            contentMapper.insert(content);

        } else {
            // 更新
            docMapper.updateByPrimaryKey(doc);
            contentMapper.updateByPrimaryKeyWithBLOBs(content);
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }


    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        docMapperCust.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        }
        return content.getContent();
    }

    public void vote(Long id) {
        String key = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + key, 3600 * 24)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
        // 推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        webSocketServer.sendInfo(docDb.getName() + " 被点赞！");


    }

    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }
}
