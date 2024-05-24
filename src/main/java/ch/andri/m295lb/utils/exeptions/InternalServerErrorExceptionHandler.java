package ch.andri.m295lb.utils.exeptions;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//on this error ExceptionMapper<InternalServerErrorException> you do what you want to do
@Provider
public class InternalServerErrorExceptionHandler implements ExceptionMapper<InternalServerErrorException> {
    private final Logger logger = LogManager.getLogger(InternalServerErrorExceptionHandler.class);

    @Override
    public Response toResponse(InternalServerErrorException exception) {
        logger.error(exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
    }
}
