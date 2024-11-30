package com.leo.wiki.service;

import com.leo.wiki.domain.Demo;
import com.leo.wiki.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author leijiong
 * @version 1.0
 */
@Service
public class DemoService {

    @Autowired
    private DemoMapper DemoMapper;

    public List<Demo> list() {
        return DemoMapper.selectByExample(null);
    }
}
