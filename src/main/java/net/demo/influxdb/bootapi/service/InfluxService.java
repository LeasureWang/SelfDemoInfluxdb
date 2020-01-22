package net.demo.influxdb.bootapi.service;

import com.alibaba.fastjson.JSONObject;
import net.demo.influxdb.bootapi.config.DemoInfluxTemplate;
import net.demo.influxdb.bootapi.reqeust.DemoInfluxRequestBody;
import net.demo.influxdb.bootapi.response.CommonResponse;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class InfluxService {

    @Autowired
    private DemoInfluxTemplate demoInfluxTemplate;


    public CommonResponse insert(DemoInfluxRequestBody requestBody) {

        long id = requestBody.getId();
        String location = requestBody.getLocation();
        JSONObject message = requestBody.getMessage();
        int refNumber = requestBody.getRefNumber();
        boolean status = requestBody.isStatus();

        BatchPoints batchPoints = BatchPoints.database("demoDbTemplate")
                .consistency(InfluxDB.ConsistencyLevel.ALL).build();

        Map<String, String> tags = new HashMap<>();
        tags.put("location", location);
        tags.put("refNumber", String.valueOf(refNumber));

        Map<String, Object> fields = new HashMap<>();
        fields.put("id", id);
        fields.put("message", message.toJSONString());
        fields.put("status", status);

        Point point = Point.measurement("demoTemplateMs")
                .time(Long.parseLong("1577732860552"), TimeUnit.MILLISECONDS)
                .tag(tags)
                .fields(fields)
                .build();

        batchPoints.point(point);

        demoInfluxTemplate.insertBatch(batchPoints);

        return CommonResponse.setSuccess();
    }

    public CommonResponse insertBatchList(List<DemoInfluxRequestBody> request) {

        // batchPoint
        BatchPoints batchPoints = BatchPoints.database("demoDbTemplate")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        //batchList
        List<String> batchData = new ArrayList<>();

        for (DemoInfluxRequestBody req : request) {
            Map<String, String> tags = new HashMap<>();
            tags.put("location", req.getLocation());
            tags.put("refNumber", String.valueOf(req.getRefNumber()));
            Map<String, Object> fields = new HashMap<>();
            fields.put("id", req.getId());
            fields.put("message", req.getMessage().toJSONString());
            fields.put("status", req.isStatus());
            // point
            Point point = demoInfluxTemplate.buildPoints("demoTemplateMs",
                    System.currentTimeMillis(),
                    tags,
                    fields);
            batchPoints.point(point);
        }

        // change to string
        batchData.add(batchPoints.lineProtocol());

        demoInfluxTemplate.insertBatchList(
                "demoDbTemplate",
                "7_days",
                InfluxDB.ConsistencyLevel.ALL,
                batchData);

        return CommonResponse.setSuccess();
    }


    public List<JSONObject> query(long time) {

        final String MEASUREMENT = "demoTemplateMs";

        String sql = String.format("select * from %s where time=%d", MEASUREMENT, time);

        QueryResult queryResult = null;

        try {
            queryResult = demoInfluxTemplate.query(sql);
        } catch (Exception e) {
            throw new RuntimeException("query records error: {}", e.getCause());
        }

        List<JSONObject> queryResultList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(queryResult)) {
            QueryResult.Result oneResult = queryResult.getResults().get(0); // hardcode one sql
            if (!ObjectUtils.isEmpty(oneResult.getSeries())) {
                List<List<Object>> valueList = oneResult.getSeries().stream().map(QueryResult.Series::getValues)
                        .collect(Collectors.toList()).get(0);
                if (valueList != null && valueList.size() > 0) {
                    for (List<Object> value : valueList) {
                        JSONObject objResponse = new JSONObject();
                        objResponse.put("time", ObjectUtils.isEmpty(value.get(0)) ? "" : value.get(0).toString());
                        objResponse.put("id", ObjectUtils.isEmpty(value.get(1)) ? "" : value.get(1).toString());
                        objResponse.put("location", ObjectUtils.isEmpty(value.get(2)) ? "" : value.get(2).toString());
                        objResponse.put("message", ObjectUtils.isEmpty(value.get(3)) ?
                                new JSONObject() : JSONObject.parse(value.get(3).toString()));
                        objResponse.put("refNumber", ObjectUtils.isEmpty(value.get(4)) ? "" : value.get(4).toString());
                        objResponse.put("status", ObjectUtils.isEmpty(value.get(5)) ? "" : value.get(5).toString());
                        queryResultList.add(objResponse);
                    }
                }
            }
        }
        return queryResultList;
    }
}
