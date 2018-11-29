package mx.finerio.api.services

import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.dtos.AccountData
import mx.finerio.api.dtos.AccountListDto

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
  ListService listService

  @Autowired
  SecurityService securityService

  @Autowired
  AccountRepository accountRepository

  @Autowired
  AccountCredentialRepository accountCredentialRepository

  @Autowired
  CreditDetailsService creditDetailsService

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
    def account = findDuplicated( credential, number,
        accountData.id ) ?: new Account() 
    account.idBank = accountData.id
    account.name = cleanedName
    account.institution = credential.institution
    account.number = number
    account.user = credential.user
    account.balance = accountData.balance
    account.nature = NATURES[ accountData.nature ] ?: accountData.nature
    account.dateCreated = account.dateCreated ?: new Date()
    account.lastUpdated = new Date()
    accountRepository.save( account )
    createAccountCredential( account, credential )
    if( accountData.credit_card_detail && accountData.is_credit_card ){
      creditDetailsService.create( accountData.credit_card_detail, account )
    }
    account

  }

  Map findAll( Map params ) throws Exception {

    if ( params == null ) {
      throw new BadImplementationException(
          'accountService.findAll.params.null' )
    }

    def dto = getFindAllDto( params )
    def spec = AccountCredentialSpecs.findAll( dto )
    def results = listService.findAll( dto, accountCredentialRepository, spec )
    results.data = results.data*.account

    if ( results.nextCursor ) {
      results.nextCursor = accountCredentialRepository.findOne(
          results.nextCursor ).account.id
    }

    results

  }

  Account findOne( String id ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'accountService.findOne.id.null' )
    }

    def client = securityService.getCurrent()
    def instance = accountCredentialRepository.findFirstByAccountId( id )

    if ( !instance || instance?.credential?.customer?.client?.id != client.id ||
        instance?.account?.dateDeleted ) {
      throw new InstanceNotFoundException( 'account.not.found' )
    }

    instance.account

  }

  List findAllByUser( Account account ) throws Exception {

    if ( !account ) {
      throw new BadImplementationException(
          'accountService.findAllByUser.account.null' )
    }

    def acc = findById( account.id )
    def accounts = accountRepository.findByUserAndDeleted( acc.user, false )

    if ( !accounts ) {
      throw new InstanceNotFoundException( 'accounts.not.found' )
    }
    accounts
    
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

  Map getFields( Account account ) throws Exception {

    if ( !account ) {
      throw new BadImplementationException(
          'accountService.getFields.account.null' )
    }
 
    [ id: account.id, name: account.name, number: account.number,
        balance: account.balance, type: account.nature,
        dateCreated: account.dateCreated ]

  }

  private String getAccountName( String originalAccountName )
      throws Exception {
    originalAccountName.replace( '&#092;u00f3', '\u00F3' ).trim()
  }

  private String getNumber( FinancialInstitution institution, Map extraData )
      throws Exception {

    if ( institution.code == 'SANTANDER' ) {
      return extraData.number ?: extraData.tarjeta
    } else if ( institution.code == 'HSBC' || institution.code == 'INVEX' ||
        institution.code == 'BBVA' ) {
      return extraData.number
    } else if ( institution.code == 'BANORTE' ) {
      return "***${extraData.short_number}"
    } else if ( institution.code == 'AMEX' ) {
      return "XXX-${extraData.card_number_display}"
    }

    null

  }

  private Account findDuplicated( Credential credential, String number,
      String id ) throws Exception {

    def institution = credential.institution
    def user = credential.user
    def instance 
    if ( id ) {
      instance = accountRepository.findFirstByInstitutionAndUserAndIdBankOrderByDateCreatedDesc(
        institution, user, id )
    }
    instance = validateFinder( credential, id, instance )

    if ( !instance ) {
      instance= accountRepository.findFirstByInstitutionAndUserAndNumberOrderByDateCreatedDesc(
        institution, user, number )
    }
    instance = validateFinder( credential, id, instance )

    if ( !instance ) {
      instance = accountRepository.findFirstByInstitutionAndUserAndNumberLikeOrderByDateCreatedDesc(
        institution, user, getMaskedNumber( institution, number ) )
    }
    validateFinder( credential, id, instance )

  }

  private Account validateFinder( Credential credential, String id, Account instance){

    if ( instance && id ) {
      if( instance.idBank && instance.idBank != id ){ instance = null }
    }

    if ( instance?.deleted && instance?.dateDeleted && 
        !accountCredentialRepository.findAllByAccountAndCredential(
        instance, credential ) ) {
      instance = null
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
    } else if ( institution.code == 'BBVA' && number.size() >= 5 ) {
      def size = number.size()
      return "%${number[ (size - 4)..(size - 1) ]}"
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

  private AccountListDto getFindAllDto( Map params ) throws Exception {

    if ( !params.credentialId ) {
      throw new BadRequestException( 'account.findAll.credentialId.null' )
    }

    def dto = new AccountListDto()
    dto.credential = credentialService.findOne( params.credentialId )
    listService.validateFindAllDto( dto, params )

    if ( params.cursor ) {
      def cursorInstance = findOne( params.cursor )
      dto.dateCreated = cursorInstance.dateCreated
    }

    dto

  }

}
