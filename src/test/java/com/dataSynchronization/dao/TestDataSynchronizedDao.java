package com.dataSynchronization.dao;

import com.SearchApplication;
import com.config.ConfigEngine;
import com.config.TestConfigure;
import com.config.mapping.IndexType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestDataSynchronizedDao {
    private Logger logger = LoggerFactory.getLogger(TestConfigure.class);
    @Autowired
    private ConfigEngine configEngine;
    @Autowired
    private DataSynchronizedDao dataSynchronizedDao;

    @Test
    public void test(){
        String indexName = "policyindex_index";
        configEngine.setDataSource(indexName);
        List<IndexType> indexTypeList = configEngine.getIndexTypeByIndexName(indexName);
        //所有主键
        String primarykeysql = indexTypeList.get(0).getPrimarykeysql();
        List<String> list = dataSynchronizedDao.queryAllPrimaryKey(primarykeysql);
        logger.info("测试"+list.size());
      /* //查询主表
        String primarysql = indexTypeList.get(0).getPrimarysql();
        primarysql = primarysql.replace("${primarykey}",list.get(0));
        logger.info("测试"+primarysql);
        Map<String, Object> map =  dataSynchronizedDao.queryUniqueData(primarysql);
        logger.info("测试"+map.toString());*/


    }
}

