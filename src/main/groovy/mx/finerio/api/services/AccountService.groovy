package mx.finerio.api.services

import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*

import org.springframework.data.domain.Pageable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountService {

  @Autowired
  CredentialPersistenceService credentialPersistenceService

  @Autowired
  AccountRepository accountRepository

  @Autowired
  AccountCredentialRepository accountCredentialRepository

  private static final Map NATURES = [
    account: 'Cuenta',
    bonus: 'Bono',
    card: 'Tarjeta',
    checking: 'Cheques',
    credit: 'Cr\u00E9dito',
    credit_card: 'Cr\u00E9dito',
    debit_card: 'D\u00E9bito',
    ewallet: 'Monedero electr\u00F3nico',
    insurance: 'Seguro',
    investment: 'Inversi\u00F3n',
    loan: 'Pr\u00E9stamo',
    mortgage: 'Hipoteca',
    savings: 'Ahorros'
  ]

  Account createAccount(Map params){

    def credential = credentialPersistenceService.findOne( params.request.credential_id )

    if ( !credential ) {
      throw new InstanceNotFoundException(
          'account.createAccount.credential.null' )
    }

    def cleanedName = params.request.name.replace( '&#092;u00f3', '\u00F3' )
    Account account = findDuplicated(
	credential.institution,
	credential.user, 
	cleanedName,
	cleanedName
	) ?: new Account()
    if( params.request?.extra_data?.account_name ){
      account.name = params.request.extra_data.account_name
    }else{
    account.name = cleanedName
    }
    account.version = 0
    account.clazz = 'mx.com.glider.dinerio.Account'
    account.institution = credential.institution
    account.number = cleanedName
    account.user = credential.user
    account.balance = params.request.balance
    account.nature = NATURES[ params.request.nature ]
    account.dateCreated = account.dateCreated ?: new Date()
    account.lastUpdated = new Date()
    if ( account.deleted )  return
    accountRepository.save(account)
    createAccountCredential(account,credential)
    account
  }

  void createAccountCredential(Account account, Credential credential){
    def accountCredential= accountCredentialRepository.findAllByAccountAndCredential(account, credential)
    if( !accountCredential ){
      accountCredential = new AccountCredential()
      accountCredential.account = account
      accountCredential.credential = credential
      accountCredential.dateCreated = accountCredential.dateCreated ?: new Date()
      accountCredential.lastUpdated = new Date()
      accountCredential.version = 0
      accountCredentialRepository.save(accountCredential)
    }
  }

  Account findById( String id ){
    accountRepository.findById( id )
    
  }  

  def findByCredentialId( String id, Pageable pageable ){

    def credential = credentialPersistenceService.findOne( id )
    accountRepository.getByCredentialId( credential, pageable )
  }

  Account findDuplicated( FinancialInstitution institution, User user,
      String number, String name ) throws Exception {
    validateFindDuplicatedInput( institution, user, number, name )
    def instance = accountRepository.findByInstitutionAndUserAndNumberAndDeleted(
        institution, user, number, false )

    if ( !instance && institution.code == 'BNMX' ) {
      instance = accountRepository.findByInstitutionAndUserAndNameAndDeleted(
        institution, user, name, false )
    }

    if ( !instance && institution.code == 'BANORTE' ) {
      instance = accountRepository.findByInstitutionAndUserAndNumberLikeAndDeleted(
        institution, user, "%${number}", false )
    }

    instance

  }


  private void validateFindDuplicatedInput( FinancialInstitution institution,
      User user, String number, String name ) throws Exception {

    if ( !institution ) {
      throw new IllegalArgumentException(
          'account.findDuplicated.institution.null' )
    }

    if ( !user ) {
      throw new IllegalArgumentException(
          'account.findDuplicated.user.null' )
    }

    if ( !number ) {
      throw new IllegalArgumentException(
          'account.findDuplicated.number.blank' )
    }

    if ( !name ) {
      throw new IllegalArgumentException(
          'account.findDuplicated.name.blank' )
    }

  }


}
