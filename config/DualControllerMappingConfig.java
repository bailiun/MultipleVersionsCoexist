package org.bailiun.multipleversionscoexist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;

@Configuration
public class DualControllerMappingConfig implements WebMvcRegistrations {
    @Resource
    MultiVersionProperties mp;
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new DualRequestMappingHandlerMapping(mp);
    }
}
