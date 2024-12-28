package com.seebon.rpa.common.config;


import com.seebon.rpa.utils.BigDecimalToDecimal128Converter;
import com.seebon.rpa.utils.Decimal128ToBigDecimalConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author hao
 * @Date 2023/1/4 14:28
 * @Version 1.0
 **/
@Configuration
public class MongoCustomConfig {

    @Lazy
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>(2);
        converters.add(new BigDecimalToDecimal128Converter());
        converters.add(new Decimal128ToBigDecimalConverter());
        return new MongoCustomConversions(converters);
    }


    public MongoTemplate mongoTemplate() {
        MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        return mongoTemplate;
    }

}
