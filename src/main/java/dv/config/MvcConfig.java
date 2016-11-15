package dv.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter{

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        super.configurePathMatch(configurer);

        configurer.setUseRegisteredSuffixPatternMatch(true);
    }

    @Bean
    public FilterRegistrationBean requestLoggingFilterBean() {
        CustomRequestLoggingFilter f = new CustomRequestLoggingFilter();
        f.setIncludeQueryString(true);
        f.setIncludeHeaders(true);
//        f.includeHeaders("authorization", "content-type", "content-length", "accept");
        f.setIncludeClientInfo(true);
        f.setIncludePayload(true);
        f.setMaxPayloadLength(1000);
        f.setBeforeMessagePrefix("\n------------------------ <BEFORE> --------------------------\n");
        f.setBeforeMessageSuffix("\n------------------------ </BEFORE> --------------------------");
        f.setAfterMessagePrefix("\n------------------------ <AFTER> --------------------------\n");
        f.setAfterMessageSuffix("\n------------------------ </AFTER> --------------------------");

        FilterRegistrationBean b = new FilterRegistrationBean();
        b.setFilter(f);
        b.setOrder(Ordered.HIGHEST_PRECEDENCE);
        b.setUrlPatterns(Arrays.asList("/", "/templog/*"));

        return b;
    }
}
