package mx.finerio.api.services

import com.fasterxml.jackson.databind.ObjectMapper

import javax.validation.Valid

import mx.finerio.api.domain.Callback
import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientMtls
import mx.finerio.api.domain.repository.CallbackRepository
import mx.finerio.api.dtos.CallbackDto
import mx.finerio.api.dtos.CallbackUpdateDto
import mx.finerio.api.dtos.MtlsDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class CallbackService {

  @Autowired
  @Lazy
  CallbackService selfReference

  @Autowired
  CallbackRestService callbackRestService

  @Autowired
  ClientMtlsService clientMtlsService

  @Autowired
  CryptService cryptService

  @Autowired
  SecurityService securityService

  @Autowired
  CallbackRepository callbackRepository

  @Autowired 
  RsaCryptService rsaCryptService

  Callback create( CallbackDto callbackDto ) throws Exception {

    if ( !callbackDto ) {
      throw new BadImplementationException(
          'callbackService.create.callbackDto.null' )
    }
 
    def client = securityService.getCurrent()

    if ( callbackRepository.findFirstByClientAndNatureAndDateDeletedIsNull(
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

  Map findAll() throws Exception {

    def client = securityService.getCurrent()
    [ data: callbackRepository.findAllByClientAndDateDeletedIsNull(
        client ), nextCursor: null ]

  }

  List<Callback> findAllByNature( Callback.Nature nature )
      throws Exception {

    if ( nature == null ) {
      throw new BadImplementationException(
          'callbackService.findAllByNature.nature.null' )
    }

    return callbackRepository.findAllByNatureAndDateDeletedIsNull( nature )

  }

  Callback findOne( Long id ) throws Exception {

    if ( id == null ) {
      throw new BadImplementationException(
          'callbackService.findOne.id.null' )
    }
 
    def client = securityService.getCurrent()
    def instance = callbackRepository.findOne( id )

    if ( !instance || instance.client.id != client.id ||
        instance.dateDeleted != null ) {
      throw new InstanceNotFoundException( 'callback.not.found' )
    }
 
    instance

  }

  Callback update( Long id, CallbackUpdateDto callbackUpdateDto )
      throws Exception {

    if ( id == null ) {
      throw new BadImplementationException(
          'callbackService.update.id.null' )
    }
 
    if ( !callbackUpdateDto ) {
      throw new BadImplementationException(
          'callbackService.update.callbackUpdateDto.null' )
    }
 
    def instance = findOne( id )
    instance.url = callbackUpdateDto.url
    instance.lastUpdated = new Date()
    callbackRepository.save( instance )

  }

  Map getFields( Callback callback ) throws Exception {

    if ( !callback ) {
      throw new BadImplementationException(
          'callbackService.getFields.callback.null' )
    }
 
    [ id: callback.id, url: callback.url, nature: callback.nature,
        dateCreated: callback.dateCreated, lastUpdated: callback.lastUpdated ]

  }

  void sendToClient( Client client, Callback.Nature nature, Map data )
      throws Exception {

    validateSendToClientInput( client, nature, data )
    def callback = callbackRepository.
        findFirstByClientAndNatureAndDateDeletedIsNull( client, nature )

    if ( !callback ) {
      return
    }

    def headers = getHeaders( callback.url, data, client )
    def clientMtls = clientMtlsService.findByClient( client )

    if ( clientMtls != null ) {
      selfReference.sendCallback( callback.url, headers, data, clientMtls )
    } else {
      selfReference.sendCallback( callback.url, headers, data )
    }

  }

  @Async
  void sendCallback( String url, Map headers, Map data ) throws Exception {
    callbackRestService.post( url, data, headers )
  }

  @Async
  void sendCallback( String url, Map headers, Map data,
      ClientMtls clientMtls ) throws Exception {

    def mtlsDto = new MtlsDto()
    mtlsDto.filename = clientMtls.filename
    mtlsDto.secret = cryptService.decrypt( clientMtls.secret,
        clientMtls.iv )
    callbackRestService.post( url, data, headers, mtlsDto )

  }

  private Map getHeaders( String url, Map data, Client client )
      throws Exception {

    def signatureHeader = getSignatureHeader( url, data )
    def clientHeaders = getClientHeaders( client )
    return ( signatureHeader + clientHeaders )

  }

  private Map getSignatureHeader( String url, Map data ) throws Exception {

    def objectMapper = new ObjectMapper()
    def jsonString = objectMapper.writeValueAsString( data )
    def finalData = "${url}|${jsonString}"
    def jsonSigned = rsaCryptService.sign( finalData )
    return [ 'Signature': jsonSigned ]

  }

  private Map getClientHeaders( Client client ) throws Exception {

    def headers = [:]
    if ( client.userAgent != null ) {
      headers[ 'User-Agent' ] = client.userAgent
    }
    return headers

  }

  private void validateSendToClientInput( Client client,
    Callback.Nature nature, Map data ) throws Exception {

    if ( !client ) {
      throw new BadImplementationException(
          'callbackService.sendToClient.client.null' )
    }
 
    if ( !nature ) {
      throw new BadImplementationException(
          'callbackService.sendToClient.nature.null' )
    }
 
    if ( !data ) {
      throw new BadImplementationException(
          'callbackService.sendToClient.data.null' )
    }

  }

  void delete( Long id ) throws Exception {

    def instance = findOne( id )
    instance.dateDeleted = new Date()
    callbackRepository.save( instance )

  }

}
