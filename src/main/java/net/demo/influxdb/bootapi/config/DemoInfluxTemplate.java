package net.demo.influxdb.bootapi.config;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DemoInfluxTemplate {

    private String username;
    private String password;
    private String connUrl;
    private String database;
    private String retentionPolicy;

    private InfluxDB influxDB;

    private final String POLICY = "autogen";

    public DemoInfluxTemplate() {

    }

    public DemoInfluxTemplate(String username,
                              String password,
                              String connUrl,
                              String database,
                              String retentionPolicy) {
        this.username = username;
        this.password = password;
        this.connUrl = connUrl;
        this.database = database;
        this.retentionPolicy = ObjectUtils.isEmpty(retentionPolicy) ? POLICY : retentionPolicy;

        buildInfluxConn();
    }

    private void buildInfluxConn() {
        if (ObjectUtils.isEmpty(influxDB)) {
            try {
                influxDB = InfluxDBFactory.connect(connUrl, username, password);
            } catch (Exception e) {
                log.error("influxDb connection error: {}", e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
        influxDB.setRetentionPolicy(retentionPolicy);
        influxDB.setLogLevel(InfluxDB.LogLevel.NONE);
    }


    public QueryResult query(String sql) {
        return influxDB.query(new Query(sql, database));
    }

    public Point buildPoints(String measurement,
                             long time,
                             Map<String, String> tags,
                             Map<String, Object> fields) {
        return Point.measurement(measurement)
                .time(time, TimeUnit.MILLISECONDS)
                .tag(tags)
                .fields(fields)
                .build();
    }

    public void insertBatch(BatchPoints batchPoints) {
        influxDB.write(batchPoints);
    }

    public void insertBatchList(final String database,
                                final String retentionPolicy,
                                final InfluxDB.ConsistencyLevel consistencyLevel,
                                final List<String> records) {
        influxDB.write(database, retentionPolicy, consistencyLevel, records);
    }

    public void creatRetentionPolicy(String policyName, String duration, int replication, Boolean isDefault) {
        String sql = String.format("create retention policy \"%s\" ON \"%s\" DURATION %s REPLICATION %s ",
                policyName, database, duration, replication);
        if (isDefault) {
            sql = sql + " DEFAULT";
        }
        this.query(sql);
    }


}
