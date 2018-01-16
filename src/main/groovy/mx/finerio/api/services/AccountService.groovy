package mx.finerio.api.services

import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*

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
    credential.version = 0
    if ( !credential ) {
      throw new InstanceNotFoundException(
          'account.createAccount.credential.null' )
    }

    Account account = findDuplicated(
	credential.institution,
	credential.user, 
	params.request.name, 
	params.request.name
	) ?: new Account()
    if( params.request.extra_data ){
      account.name = params.request.extra_data.account_name
    }else{
    account.name = params.request.name
    }
    account.version=0
    account.clazz = 'mx.com.glider.dinerio.Account'
    account.institution = credential.institution
    account.number = params.request.name
    account.user = credential.user
    account.balance = params.request.balance
    account.nature = NATURES[ params.request.nature ]
    account.dateCreated = new Date() 
    account.lastUpdated = new Date()
    if ( account.deleted )  return
    accountRepository.save(account)
    createAccountCredential(account,credential)
    account
  }

  void createAccountCredential(Account account, Credential credential){
    def accountCredential= accountCredentialRepository.findByAccountAndCredential(account, credential)
    if( !accountCredential ){
      accountCredential = new AccountCredential()
      accountCredential.account = account
      accountCredential.credential = credential
      accountCredential.dateCreated = new Date()
      accountCredential.lastUpdated = new Date()
      accountCredential.version = 0
      accountCredentialRepository.save(accountCredential)
    }
  }

  Account findById( String id ){
    accountRepository.findById( id )
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
