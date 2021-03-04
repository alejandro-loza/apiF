package mx.finerio.api.services

import mx.finerio.api.dtos.ApiTransactionDto
import mx.finerio.api.domain.repository.TransactionRepository

import org.springframework.beans.factory.annotation.Autowired

class InsightsService {

  @Autowired
  TransactionRepository transactionRepository

  protected List<ApiTransactionDto> getTransactions( Long customerId )
    throws Exception {

    def rawTransactions = transactionRepository.findAllByCustomerId(
      customerId )
    def transactions = []

    for ( transaction in rawTransactions ) {

      transactions << new ApiTransactionDto(
        id: transaction[ 0 ] as Long,
        description: transaction[ 1 ] as String,
        cleanedDescription: transaction[ 2 ] as String,
        amount: transaction[ 3 ] as BigDecimal,
        isCharge: transaction[ 4 ] as Boolean,
        date: transaction[ 5 ] as Date,
        categoryId: transaction[ 6 ] as String,
        duplicated: transaction[ 7 ] as Boolean,
        balance: transaction[ 8 ] as BigDecimal
      )

    }

    return transactions

  }

  protected Date getMainDate( Date date ) throws Exception {

    def cal = Calendar.getInstance()
    cal.time = date
    def year = cal.get( Calendar.YEAR )
    def month = cal.get( Calendar.MONTH ) + 1
    return Date.parse( 'yyyy-MM',
        "${year}-${month.toString().padLeft( 2, '0' )}" )

  }

  protected String getCategoryId( List<Category> categories,
      String subcategoryId ) throws Exception {

    def category = categories.find { it.id == subcategoryId }

    if ( category != null ) {
      return category.parent.id
    }

    return null

  }

}

