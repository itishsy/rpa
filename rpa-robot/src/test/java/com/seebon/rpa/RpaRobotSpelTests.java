package com.seebon.rpa;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.File;

@SpringBootTest
public class RpaRobotSpelTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testSpEL() {
        String.format("姓名：%s,时间：%s", "fd", "df");
        // String expression = "@browser.run()";
        String expression = "T(java.lang.String).format(\"姓名：%s,时间：%s\",#name,T(cn.hutool.core.date.DateUtil).now())";
        // String expression = "1==2";

        StandardEvaluationContext context = new StandardEvaluationContext();

        context.setVariable("name", "张三");

        BeanFactoryResolver factoryResolver = new BeanFactoryResolver(applicationContext);
        context.setBeanResolver(factoryResolver);
        ExpressionParser parser = new SpelExpressionParser();
        Expression parseExpression = parser.parseExpression(expression);
        Object object = parseExpression.getValue(context);
        System.out.println(object);
    }

    @Test
    void testPDf() throws Exception {
        File file = new File("E:\\20221214L01030000001365593951.pdf");
        PDDocument doc = PDDocument.load(file);

        PDFTextStripper pdfStripper = new PDFTextStripper();

        String text = pdfStripper.getText(doc);
        if (StringUtils.isBlank(text)) {
            return;
        }
        String[] texts = text.split("\r\n");
        String month = null;
        for (String value : texts) {
            if (StringUtils.isBlank(value)) {
                continue;
            }
            if (!value.startsWith("缴费起始年月")) {
                continue;
            }
            month = value.replace("缴费起始年月 ", "");
        }
        System.out.println("month=" + month);
        doc.close();
    }
}
