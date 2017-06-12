package com.forgerock.microblog.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Required for Swagger REST API docs.
 * See http://(host)/api-docs/swagger-ui.html
 */
@Configuration
@ComponentScan
@EnableSwagger2
public class SwaggerConfig {

    private final static Logger LOG = LoggerFactory.getLogger(SwaggerConfig.class);

    private static final String BASE_PATH = "com.forgerock.microblog";

    private static final Contact CONTACT = new Contact("David Higgins", "", "dehiggins@gmail.com");

    private static final String API_VERSION = "1.0";

    /**
     * In all non-production profiles we can allow all REST API docs
     * @return API
     */
    @Bean
    public Docket api() {

        LOG.info("Creating REST API docs for: {}", BASE_PATH);
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PATH))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {

        return new ApiInfo(
                "Microblog REST Service",
                "This is a microblogging REST platform that can be deployed and uses to support any simple blogging UI (e.g. a Supervillain ideas website)",
                API_VERSION,
                "N/A",
                CONTACT,
                "",
                "");
    }

}
