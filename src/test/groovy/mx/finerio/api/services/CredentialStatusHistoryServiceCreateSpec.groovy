package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialStatusHistoryServiceCreateSpec extends Specification {

  def service = new CredentialStatusHistoryService()

  def credentialService = Mock( CredentialService )
  def credentialStatusHistoryRepository = Mock( CredentialStatusHistoryRepository )

  def setup() {

    service.credentialService = credentialService
    service.credentialStatusHistoryRepository = credentialStatusHistoryRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.create( credential )
    then:
      1 * credentialService.findAndValidate( _ as String ) >> getCredential("1")
      1 * credentialStatusHistoryRepository.
          findByCredentialAndDateDeletedIsNull( _ as Credential ) >> null
      1 * credentialStatusHistoryRepository.save( _ as CredentialStatusHistory ) 
      result instanceof CredentialStatusHistory
    where:
      credential = getCredential("1")

  }

  def "parameter 'credential' is null"() {

    when:
      service.create( credential )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialStatusHistoryService.create.credential.null'
    where:
      credential = null

  }

  def "instance already exists"() {

    when:
      def result = service.create( credential )
    then:
      1 * credentialService.findAndValidate( _ as String ) >> getCredential("1")
      1 * credentialStatusHistoryRepository.findByCredentialAndDateDeletedIsNull(
          _ as Credential ) >> new CredentialStatusHistory()
      result instanceof CredentialStatusHistory
    where:
      credential = getCredential("1")

  }

  Credential getCredential(String id){
    def cred = new Credential(
      id: id,
      status: Credential.Status.ACTIVE  
    )
    return cred  
  }


}
