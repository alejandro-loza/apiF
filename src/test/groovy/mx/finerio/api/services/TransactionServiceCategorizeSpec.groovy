package mx.finerio.api.services

import mx.finerio.api.domain.Category
import mx.finerio.api.domain.Transaction
import mx.finerio.api.domain.repository.TransactionRepository
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class TransactionServiceCategorizeSpec extends Specification {

  def service = new TransactionService()

  def categorizerService = Mock( CategorizerService )
  def categoryService = Mock( CategoryService )
  def cleanerRestService = Mock( CleanerRestService )
  def transactionRepository = Mock( TransactionRepository )

  def setup() {

    service.categorizerService = categorizerService
    service.categoryService = categoryService
    service.cleanerRestService = cleanerRestService
    service.transactionRepository = transactionRepository

  }

  def "everything was OK"() {

    when:
      service.categorize( transaction )
    then:
      1 * cleanerRestService.clean( _ as String ) >> 'cleanedText'
      1 * categorizerService.search( _ as String ) >>
          [ categoryId: 'categoryId' ]
      1 * categoryService.findOne( _ as String ) >> new Category()
      1 * transactionRepository.save( _ as Transaction )
    where:
      transaction = getTransaction()

  }

  def "category not found"() {

    when:
      service.categorize( transaction )
    then:
      1 * cleanerRestService.clean( _ as String ) >> 'cleanedText'
      1 * categorizerService.search( _ as String ) >> [:]
      0 * categoryService.findOne( _ as String ) >> new Category()
      0 * transactionRepository.save( _ as Transaction )
    where:
      transaction = getTransaction()

  }

  def "parameter 'transaction' is null"() {

    when:
      service.categorize( transaction )
    then:
      BadImplementationException e = thrown()
      e.message == 'transactionService.categorize.transaction.null'
    where:
      transaction = null

  }

  private Transaction getTransaction() throws Exception {

    new Transaction(
      description: 'description'
    )

  }

}
