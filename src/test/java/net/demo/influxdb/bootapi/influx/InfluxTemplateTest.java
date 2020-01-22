package net.demo.influxdb.bootapi.influx;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.demo.influxdb.bootapi.ApplicationTests;
import net.demo.influxdb.bootapi.config.DemoInfluxTemplate;
import net.demo.influxdb.bootapi.entity.SimpleObjectEntity;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
class InfluxTemplateTest extends ApplicationTests {

    @Autowired
    private DemoInfluxTemplate demoInfluxTemplate;


    /**
     * create database "demoDbTemplate" first
     */

    @Test
    void testBatchInsert() {
        // tags
        Map<String, String> tags = new HashMap<>();
        tags.put("tag01", "t01-v01");
        tags.put("tag02", "t02-v02");
        // fields
        Map<String, Object> fields = new HashMap<>();
        fields.put("location", "shanghai");
        fields.put("msg", new Gson().toJson(
                new SimpleObjectEntity(10001, "beijing", 2222, true)));
        // point
        Point point = demoInfluxTemplate.buildPoints("templateMs",
                System.currentTimeMillis(),
                tags,
                fields);
        // batchPoint
        BatchPoints batchPoints = BatchPoints.database("demoDbTemplate")
//                .retentionPolicy("2_hours") // must have this policy
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        batchPoints.point(point);
        demoInfluxTemplate.insertBatch(batchPoints);

        log.info("run success..");
    }

    @Test
    void testBatchListInsert() {

        // batchPoint
        BatchPoints batchPoints = BatchPoints.database("demoDbTemplate")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        //batchList
        List<String> batchData = new ArrayList<>();

        //---------------------------------------------------------------------
        // tags
        Map<String, String> tags = new HashMap<>();
        tags.put("tag01", "t01-v01-List");
        tags.put("tag02", "t02-v02-List");
        // fields
        Map<String, Object> fields = new HashMap<>();
        fields.put("location", "shanghai-pd");
        fields.put("msg", new Gson().toJson(
                new SimpleObjectEntity(22222, "beijing-cy", 77777, false)));
        // point
        Point point1 = demoInfluxTemplate.buildPoints("templateMs",
                System.currentTimeMillis(),
                tags,
                fields);
        batchPoints.point(point1);

        //---------------------------------------------------------------------
        // tags
        Map<String, String> tags2 = new HashMap<>();
        tags2.put("tag01", "t01-v01-List");
        tags2.put("tag02", "t02-v02-List");
        // fields
        Map<String, Object> fields2 = new HashMap<>();
        fields2.put("location", "shanghai-pd");
        fields2.put("msg", new Gson().toJson(
                new SimpleObjectEntity(22222, "beijing-cy", 77777, false)));
        // point
        Point point2 = demoInfluxTemplate.buildPoints("templateMs",
                System.currentTimeMillis(),
                tags2,
                fields2);
        batchPoints.point(point2);

        // change to string
        batchData.add(batchPoints.lineProtocol());

        demoInfluxTemplate.insertBatchList(
                "demoDbTemplate",
                "7_days",
                InfluxDB.ConsistencyLevel.ALL,
                batchData);

        log.info("run success..");
    }

    @Test
    void testQuery() {

        String sql = String.format("select time,location,msg from templateMs where tag01='%s'", "t01-v01-List");

        QueryResult queryResult = null;

        try {
            queryResult = demoInfluxTemplate.query(sql);
        } catch (Exception e) {
            throw new RuntimeException("query records error: {}", e.getCause());
        }

        List<JsonObject> queryResultList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(queryResult)) {
            QueryResult.Result oneResult = queryResult.getResults().get(0); // hardcode one sql
            if (!ObjectUtils.isEmpty(oneResult.getSeries())) {
                List<List<Object>> valueList = oneResult.getSeries().stream().map(QueryResult.Series::getValues)
                        .collect(Collectors.toList()).get(0);
                if (valueList != null && valueList.size() > 0) {
                    for (List<Object> value : valueList) {
                        JsonObject objResponse = new JsonObject();
                        objResponse.addProperty("time", ObjectUtils.isEmpty(value.get(0)) ? "" : value.get(0).toString());
                        objResponse.addProperty("location", ObjectUtils.isEmpty(value.get(1)) ? "" : value.get(1).toString());
                        objResponse.addProperty("msg", ObjectUtils.isEmpty(value.get(2)) ? "" : value.get(2).toString());
                        queryResultList.add(objResponse);
                    }
                }
            }
        }
        log.info("query result: {}", queryResultList);
    }

    @Test
    void testCreateRetentionPolicy() {
        demoInfluxTemplate.creatRetentionPolicy("2_hour", "2h", 1, false);
        log.info("success");
    }

    @Test
    void echoTimestamp(){
        System.out.println(System.currentTimeMillis());
    }
}
