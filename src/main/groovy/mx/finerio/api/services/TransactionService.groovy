package mx.finerio.api.services

import java.sql.Timestamp

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Transaction
import mx.finerio.api.domain.TransactionSpecs
import mx.finerio.api.domain.repository.TransactionRepository
import mx.finerio.api.dtos.DuplicatedTransactionDto
import mx.finerio.api.dtos.Transaction as TransactionCreateDto
import mx.finerio.api.dtos.TransactionListDto
import mx.finerio.api.dtos.TransactionData
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService {

  @Autowired
  AccountService accountService

  @Autowired
  CategorizerService categorizerService  

  @Autowired
  CategoryService categoryService  

  @Autowired
  CleanerService cleanerService  

  @Autowired
  DuplicatedTransactionsValidatorService duplicatedTransactionsValidatorService

  @Autowired
  ListService listService

  @Autowired
  TransactionRepository transactionRepository

  List createAll( TransactionData transactionData ) throws Exception {

    if ( !transactionData ) {
      throw new BadImplementationException(
          'transactionService.createAll.transactionData.null' )
    }

    def account = accountService.findById( transactionData.account_id )

    transactionData.transactions.findResults { transaction ->
      create( account, transaction )
    }

  }

  Map findAll( Map params ) throws Exception {

    if ( params == null ) {
      throw new BadImplementationException(
          'transactionService.findAll.params.null' )
    }

    def dto = getFindAllDto( params )
    def spec = TransactionSpecs.findAll( dto )
    listService.findAll( dto, transactionRepository, spec )

  }

  Transaction findOne( Long id ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'transactionService.findOne.id.null' )
    }

    def instance = transactionRepository.findOne( id )

    if ( !instance || instance.dateDeleted ) {
      throw new InstanceNotFoundException( 'transaction.not.found' )
    }

    try {
      accountService.findOne( instance?.account?.id )
    } catch ( InstanceNotFoundException e ) {
      throw new InstanceNotFoundException( 'transaction.not.found' )
    }

    instance

  }

  Map getFields( Transaction transaction ) throws Exception {

    if ( !transaction ) {
      throw new BadImplementationException(
          'transactionService.getFields.transaction.null' )
    }

    [ id: transaction.id, description: transaction.description,
        cleanedDescription: transaction.cleanedDescription,
        amount: transaction.amount, isCharge: transaction.charge,
        date: transaction.bankDate, categoryId: transaction.category?.id ]

  }

  void categorize( Transaction transaction ) throws Exception {

    if ( !transaction ) {
      throw new BadImplementationException(
          'transactionService.categorize.transaction.null' )
    }

    try {

      def cleanedText = cleanerService.clean( transaction.description,
          !transaction.charge )
      transaction.cleanedDescription = cleanedText
      def categorizerResult = categorizerService.search( cleanedText,
          !transaction.charge )

      if ( !categorizerResult?.categoryId ) {
        return
      }

      def category = categoryService.findOne( categorizerResult.categoryId )
      transaction.category = category
      transactionRepository.save( transaction )

    } catch ( Exception e ) {}

  }

  @Transactional
  void deleteAllByAccount( Account account ) throws Exception {

    def transactions = this.findAll( [ accountId: account.id ] )?.data

    for ( transaction in transactions ) {
      transaction.dateDeleted = new Timestamp( new Date().time )
      transactionRepository.save( transaction )
    }

  }

  private Transaction create( Account account,
      TransactionCreateDto transactionCreateDto ) throws Exception {

    def date = new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",
        transactionCreateDto.made_on ) ?: new Date()
    def description = transactionCreateDto.description.take( 255 )
    def rawAmount = transactionCreateDto.amount
    def amount = new BigDecimal( rawAmount ).abs().setScale(
        2, BigDecimal.ROUND_HALF_UP )
    def charge = rawAmount < 0
    def transaction = new Transaction()
    transaction.account = account
    transaction.bankDate = new Timestamp( date.time )
    transaction.description = description
    transaction.amount = amount
    transaction.charge = charge
    transaction.scraperId =
        transactionCreateDto?.extra_data?.transaction_Id?.take( 255 )
    if ( isDuplicated( transaction ) ) { return null }
    def now = new Timestamp( new Date().time )
    transaction.dateCreated = now
    transaction.lastUpdated = now
    return transactionRepository.save( transaction )

  }

  private TransactionListDto getFindAllDto( Map params ) throws Exception {

    if ( !params.accountId ) {
      throw new BadRequestException( 'transaction.findAll.accountId.null' )
    }

    def dto = new TransactionListDto()
    dto.account = accountService.findOne( params.accountId )
    listService.validateFindAllDto( dto, params )

    if ( params.cursor ) {
      try {
        findOne( params.cursor as Long )
        dto.id = params.cursor as Long
      } catch ( NumberFormatException e ) {
        throw new BadRequestException( 'cursor.invalid' )
      }
    }

    dto

  }

  private boolean isDuplicated( Transaction transaction )
      throws Exception {

    def transactionDto = new DuplicatedTransactionDto(
      description: transaction.description,
      amount: transaction.amount,
      deposit: !transaction.charge,
      transactionId: transaction.scraperId
    )
    def transactionsSameDay = findAllTransactionsByAccountAndDate(
        transaction.account, transaction.bankDate, 1 )
    def duplicatedFlag1 = duplicatedTransactionsValidatorService.
        validateTransactionsFromSameDate( transactionDto,
        transactionsSameDay )
    def transactionsDifferentDay = findAllTransactionsByAccountAndDate(
        transaction.account, transaction.bankDate, 5 )
    def duplicatedFlag2 = duplicatedTransactionsValidatorService.
        validateTransactions( transactionDto, transactionsDifferentDay )
    return duplicatedFlag1 || duplicatedFlag2

  }

  private List<DuplicatedTransactionDto> findAllTransactionsByAccountAndDate(
      Account account, Date date, int daysBefore ) throws Exception {

    def format = 'yyyy-MM-dd'
    def calTo = Calendar.instance
    calTo.time = date
    calTo.add( Calendar.DAY_OF_MONTH, 1 )
    def to = Date.parse( format, calTo.time.format( format ) )
    def calFrom = Calendar.instance
    calFrom.time = to
    calFrom.add( Calendar.DAY_OF_MONTH, -daysBefore )
    def from = Date.parse( format, calFrom.time.format( format ) )
    def transactions = transactionRepository.
        findAllByAccountAndBankDateGreaterThanEqualAndBankDateLessThanAndDateDeletedIsNull(
        account, from, to )
    return parseTransactions( transactions )

  }

  private List<DuplicatedTransactionDto> parseTransactions(
      List<Transaction> transactions ) throws Exception {

    def parsedTransactions = []

    for ( transaction in transactions ) {

      def parsedTransaction = new DuplicatedTransactionDto()
      parsedTransaction.description = transaction.description
      parsedTransaction.amount = transaction.amount
      parsedTransaction.deposit = !transaction.charge
      parsedTransaction.transactionId = transaction.scraperId
      parsedTransactions << parsedTransaction

    }

    return parsedTransactions

  }

}
