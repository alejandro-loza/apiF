package mx.finerio.api.controllers

import mx.finerio.api.dtos.ErrorDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class ControllerExceptionHandler {

  @Autowired
  MessageSource messageSource

  @ExceptionHandler(BadRequestException)
  ResponseEntity handleBadRequestException(
      BadRequestException e ) {

    def errors = [ getError( e.message ) ]
    ResponseEntity.badRequest().body( [ errors: errors ] )

  }

  @ExceptionHandler(MethodArgumentNotValidException)
  ResponseEntity handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e ) {

    def errors = e.bindingResult.allErrors*.defaultMessage.collect {
      getError( it )
    }

    ResponseEntity.badRequest().body( [ errors: errors ] )

  }

  @ExceptionHandler(InstanceNotFoundException)
  ResponseEntity handleInstanceNotFoundException(
      InstanceNotFoundException e ) {
    ResponseEntity.notFound().build()
  }

    @ExceptionHandler(MethodArgumentTypeMismatchException)
  ResponseEntity handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e ) {

    def errors = [ getError( 'http.path.invalid' ) ]
    ResponseEntity.badRequest().body( [ errors: errors ] )

  }

  private ErrorDto getError( String message ) throws Exception {

    new ErrorDto(
      status: 400,
      code: message,
      title: messageSource.getMessage( message, null, null ),
      detail: messageSource.getMessage( "${message}.detail".toString(),
          null, null )
    )

  }

}
