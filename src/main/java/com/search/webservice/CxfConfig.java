package com.search.webservice;

import com.search.webservice.impl.SearchWebserviceImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

@Configuration
public class CxfConfig {


    @Bean
    public ServletRegistrationBean  servletRegistrationBean(){
        return new ServletRegistrationBean(new CXFServlet(),"/sinosoft/*");
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
       return new SpringBus();
     }
    @Bean
    public SearchWebservice searchWebservice(){
        return new SearchWebserviceImpl();
    }
    @Bean
    public Endpoint endpoint(){
        EndpointImpl endpoint = new EndpointImpl(springBus(),searchWebservice());
        endpoint.publish("/searchEngine");
        return endpoint;
    }

}
