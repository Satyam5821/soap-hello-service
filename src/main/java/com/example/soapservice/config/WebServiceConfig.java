package com.example.soapservice.config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

import org.springframework.context.ApplicationContext;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.ClassPathResource;

import org.springframework.ws.config.annotation.EnableWs;

import org.springframework.ws.config.annotation.WsConfigurerAdapter;

import org.springframework.ws.transport.http.MessageDispatcherServlet;

import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;

import org.springframework.xml.xsd.SimpleXsdSchema;

import org.springframework.xml.xsd.XsdSchema;

//hello _ name config class

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
  private static final String DEFAULT_TARGET_NAMESPACE = "http://spring.io/guides/gs-producing-web-service";
  @Bean
  public ServletRegistrationBean<MessageDispatcherServlet> helloDispatcherServlet(ApplicationContext applicationContext) {
    MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setApplicationContext(applicationContext);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean<>(servlet, "/ws/*");

  }

  @Bean(name = "hello")
  public DefaultWsdl11Definition defaultWsdl11Definition(@Qualifier ("helloSchema")XsdSchema helloSchema) {

    DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
    wsdl11Definition.setPortTypeName("HelloPort");
    wsdl11Definition.setLocationUri("/ws/hello");
    wsdl11Definition.setTargetNamespace(DEFAULT_TARGET_NAMESPACE);
    wsdl11Definition.setSchema(helloSchema);
    return wsdl11Definition;

  }


  @Bean
  public XsdSchema helloSchema() {

    return new SimpleXsdSchema(new ClassPathResource("hello.xsd"));

  }
  
  @Bean(name = "userlist")
  public DefaultWsdl11Definition userListWsdl(@Qualifier("userListSchema") XsdSchema schema) {
      DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
      definition.setPortTypeName("UserListPort");
      definition.setLocationUri("/ws/userlist");
      definition.setTargetNamespace(DEFAULT_TARGET_NAMESPACE);
      definition.setSchema(schema);
      return definition;
  }

  @Bean
  public XsdSchema userListSchema() {
      return new SimpleXsdSchema(new ClassPathResource("userlist.xsd"));
  }
  
  @Bean(name = "userlistid")
  public DefaultWsdl11Definition userLisidtWsdl(@Qualifier("userListidSchema") XsdSchema userListidSchema) {
      DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
      definition.setPortTypeName("UserListidPort");
      definition.setLocationUri("/ws/userlistid");
      definition.setTargetNamespace(DEFAULT_TARGET_NAMESPACE);
      definition.setSchema(userListidSchema);
      return definition;
  }

  @Bean
  public XsdSchema userListidSchema() {
      return new SimpleXsdSchema(new ClassPathResource("userlistid.xsd"));
  }
  
  @Bean(name = "textFile")
  public DefaultWsdl11Definition defaultWsdl11DefinitionText(@Qualifier("textFileSchema") XsdSchema textFileSchema) {
      DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
      wsdl11Definition.setPortTypeName("TextFilePort");
      wsdl11Definition.setLocationUri("/ws/readtxt");
  wsdl11Definition.setTargetNamespace(DEFAULT_TARGET_NAMESPACE);
      wsdl11Definition.setSchema(textFileSchema);
      return wsdl11Definition;
  }
   
  @Bean
  public XsdSchema textFileSchema() {
      return new SimpleXsdSchema(new ClassPathResource("readTextFile.xsd"));
  }
  
  @Bean(name = "shipping")
  public DefaultWsdl11Definition shippingWsdl(@Qualifier ("shippingSchema")XsdSchema schema) {
      DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
      definition.setPortTypeName("ShippingPort");
      definition.setLocationUri("/shipping");
definition.setTargetNamespace("http://spring.io/guides/shipping-service");
      definition.setSchema(schema);
      return definition;
  }

  @Bean
  public XsdSchema shippingSchema() {
      return new SimpleXsdSchema(new ClassPathResource("shipping.xsd"));
  }
  
  @Bean(name = "emi")
  public DefaultWsdl11Definition emiWsdl(@Qualifier ("emiSchema")XsdSchema emiSchema) {
      DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
      definition.setPortTypeName("EmiPort");
      definition.setLocationUri("/emi");
definition.setTargetNamespace(DEFAULT_TARGET_NAMESPACE);
      definition.setSchema(emiSchema);
      return definition;
  }

  @Bean
  public XsdSchema emiSchema() {
      return new SimpleXsdSchema(new ClassPathResource("emiCalculator.xsd"));
  }
  
  @Bean(name = "premium")
  public DefaultWsdl11Definition defaultWsdl1Definition(@Qualifier ("premiumSchema")XsdSchema premiumSchema) {
    DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
    wsdl11Definition.setPortTypeName("PremiumPort");
    wsdl11Definition.setLocationUri("/ws/car");
    wsdl11Definition.setTargetNamespace(DEFAULT_TARGET_NAMESPACE);
    wsdl11Definition.setSchema(premiumSchema);
    return wsdl11Definition;

  }

   

  @Bean
  public XsdSchema premiumSchema() {
    return new SimpleXsdSchema(new ClassPathResource("car.xsd"));

  }

  @Bean(name = "calculator")
  public DefaultWsdl11Definition calculatorWsdl(@Qualifier ("calculatorSchema")XsdSchema calculatorSchema) {
      DefaultWsdl11Definition wsdlDefinition = new DefaultWsdl11Definition();
      wsdlDefinition.setPortTypeName("CalculatorPort");
      wsdlDefinition.setLocationUri("/ws/calculator");
      wsdlDefinition.setTargetNamespace(DEFAULT_TARGET_NAMESPACE);
      wsdlDefinition.setSchema(calculatorSchema);
      return wsdlDefinition;
  }
   
  @Bean
  public XsdSchema calculatorSchema() {
      return new SimpleXsdSchema(new ClassPathResource("calculator.xsd"));
  }

}