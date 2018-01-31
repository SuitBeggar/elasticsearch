package com.dataSynchronization.api;

import com.dataSynchronization.dynamictask.DynamicTaskEngine;
import com.dataSynchronization.service.DataSynchronizedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dataSynchronized")
public class DataSynchronizedApi {

    @Autowired
    private DataSynchronizedService dataSynchronizedService;


    @Autowired
    private DynamicTaskEngine dynamicTaskEngine;

    /**
     * 全量数据同步
     * @param indexName
     */
    @RequestMapping("/allDataSynchronized")
    public void allDataSynchronized(@RequestParam(value="agencyId", required=false)String indexName){
        dataSynchronizedService.synchronizeData(indexName);
    }

    /**
     * 增量数据同步
     * @param indexName
     */
    @RequestMapping("/incrementDataSynchronized")
    public void incrementDataSynchronized(@RequestParam(value="agencyId", required=false)String indexName){
        dynamicTaskEngine.createIncrementSyncTask(indexName);
    }


}
