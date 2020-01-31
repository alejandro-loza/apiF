package mx.finerio.api.services

import javax.validation.Valid
import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.dtos.*
import mx.finerio.api.exceptions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ClientService {

  @Autowired
  BcryptService cryptService

  @Autowired
  ClientRepository clientRepository

  @Autowired
  OauthClientDetailsService oauthClientDetailsService

  @Autowired
  SecurityService securityService

  void deleteClient() throws Exception {
    def instance = securityService.getCurrent()
    if ( !instance ) {
      throw new BadRequestException( 'client.deleteInstance.client.notFound' )
    }
    instance.enabled = false
    instance.accountNonExpired = false
    instance.accountNonLocked = false
    instance.credentialsNonExpired = false
    def today = new Date()
    instance.lastUpdated = today
    instance.dateDeleted = today
    clientRepository.save( instance )
    oauthClientDetailsService.deleteInstance( instance )
  }

  Map update( @Valid UpdateClientDto dto ) throws Exception {
    if ( !dto ) {
      throw new BadImplementationException(
          'clientService.update.dto.null' )
    }
    def instance = securityService.getCurrent()
    if ( !instance ) {
      throw new BadRequestException( 'client.update.client.notFound' )
    }
    def  details = oauthClientDetailsService.findOne( instance )
    if ( dto.isEmpty() ) {
      return getFields( instance, details )
    }
    if( dto.password ){ instance.password = cryptService.encode( dto.password ) }
    if( dto.categorize ){ instance.categorizeTransactions = dto.categorize }
    instance.lastUpdated = new Date()
    clientRepository.save( instance )
    getFields( instance, details )

  }

  Map findOne() throws Exception {
    def instance = securityService.getCurrent()
    if ( !instance ) {
      throw new BadRequestException( 'client.findOne.client.notFound' )
    }
    def  details = oauthClientDetailsService.findOne( instance )
    getFields( instance, details )

  }

  Map create( @Valid ClientDto dto ) throws Exception {

    if ( !dto ) {
      throw new BadImplementationException(
          'clientService.create.dto.null' )
    }
    if ( clientRepository.findOneByUsername( dto.name ) ) {
      throw new BadRequestException( 'client.create.username.exists' )
    }
 
    def instance = new Client()
    instance.username = dto.name 
    instance.password = cryptService.encode( dto.password ) 
    instance.enabled = true
    instance.accountNonExpired = true
    instance.accountNonLocked = true
    instance.credentialsNonExpired = true 
    def today = new Date()
    instance.dateCreated = today
    instance.lastUpdated = today
    instance.dateDeleted = null
    instance.categorizeTransactions = false
    instance.useTransactionsTable = true
    clientRepository.save( instance )
    def  details = oauthClientDetailsService.create( instance )
    getFields( instance, details )

  }

  private Map getFields( Client client, Map details ){
    Map map = [:]
    map.id = client.id
    map.username = client.username
    map.enabled = client.enabled
    map.dateCreated = client.dateCreated
    map.lastUpdated = client.lastUpdated
    map.isDeleted = client.dateDeleted ? true : false
    map.categorizeTransactions = client.categorizeTransactions
    map.details = details
    map  
  }


}
