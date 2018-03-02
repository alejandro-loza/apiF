package mx.finerio.api.controllers

import com.fasterxml.jackson.databind.exc.InvalidFormatException

import mx.finerio.api.dtos.ErrorDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.services.MessageService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class ControllerExceptionHandler {

  @Autowired
  MessageService messageService

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

    def errors = [ getError( e.message ) ]
    new ResponseEntity( [ errors: errors ], HttpStatus.NOT_FOUND )

  }

  @ExceptionHandler(MethodArgumentTypeMismatchException)
  ResponseEntity handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e ) {

    def errors = [ getError( 'http.path.invalid' ) ]
    ResponseEntity.badRequest().body( [ errors: errors ] )

  }

  @ExceptionHandler(InvalidFormatException)
  ResponseEntity handleInvalidFormatException(
      InvalidFormatException e ) {

    def code = e.pathReference.replaceAll( /\["/, '.' )
    .replaceAll( /"\]/, '' )

    def errors = [ getError( "${code}.invalidFormat",
        "${code}.friendlyCode" ) ]
    ResponseEntity.badRequest().body( [ errors: errors ] )

  }

  private ErrorDto getError( String message, String code = null )
      throws Exception {
    new ErrorDto( messageService.findOne( message, code ) )
  }

}
