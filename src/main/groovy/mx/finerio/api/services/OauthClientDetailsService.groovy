package mx.finerio.api.services

import javax.validation.Valid
import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.clients.*
import mx.finerio.api.exceptions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class OauthClientDetailsService {

  @Autowired
  OauthClientDetailsRepository oauthClientDetailsRepository

  void deleteInstance( Client client ) throws Exception {
    if ( !client ) {
      throw new BadImplementationException(
          'oauthClientDetailsService.deleteInstance.client.null' )
    }
    def additionalInfo = "{\"name\":\"${client.username}\"}"
    def instance = oauthClientDetailsRepository.findOneByAdditionalInfo( additionalInfo ) 
    if ( !instance ) {
      throw new BadImplementationException(
          'oauthClientDetailsService.deleteInstance.oauthClientDetails.notFound' )
    }
    def today = new Date()
    instance.lastUpdated = today
    instance.dateDeleted = today
    oauthClientDetailsRepository.save( instance )
  }

  Map findOne( Client client ) throws Exception {
    if ( !client ) {
      throw new BadImplementationException(
          'oauthClientDetailsService.findOne.client.null' )
    }
    def additionalInfo = "{\"name\":\"${client.username}\"}"
    def instance = oauthClientDetailsRepository.findOneByAdditionalInfo( additionalInfo ) 
    if ( !instance ) {
      throw new BadImplementationException(
          'oauthClientDetailsService.findOne.oauthClientDetails.notFound' )
    }
    [ clientId: instance.clientId, clientSecret: instance.clientSecret ]
  }

  Map create( Client client ) throws Exception {

    if ( !client ) {
      throw new BadImplementationException(
          'oauthClientDetailsService.create.client.null' )
    }

    def clientRandom = generateStringRandom()
    def instance = oauthClientDetailsRepository.findOneByClientId( clientRandom ) 

    while ( instance ) {
      instance = oauthClientDetailsRepository.findOneByClientId( clientRandom ) 
    }
 
    instance = new OauthClientDetails()
    instance.clientId = clientRandom
    instance.clientSecret = generateStringRandom()
    instance.accessTokenValidity = 3600
    instance.refreshTokenValidity = 7200
    instance.scope = "read,write"
    instance.grantTypes = "password,refresh_token"
    instance.resourceIds = "resource_id"
    instance.webServerRedirectUri =  null
    instance.authorities =  null
    instance.additionalInfo = "{\"name\":\"${client.username}\"}"
    instance.autoApprove = null
    def today = new Date()
    instance.dateCreated = today
    instance.lastUpdated = today
    instance.dateDeleted = null
    oauthClientDetailsRepository.save( instance )
    [ clientId: instance.clientId, clientSecret: instance.clientSecret ]
  }

  String generateStringRandom(){
    String alp = (('a'..'z')+('A'..'Z')+('0'..'9')).join()
    return new Random().with {
      (1..50).collect { alp[ nextInt( alp.length() ) ] }.join()
    }
  }

}
