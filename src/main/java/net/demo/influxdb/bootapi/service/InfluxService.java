package net.demo.influxdb.bootapi.service;

import lombok.extern.slf4j.Slf4j;
import net.demo.influxdb.bootapi.config.DemoInfluxTemplate;
import net.demo.influxdb.bootapi.entity.DemoInfluxEntity;
import net.demo.influxdb.bootapi.reqeust.DemoInfluxRequestBody;
import net.demo.influxdb.bootapi.response.CommonResponse;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class InfluxService {

    @Autowired
    private DemoInfluxTemplate demoInfluxTemplate;

    public CommonResponse insert(DemoInfluxRequestBody requestBody) {

        DemoInfluxEntity entity = new DemoInfluxEntity();

        entity.setId(Long.toString(requestBody.getId()))
                .setRefNumber(String.valueOf(requestBody.getRefNumber()))
                .setLocation(requestBody.getLocation())
                .setMessage(requestBody.getMessage().toJSONString())
                .setStatus(String.valueOf(requestBody.isStatus()));

        Point point = Point.measurementByPOJO(DemoInfluxEntity.class)
                .addFieldsFromPOJO(entity)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();

        BatchPoints batchPoints = BatchPoints.database("demoDbTemplate")
                .consistency(InfluxDB.ConsistencyLevel.ALL).build();

        batchPoints.point(point);

        try {
            demoInfluxTemplate.insertBatch(batchPoints);
        } catch (Exception e) {
            log.error("insert influx error: {}", e.getMessage());
        }

        return CommonResponse.setSuccess();
    }
}
