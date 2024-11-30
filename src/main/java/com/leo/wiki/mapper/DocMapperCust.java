package com.leo.wiki.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author leijiong
 * @version 1.0
 */
@Mapper
public interface DocMapperCust {


    public void increaseViewCount(@Param("id") Long id);

    void increaseVoteCount(Long id);

    void updateEbookInfo();

//    public void increaseVoteCount(@Param("id") Long id);
//
//    public void updateEbookInfo();

}
