package ch.andri.m295lb.utils.exeptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
//just redirecting to BadRequestExceptionHandler
@Provider
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException> { //since it implements what it does one can change the ConstraintViolationException
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        throw new BadRequestException(exception.getMessage()); //
    }
}
