package com.config;

import com.SearchApplication;
import com.config.mapping.Applications;
import com.config.mapping.Configure;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestConfigure {
    private Logger logger = LoggerFactory.getLogger(TestConfigure.class);

    @Autowired
   // @Qualifier("Configuration")
    Applications applications;
    @Test
    public void test(){
        logger.info("测试："+applications.getApplicationlist().get(0).getIndextypelist().get(0).getChildsqlmap().get(0).getName());

    }
}
