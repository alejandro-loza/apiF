package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.dtos.AccountData

import org.springframework.data.domain.Pageable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountService {

  @Autowired
  CredentialService credentialService

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

  Account create( AccountData accountData ) {

    if ( !accountData ) {
      throw new BadImplementationException(
          'accountService.create.accountData.null' )
    }

    def credential = credentialService.findAndValidate( accountData.credential_id )
    def cleanedName = getAccountName( accountData.name )
    def number = getNumber( credential.institution, accountData.extra_data )
        ?: cleanedName
    def account = findDuplicated( credential.institution, credential.user,
        number, cleanedName ) ?: new Account()
    account.name = cleanedName
    account.institution = credential.institution
    account.number = number
    account.user = credential.user
    account.balance = accountData.balance
    account.nature = NATURES[ accountData.nature ]
    account.dateCreated = account.dateCreated ?: new Date()
    account.lastUpdated = new Date()
    accountRepository.save( account )
    createAccountCredential( account, credential )
    account

  }

  Account findById( String id ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'accountService.findById.id.null' )
    }

    def account = accountRepository.findById( id )

    if ( !account ) {
      throw new InstanceNotFoundException( 'account.not.found' )
    }

    account
    
  }  

  def findByCredentialId( String id, Pageable pageable ){

    def credential = credentialPersistenceService.findOne( id )
    accountRepository.getByCredentialId( credential, pageable )
  }

  private String getAccountName( String originalAccountName )
      throws Exception {
    originalAccountName.replace( '&#092;u00f3', '\u00F3' ).trim()
  }

  private String getNumber( FinancialInstitution institution, Map extraData )
      throws Exception {

    if ( institution.code == 'SANTANDER' ) {
      return extraData.number ?: extraData.tarjeta
    } else if ( institution.code == 'HSBC' || institution.code == 'INVEX' ) {
      return extraData.number
    } else if ( institution.code == 'BANORTE' ) {
      return "***${extraData.short_number}"
    } else if ( institution.code == 'AMEX' ) {
      return "XXX-${extraData.account_token}"
    }

    null

  }

  private Account findDuplicated( FinancialInstitution institution, User user,
      String number, String name ) throws Exception {

    def instance = accountRepository.findByInstitutionAndUserAndNumberAndDeleted(
        institution, user, number, false )

    if ( !instance ) {
      instance = accountRepository.findByInstitutionAndUserAndNumberLikeAndDeleted(
        institution, user, getMaskedNumber( institution, number ), false )
    }

    instance

  }

  private String getMaskedNumber( FinancialInstitution institution,
      String number ) throws Exception {

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

  private void createAccountCredential( Account account, Credential credential )
      throws Exception {

    def accountCredential =
        accountCredentialRepository.findAllByAccountAndCredential(
        account, credential )

    if ( accountCredential ) {
      return
    }

    accountCredential = new AccountCredential()
    accountCredential.account = account
    accountCredential.credential = credential
    accountCredential.dateCreated = new Date()
    accountCredential.lastUpdated = new Date()
    accountCredential.version = 0
    accountCredentialRepository.save( accountCredential )

  }

}
