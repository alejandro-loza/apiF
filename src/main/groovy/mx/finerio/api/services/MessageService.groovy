package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service

@Service
class MessageService {

  @Autowired
  MessageSource messageSource

  Map findOne( String message, String code = null ) throws Exception {

    if ( !message ) {
      throw new BadImplementationException(
          'messageService.findOne.message.null' )
    }

    [
      code: code ? messageSource.getMessage( code, null, null ) : message,
      title: messageSource.getMessage( message, null, null ),
      detail: messageSource.getMessage( "${message}.detail".toString(),
          null, null )
    ]

  }

}
