package ch.andri.m295lb.utils.exeptions;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BadRequestExceptionHandler implements ExceptionMapper<BadRequestException> { //since it impliments what it impliments you can specify the error message and do some custom things with it
    private final Logger logger = LogManager.getLogger(BadRequestExceptionHandler.class);//instantiate logger and also tell it in which class we are

    @Override
    public Response toResponse(BadRequestException exception) {
        logger.error(exception.getMessage());//logg the error message as error
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();//build and return the status/response
    }
}
