package org.bailiun.multipleversionscoexist.config;

import org.bailiun.multipleversionscoexist.Properties.MultiVersionFile;
import org.bailiun.multipleversionscoexist.Properties.MultiVersionInfo;
import org.bailiun.multipleversionscoexist.Properties.MultiVersionProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
/**
 * 版本控制启动项 / Version control startup items
 *
 * <p><b>中文说明：</b><br>
 * 用于将版本管理功能添加进程序进程
 * </p>
 *
 * <p><b>English Description:</b><br>
 * Used to add version management functionality to an application process</p>
 *
 * @author Bailiun
 * @since 1.0.0
 */

@Configuration
@EnableConfigurationProperties({MultiVersionProperties.class,
        MultiVersionInfo.class,
        MultiVersionFile.class})
public class DualControllerMappingConfig implements WebMvcRegistrations {
    /**
     * 版本管理模块配置文件的实例。
     * <br>An instance of a version management module configuration file
     */
    @Resource
    MultiVersionProperties mp;
    /**
     * 版本信息输出模块配置文件的实例。
     * <br>Version information outputs an instance of a module configuration file
     */
    @Resource
    MultiVersionInfo mi;
    /**
     * 本地文件控制模块配置文件的实例。
     * <br>The local file controls the instance of the module configuration file
     */
    @Resource
    MultiVersionFile mf;

    public DualControllerMappingConfig(MultiVersionProperties mp, MultiVersionInfo mi, MultiVersionFile mf) {
        this.mp = mp;
        this.mi = mi;
        this.mf = mf;
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        System.out.println("✅ DualRequestMappingHandlerMapping启动");
        DualRequestMappingHandlerMapping r = new DualRequestMappingHandlerMapping(mp,mf,mi);
        if(mi.isStart()){
            System.out.println("----------------------");
            System.out.println("版本信息展示");
            mi.print();
            System.out.println("----------------------");
        }
        return r;
    }
    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
