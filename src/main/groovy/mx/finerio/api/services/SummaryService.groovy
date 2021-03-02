package mx.finerio.api.services

import mx.finerio.api.dtos.ApiTransactionDto
import mx.finerio.api.domain.repository.TransactionRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SummaryService {

  @Autowired
  CustomerService customerService

  @Autowired
  TransactionRepository transactionRepository

  void getSummaryByCustomer( Long customerId ) throws Exception {

    customerService.findOne( customerId )
    def transactions = getTransactions( customerId )

  }

  private List<ApiTransactionDto> getTransactions( Long customerId )
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

}

