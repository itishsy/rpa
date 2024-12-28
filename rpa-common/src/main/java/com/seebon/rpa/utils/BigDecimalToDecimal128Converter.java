package com.seebon.rpa.utils;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.math.BigDecimal;

/**
 * @Author hao
 * @Date 2023/1/4 14:38
 * @Version 1.0
 **/
@WritingConverter
public class BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {
    @Override
    public Decimal128 convert(BigDecimal bigDecimal) {
        BigDecimal source = bigDecimal != null ? bigDecimal : BigDecimal.ZERO;
        return new Decimal128(source);
    }
}
