package ch.andri.m295lb;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@Order(Ordered.HIGHEST_PRECEDENCE)//  sets the highest priority for the annotated component, ensuring it is executed first.
public class ApplicationInitializer
        implements WebApplicationInitializer { //By implementing the WebApplicationInitializer interface, this class becomes a candidate for configuration and initialization of the ServletContext when the application starts.
//so it can configure and connect our code to the local host servers

    @Override
    public void onStartup(ServletContext servletContext) // get the servlet context (so we can add some things)
            throws ServletException {

        AnnotationConfigWebApplicationContext context
                = new AnnotationConfigWebApplicationContext(); //setting things and so sites can load

        servletContext.addListener(new ContextLoaderListener(context));
        servletContext.setInitParameter(
                "contextConfigLocation", "ch.andri.m295lb");//Todo changes ch.andri.springjerseymodulraum
    }
}