package mnykolaichuk.prz.pracaDyplomowa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

import java.util.Map;

@Configuration
public class ThymeleafConfig implements ITemplateResolver {

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        // inne ustawienia...
        return templateResolver;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Integer getOrder() {
        return null;
    }

    @Override
    public TemplateResolution resolveTemplate(IEngineConfiguration iEngineConfiguration, String s, String s1, Map<String, Object> map) {
        return null;
    }
}

