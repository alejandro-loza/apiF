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

    def cleanedName = params.request.name.replace( '&#092;u00f3', '\u00F3' ).trim()
    def number = getNumber( credential.institution, params.request.extra_data )
        ?: cleanedName
    Account account = findDuplicated(
        credential.institution,
        credential.user,
        number,
        cleanedName
        ) ?: new Account()
    account.name = cleanedName
    account.version = 0
    account.clazz = 'mx.com.glider.dinerio.Account'
    account.institution = credential.institution
    account.number = number
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

  String getNumber( FinancialInstitution institution, Map extraData )
      throws Exception {

    if ( institution.code == 'SANTANDER' ) {
      return extraData.number ?: extraData.tarjeta
    } else if ( institution.code == 'HSBC' ) {
      return extraData.number
    } else if ( institution.code == 'BANORTE' ) {
      return "***${extraData.short_number}"
    }

    null

  }

  String getMaskedNumber( FinancialInstitution institution, String number )
      throws Exception {

    if ( institution.code == 'SANTANDER' ) {

      if ( number.size() == 11 ) {
        return "${number[ 0..1 ]}%${number[ 7..10 ]}"
      } else if ( number.size() == 16 ) {
        return "${number[ 0..3 ]}%${number[ 12..15 ]}"
      }

    } else if ( institution.code == 'HSBC' && number.size() >= 8 ) {
      def size = number.size()
      return "${number[ 0..3 ]}%${ number[ (size - 4)..(size - 1) ]}"
    }

    number

  }

  Account findDuplicated( FinancialInstitution institution, User user,
      String number, String name ) throws Exception {

    validateFindDuplicatedInput( institution, user, number, name )

    def instance = accountRepository.findByInstitutionAndUserAndNumberAndDeleted(
        institution, user, number, false )

    if ( !instance ) {
      instance = accountRepository.findByInstitutionAndUserAndNumberLikeAndDeleted(
        institution, user, getMaskedNumber( institution, number ), false )
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
