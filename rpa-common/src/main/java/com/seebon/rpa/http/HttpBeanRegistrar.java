package com.seebon.rpa.http;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.*;

@Component
public class HttpBeanRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Set<String> basePackages = this.getBasePackages(metadata);
        LinkedHashSet<BeanDefinition> candidateComponents = candidateComponents(HttpService.class,basePackages);
        for (BeanDefinition candidateComponent : candidateComponents) {
            AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) candidateComponent;
            Map<String, Object> attributes = abd.getMetadata().getAnnotationAttributes(HttpService.class.getCanonicalName());
            String baseUrl = attributes.get("value") + "";
            registerBeanDefinition(registry,candidateComponent,resolve(baseUrl));
        }
    }

    public void registerBeanDefinition(BeanDefinitionRegistry registry,BeanDefinition candidateComponent,String baseUrl){
        try {
            String className = candidateComponent.getBeanClassName();
            Class<?> clazz = resourceLoader.getClassLoader().loadClass(className);
            BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(HttpFactoryBean.class);

            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            definition.addPropertyValue("objectType", clazz);
            definition.addPropertyValue("baseUrl", baseUrl);

            AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
            beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);

            BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className);
            BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedHashSet<BeanDefinition> candidateComponents(Class<? extends Annotation> annotationType,Set<String> basePackages){
        LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();

        ClassPathScanningCandidateComponentProvider scanner = this.getScanner();
        scanner.setResourceLoader(resourceLoader);
        AnnotationTypeFilter typeFilter = new AnnotationTypeFilter(annotationType);
        scanner.addIncludeFilter(typeFilter);

        for (String basePackage : basePackages) {
            candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
        }
        return candidateComponents;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation()) {
                    isCandidate = true;
                }

                return isCandidate;
            }
        };
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(HttpScan.class.getName());
        Set<String> basePackages = new HashSet<>();
        if (attributes != null) {
            for (String pkg : (String[]) attributes.get("value")) {
                if (StringUtils.hasText(pkg)) {
                    basePackages.add(pkg);
                }
            }
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
            basePackages.add(ClassUtils.getPackageName(ClassUtils.getPackageName(this.getClass().getName())));
        }
        return basePackages;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
