package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.repository.*
import mx.finerio.api.domain.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CredentialStatusHistoryService {

  @Autowired
  CredentialService credentialService

  @Autowired
  CredentialStatusHistoryRepository credentialStatusHistoryRepository

  CredentialStatusHistory create( Credential credential ) throws Exception {

    if ( !credential ) {
      throw new BadImplementationException(
          'credentialStatusHistoryService.create.credential.null' )
    }
 
    credential = credentialService.findAndValidate( credential.id )

    def instance = credentialStatusHistoryRepository.findByCredentialAndDateDeletedIsNull( credential )
    if ( instance ) {
      return instance
    }
    instance = new CredentialStatusHistory()
    instance.credential = credential  
    switch( credential.status ){
      case "TOKEN":
        instance.status = CredentialStatusHistory.Status.TOKEN
        break
      case "ACTIVE":
        instance.status = CredentialStatusHistory.Status.ACTIVE
        break
      case "INACTIVE":
        instance.status = CredentialStatusHistory.Status.INACTIVE
        break
      default:
        instance.status = CredentialStatusHistory.Status.INVALID
    }
    instance.dateCreated = new Date()
    instance.lastUpdated = new Date()
    credentialStatusHistoryRepository.save( instance )
    instance

  }

  void update( Credential credential ) throws Exception {

    if ( !credential ) {
      throw new BadImplementationException(
          'credentialStatusHistoryService.update.credential.null' )
    }
 
    credential = credentialService.findAndValidate( credential.id )
    def instances = credentialStatusHistoryRepository.
        findAllByCredentialAndDateDeletedIsNull( credential )

    if ( !instances ) {
      return 
    }

    instances.each { instance ->

      instance.lastUpdated = new Date()
      instance.dateDeleted = new Date()
      credentialStatusHistoryRepository.save( instance )

    }

  }

}
