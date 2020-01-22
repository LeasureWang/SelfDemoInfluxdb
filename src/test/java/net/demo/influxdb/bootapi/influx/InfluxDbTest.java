package net.demo.influxdb.bootapi.influx;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.demo.influxdb.bootapi.ApplicationTests;
import net.demo.influxdb.bootapi.entity.SimpleObjectEntity;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
class InfluxDbTest extends ApplicationTests {

    private static InfluxDB influxDB = null;

    private static Gson gson = new Gson();

    @BeforeAll
    static void connection() {
        String url = "http://192.168.145.30:8086";
        String username = "demoAdmin";
        String password = "pwd";
        try {
            influxDB = InfluxDBFactory.connect(url, username, password);
        } catch (Exception e
        ) {
            log.error("connection error: {}", e.getMessage());
        }
    }

    @AfterAll
    static void close() {
        if (!ObjectUtils.isEmpty(influxDB)) influxDB.close();
    }


    @Test
    void testCrete() {
        if (ObjectUtils.isEmpty(influxDB)) log.error("influxDb non connection..");

        influxDB.setDatabase("demoDb");

        influxDB.write(Point.measurement("demoMs")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("tag01", "tag01-value")
                .addField("remark", "remark-value")
                .addField("objectF", gson.toJson(
                        new SimpleObjectEntity(10001, "beijing", 2222, true)))
                .build());

        log.info("write success");
    }

    @Test
    void testQuery() {
        if (ObjectUtils.isEmpty(influxDB)) log.error("influxDb non connection..");

        String sql = "select * from demoMs";

        Query query = new Query(sql, "demoDb");

        QueryResult queryResult = influxDB.query(query);

        List<QueryResult.Series> results =
                queryResult.getResults().get(0).getSeries();

        for (QueryResult.Series temp:results){
            log.info("name: {} ",temp.getName());
            log.info("columns: {}",temp.getColumns());
            log.info("values: {}",temp.getValues());
        }

    }
}
