package mx.finerio.api.services

import javax.validation.Valid

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.repository.CallbackRepository
import mx.finerio.api.dtos.CallbackDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class CallbackService {

  @Autowired
  SecurityService securityService

  @Autowired
  CallbackRepository callbackRepository

  Callback create( CallbackDto callbackDto ) throws Exception {

    if ( !callbackDto ) {
      throw new BadImplementationException(
          'callbackService.create.callbackDto.null' )
    }
 
    def client = securityService.getCurrent()

    if ( callbackRepository.findByClientAndNature(
        client, callbackDto.nature ) ) {
      throw new BadRequestException( 'callback.create.exists' )
    }
 
    def instance = new Callback()
    instance.url = callbackDto.url
    instance.nature = callbackDto.nature
    instance.client = client
    def now = new Date()
    instance.dateCreated = now
    instance.lastUpdated = now
    callbackRepository.save( instance )
    instance

  }

  Map getFields( Callback callback ) throws Exception {

    if ( !callback ) {
      throw new BadImplementationException(
          'callbackService.getFields.callback.null' )
    }
 
    [ id: callback.id, url: callback.url, nature: callback.nature,
        dateCreated: callback.dateCreated, lastUpdated: callback.lastUpdated ]

  }

}
