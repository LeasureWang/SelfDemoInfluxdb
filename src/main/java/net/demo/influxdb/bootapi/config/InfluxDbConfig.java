package net.demo.influxdb.bootapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDbConfig {

    @Autowired
    private DemoInfluxDbProperties demoInfluxDbProperties;

    @Bean
    public DemoInfluxTemplate demoInfluxTemplate(){
        return new DemoInfluxTemplate(
                demoInfluxDbProperties.getUsername(),
                demoInfluxDbProperties.getPassword(),
                demoInfluxDbProperties.getConnUrl(),
                demoInfluxDbProperties.getDatabase(),
                demoInfluxDbProperties.getRetentionPolicy());
    }
}
