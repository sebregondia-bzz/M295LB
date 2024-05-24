package ch.andri.m295lb.utils.exeptions;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/// on error ExceptionMapper<NotFoundException> you do what you feel like
@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<NotFoundException> {
    private final Logger logger = LogManager.getLogger(NotFoundExceptionHandler.class);

    @Override
    public Response toResponse(NotFoundException exception) {
        logger.warn(exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}
