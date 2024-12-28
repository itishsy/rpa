package com.seebon.rpa.repository.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
public class TemplateConfig implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ApplicationContextAware {

    private Environment environment;
    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try {
            String activated = environment.getProperty("developer.mysql.activated");
            if (StringUtils.isNotBlank(activated) && ("on".equals(activated) || "true".equals(activated))) {
                String url = environment.getProperty("developer.mysql.url");
                String username = environment.getProperty("developer.mysql.username");
                String password = environment.getProperty("developer.mysql.password");
                String mapperLocations = environment.getProperty("mybatis.mapper-locations");
                GenericBeanDefinition dataSourceDefinition = new GenericBeanDefinition();
                dataSourceDefinition.setBeanClass(HikariDataSource.class);
                dataSourceDefinition.getPropertyValues().add("driverClassName", "com.mysql.jdbc.Driver");
                dataSourceDefinition.getPropertyValues().add("jdbcUrl", url);
                dataSourceDefinition.getPropertyValues().add("username", username);
                dataSourceDefinition.getPropertyValues().add("password", password);
                registry.registerBeanDefinition("mysqlDataSource", dataSourceDefinition);

                SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
                bean.setDataSource(applicationContext.getBean("mysqlDataSource", DataSource.class));
                bean.setMapperLocations(
                        new PathMatchingResourcePatternResolver()
                                .getResources(mapperLocations));
                SqlSessionFactory sqlSessionFactory = bean.getObject();

                GenericBeanDefinition sessionTemplateDefinition = new GenericBeanDefinition();
                sessionTemplateDefinition.setBeanClass(SqlSessionTemplate.class);
                ConstructorArgumentValues constructorArgumentValuesForKeyValueTemplate = new ConstructorArgumentValues();
                constructorArgumentValuesForKeyValueTemplate.addGenericArgumentValue(sqlSessionFactory);
                sessionTemplateDefinition.setConstructorArgumentValues(constructorArgumentValuesForKeyValueTemplate);
                sessionTemplateDefinition.setPrimary(true);
                registry.registerBeanDefinition("mysqlSessionTemplate", sessionTemplateDefinition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
