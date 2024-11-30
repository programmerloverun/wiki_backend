package com.leo.wiki.mapper;

import com.leo.wiki.domain.Test;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author leijiong
 * @version 1.0
 */
@Mapper
public interface TestMapper {

    List<Test>list();

}
