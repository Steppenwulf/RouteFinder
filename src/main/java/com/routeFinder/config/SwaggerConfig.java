package com.routeFinder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
	public static final String BASE_PACKAGE = "com.routeFinder";
	
	@Bean
	public Docket swaggerApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				.build()
				.pathMapping("/")
				.apiInfo(apiMetadata());
	}
	
	private ApiInfo apiMetadata() {
        return new ApiInfoBuilder().title("Route Finder")
                .description("Given a set of locations, identify if a route can be found between any two given locations")
                .contact(new Contact("S. Ramesh", "https://www.linkedin.com/in/ramesh2/", "sramesh.sramesh@gmail.com"))
                .version("0.1")
                .build();
	}

}
