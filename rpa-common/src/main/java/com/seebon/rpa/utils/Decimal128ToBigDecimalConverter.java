package com.seebon.rpa.utils;


import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.math.BigDecimal;

/**
 * @Author hao
 * @Date 2023/1/4 14:33
 * @Version 1.0
 **/
@ReadingConverter
public class Decimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {
    @Override
    public BigDecimal convert(Decimal128 decimal128) {
        return decimal128 == null ? BigDecimal.ZERO : decimal128.bigDecimalValue();
    }
}
