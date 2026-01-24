package com.resumequill.app;

import com.resumequill.app.common.filters.HttpLoggingFilter;
import com.resumequill.app.configs.ServletFilterConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import jakarta.servlet.*;
import com.resumequill.app.configs.AppConfig;

import java.util.EnumSet;

public class AppInitializer implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
    AnnotationConfigWebApplicationContext rootCtx = new AnnotationConfigWebApplicationContext();
    rootCtx.register(AppConfig.class);
    servletContext.addListener(new ContextLoaderListener(rootCtx));

    ServletFilterConfig.register(servletContext);

//    AnnotationConfigWebApplicationContext mvcCtx = new AnnotationConfigWebApplicationContext();
//    mvcCtx.register(WebConfig.class);

    // === HttpLoggingFilter ===
    FilterRegistration.Dynamic loggingFilter =
      servletContext.addFilter("httpLoggingFilter", new HttpLoggingFilter());

    if (loggingFilter != null) {
      loggingFilter.addMappingForUrlPatterns(
        EnumSet.of(DispatcherType.REQUEST),
        false,
        "/*"
      );
      loggingFilter.setAsyncSupported(true);
    }

    // === CharacterEncodingFilter ===
    CharacterEncodingFilter encoding = new CharacterEncodingFilter();
    encoding.setEncoding("UTF-8");
    encoding.setForceEncoding(true);

    FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encodingFilter", encoding);

    if (encodingFilter != null) {
      encodingFilter.addMappingForUrlPatterns(null, false, "/*");
    }

    // === DispatcherServlet ===
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(rootCtx));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");

		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
				"",
				10485760L,
				10485760L,
				1048576
		);
		dispatcher.setMultipartConfig(multipartConfigElement);

//    DelegatingFilterProxy securityProxy = new DelegatingFilterProxy("springSecurityFilterChain");
//    // привяжем к root-контексту (по умолчанию так и будет, но явно — надёжнее)
//    securityProxy.setContextAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
//
//    servletContext.addFilter("springSecurityFilterChain", securityProxy)
//      .addMappingForUrlPatterns(null, false, "/*");
	}
}
