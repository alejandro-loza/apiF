package mx.finerio.api.services

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.AccountCredential
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.User
import mx.finerio.api.domain.repository.AccountCredentialRepository
import mx.finerio.api.domain.repository.AccountRepository
import mx.finerio.api.dtos.AccountData
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class AccountServiceCreateSpec extends Specification {

  def service = new AccountService()

  def credentialService = Mock( CredentialService )
  def accountCredentialRepository = Mock( AccountCredentialRepository )
  def accountRepository = Mock( AccountRepository )

  def setup() {

    service.credentialService = credentialService
    service.accountCredentialRepository = accountCredentialRepository
    service.accountRepository = accountRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.create( accountData )
    then:
      1 * credentialService.findAndValidate( _ as String ) >>
          new Credential( institution: institution, user: user )
      1 * credentialService.validateUserCredential( _ as Credential, _ as String ) >>
          new Credential( institution: institution, user: user )
      1 * accountRepository.findFirstByInstitutionAndUserAndNumberOrderByDateCreatedDesc(
          _ as FinancialInstitution, _ as User, _ as String )
      1 * accountRepository.findFirstByInstitutionAndUserAndNumberLikeOrderByDateCreatedDesc(
          _ as FinancialInstitution, _ as User, _ as String )
      1 * accountRepository.save( _ as Account )
      1 * accountCredentialRepository.findAllByAccountAndCredential(
          _ as Account, _ as Credential )
      1 * accountCredentialRepository.save( _ as AccountCredential )
      result instanceof Account
      result.nature == 'Cuenta'
    where:
      accountData = getAccountData()
      institution = new FinancialInstitution( code: 'CODE' )
      user = new User()

  }

  def "previous account found"() {

    when:
      def result = service.create( accountData )
    then:
      1 * credentialService.findAndValidate( _ as String ) >>
          new Credential( institution: institution, user: user )
      1 * credentialService.validateUserCredential( _ as Credential, _ as String ) >>
          new Credential( institution: institution, user: user )
      1 * accountRepository.findFirstByInstitutionAndUserAndNumberOrderByDateCreatedDesc(
          _ as FinancialInstitution, _ as User, _ as String ) >>
          new Account()
      0 * accountRepository.findFirstByInstitutionAndUserAndNumberLikeOrderByDateCreatedDesc(
          _ as FinancialInstitution, _ as User, _ as String )
      1 * accountRepository.save( _ as Account )
      1 * accountCredentialRepository.findAllByAccountAndCredential(
          _ as Account, _ as Credential )
      1 * accountCredentialRepository.save( _ as AccountCredential )
      result instanceof Account
    where:
      accountData = getAccountData()
      institution = new FinancialInstitution( code: 'CODE' )
      user = new User()

  }

  def "previous account found (LIKE comparison)"() {

    when:
      def result = service.create( accountData )
    then:
      1 * credentialService.findAndValidate( _ as String ) >>
          new Credential( institution: institution, user: user )
      1 * credentialService.validateUserCredential( _ as Credential, _ as String ) >>
          new Credential( institution: institution, user: user )
      1 * accountRepository.findFirstByInstitutionAndUserAndNumberOrderByDateCreatedDesc(
          _ as FinancialInstitution, _ as User, _ as String )
      1 * accountRepository.findFirstByInstitutionAndUserAndNumberLikeOrderByDateCreatedDesc(
          _ as FinancialInstitution, _ as User, _ as String ) >>
          new Account()
      1 * accountRepository.save( _ as Account )
      1 * accountCredentialRepository.findAllByAccountAndCredential(
          _ as Account, _ as Credential )
      1 * accountCredentialRepository.save( _ as AccountCredential )
      result instanceof Account
    where:
      accountData = getAccountData()
      institution = new FinancialInstitution( code: 'CODE' )
      user = new User()

  }

  def "previous accountCredential found"() {

    when:
      def result = service.create( accountData )
    then:
      1 * credentialService.findAndValidate( _ as String ) >>
          new Credential( institution: institution, user: user )
      1 * credentialService.validateUserCredential( _ as Credential, _ as String ) >>
          new Credential( institution: institution, user: user )
      1 * accountRepository.findFirstByInstitutionAndUserAndNumberOrderByDateCreatedDesc(
          _ as FinancialInstitution, _ as User, _ as String )
      1 * accountRepository.findFirstByInstitutionAndUserAndNumberLikeOrderByDateCreatedDesc(
          _ as FinancialInstitution, _ as User, _ as String )
      1 * accountRepository.save( _ as Account )
      1 * accountCredentialRepository.findAllByAccountAndCredential(
          _ as Account, _ as Credential ) >> [ new AccountCredential() ]
      0 * accountCredentialRepository.save( _ as AccountCredential )
      result instanceof Account
    where:
      accountData = getAccountData()
      institution = new FinancialInstitution( code: 'CODE' )
      user = new User()

  }

  def "account nature not mapped"() {

    given:
      accountData.nature = myNature
    when:
      def result = service.create( accountData )
    then:
      1 * credentialService.findAndValidate( _ as String ) >>
          new Credential( institution: institution, user: user )
      1 * credentialService.validateUserCredential( _ as Credential, _ as String ) >>
          new Credential( institution: institution, user: user )
      1 * accountRepository.findFirstByInstitutionAndUserAndNumberOrderByDateCreatedDesc(
          _ as FinancialInstitution, _ as User, _ as String )
      1 * accountRepository.findFirstByInstitutionAndUserAndNumberLikeOrderByDateCreatedDesc(
          _ as FinancialInstitution, _ as User, _ as String )
      1 * accountRepository.save( _ as Account )
      1 * accountCredentialRepository.findAllByAccountAndCredential(
          _ as Account, _ as Credential )
      1 * accountCredentialRepository.save( _ as AccountCredential )
      result instanceof Account
      result.nature == myNature
    where:
      accountData = getAccountData()
      myNature = 'something'
      institution = new FinancialInstitution( code: 'CODE' )
      user = new User()

  }

  def "parameter 'accountData' is null"() {

    when:
      service.create( accountData )
    then:
      BadImplementationException e = thrown()
      e.message == 'accountService.create.accountData.null'
    where:
      accountData = null

  }

  private AccountData getAccountData() throws Exception {

    new AccountData(
      user_id: 'user_id',
      credential_id: 'credential_id',
      name: 'name',
      nature: 'account'
    )

  }

}
