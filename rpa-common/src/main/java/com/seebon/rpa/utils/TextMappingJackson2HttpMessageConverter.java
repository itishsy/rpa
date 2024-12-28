package com.seebon.rpa.utils;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author created By charles
 * @Description:
 * @Date 2020/8/6 14:27
 * @Modifide By:
 */
public class TextMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public TextMappingJackson2HttpMessageConverter() {
        List<MediaType> mediaTypes=new ArrayList<>();
        //添加text/html类型的支持
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.TEXT_HTML);

        //添加text/plain类型的支持.微信接口会用到
        mediaTypes.add(MediaType.TEXT_PLAIN);

        mediaTypes.add(MediaType.TEXT_XML);

        setSupportedMediaTypes(mediaTypes);
    }
}