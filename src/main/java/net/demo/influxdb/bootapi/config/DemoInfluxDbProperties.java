package net.demo.influxdb.bootapi.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "demo.influx")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemoInfluxDbProperties {

    private String username;
    private String password;
    private String connUrl;
    private String database;
    private String retentionPolicy;
}
