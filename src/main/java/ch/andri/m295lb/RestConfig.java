package ch.andri.m295lb;

import ch.andri.m295lb.utils.security.AuthenticationFilter;
import ch.andri.m295lb.servlets.ModuleController;
import ch.andri.m295lb.utils.exeptions.BadRequestExceptionHandler;
import ch.andri.m295lb.utils.exeptions.ConstraintViolationExceptionHandler;
import ch.andri.m295lb.utils.exeptions.InternalServerErrorExceptionHandler;
import ch.andri.m295lb.utils.exeptions.NotFoundExceptionHandler;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/resources")
public class RestConfig extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<>(
                Arrays.asList(//todo change / update the list (all controllers and utils
                        ModuleController.class,
                        NotFoundExceptionHandler.class,
                        ConstraintViolationExceptionHandler.class,
                        BadRequestExceptionHandler.class,
                        InternalServerErrorExceptionHandler.class,
                        AuthenticationFilter.class
                )
        );
    }
}
