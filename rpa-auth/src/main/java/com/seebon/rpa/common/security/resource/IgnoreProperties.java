package com.seebon.rpa.common.security.resource;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

//import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author Huangshouyi
 * @date 2019/12/11
 */
@Data
@Configuration
@ConditionalOnExpression("!'${ignore}'.isEmpty()")
@ConfigurationProperties(prefix = "ignore")
public class IgnoreProperties {
    private List<String> urls = new ArrayList<>();

    private List<String> clients = new ArrayList<>();

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }
}
