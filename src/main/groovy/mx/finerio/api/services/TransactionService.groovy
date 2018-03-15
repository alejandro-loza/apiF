package mx.finerio.api.services

import java.sql.Timestamp

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Transaction
import mx.finerio.api.domain.repository.TransactionRepository
import mx.finerio.api.dtos.Transaction as TransactionCreateDto
import mx.finerio.api.dtos.TransactionData
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionService {

  @Autowired
  AccountService accountService

  @Autowired
  CategorizerService categorizerService  

  @Autowired
  CategoryService categoryService  

  @Autowired
  CleanerRestService cleanerRestService  

  @Autowired
  Sha1Service sha1Service

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

  void categorize( Transaction transaction ) throws Exception {

    if ( !transaction ) {
      throw new BadImplementationException(
          'transactionService.categorize.transaction.null' )
    }

    def cleanedText = cleanerRestService.clean( transaction.description )
    def categorizerResult = categorizerService.search( cleanedText )

    if ( !categorizerResult?.categoryId ) {
      return
    }

    def category = categoryService.findOne( categorizerResult.categoryId )
    transaction.category = category
    transactionRepository.save( transaction )

  }

  private Transaction create( Account account,
      TransactionCreateDto transactionCreateDto ) throws Exception {

    def date = new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",
        transactionCreateDto.made_on ) ?: new Date()
    def description = transactionCreateDto.description.take( 255 )
    def rawAmount = transactionCreateDto.amount
    def amount = new BigDecimal( rawAmount ).abs().setScale( 2, BigDecimal.ROUND_HALF_UP )
    def charge = rawAmount < 0
    def hash = getHash( date, description, amount, charge )
    def instance = transactionRepository.
        findByAccountAndHashAndDateDeletedIsNull( account, hash )
    if ( instance ) { return null }
    def transaction = new Transaction()
    transaction.account = account
    transaction.bankDate = new Timestamp( date.time )
    transaction.description = description
    transaction.amount = amount
    transaction.charge = charge
    transaction.hash = hash
    def now = new Timestamp( new Date().time )
    transaction.dateCreated = now
    transaction.lastUpdated = now
    transactionRepository.save( transaction )

  }

  private byte[] getHash( Date date, String description, BigDecimal amount,
      Boolean charge ) throws Exception {

    def input = "||${date.format("yyyyMMddHHmmss")}" +
        "|${description}|${amount}|${charge}||"
    sha1Service.encrypt( input.toString() )

  }

}
