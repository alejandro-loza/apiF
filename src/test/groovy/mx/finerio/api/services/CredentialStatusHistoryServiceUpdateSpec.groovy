package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CredentialStatusHistoryServiceUpdateSpec extends Specification {

  def service = new CredentialStatusHistoryService()

  def credentialService = Mock( CredentialService )
  def credentialStatusHistoryRepository = Mock( CredentialStatusHistoryRepository )

  def setup() {

    service.credentialService = credentialService
    service.credentialStatusHistoryRepository = credentialStatusHistoryRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.update( credential )
    then:
      1 * credentialService.findAndValidate( _ as String ) >> getCredential("1")
      1 * credentialStatusHistoryRepository.findAllByCredentialAndDateDeletedIsNull(
          _ as Credential ) >> getCredentialList("1")
      1 * credentialStatusHistoryRepository.save( _ as Credential )
    where:
      credential = getCredential("1")

  }

  def "parameter 'credential' is null"() {

    when:
      service.update( credential )
    then:
      BadImplementationException e = thrown()
      e.message == 'credentialStatusHistoryService.update.credential.null'
    where:
      credential = null

  }

  def "instance not found"() {

    when:
      def result = service.update( credential )
    then:
      1 * credentialService.findAndValidate( _ as String ) >> getCredential("1")
      1 * credentialStatusHistoryRepository.findAllByCredentialAndDateDeletedIsNull(
          _ as Credential ) >> null
    where:
      credential = getCredential("1")

  }

  List<Credential> getCredentialList( String id ){
    List<Credential> credList = new ArrayList<>()
    credList.add(new Credential(
            id: id,
            status: Credential.Status.ACTIVE
    ))
    return credList
  }

  Credential getCredential( String id ){
    def cred = new Credential(
      id: id,
      status: Credential.Status.ACTIVE  
    )
    return cred  
  }

  CredentialStatusHistory getCredentialStatus( Long id ){
    def cred = new CredentialStatusHistory(
      id: id,
      credential: getCredential("1"),
      status: CredentialStatusHistory.Status.ACTIVE,
      lastUpdated: new Date(),
      dateCreated: new Date()
    )
    return cred  
  }


}
