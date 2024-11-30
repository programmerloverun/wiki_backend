package com.leo.wiki.mapper;

import com.leo.wiki.resp.StatisticResp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author leijiong
 * @version 1.0
 */
@Mapper
public interface EbookSnapshotMapperCust {
    void genSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}
