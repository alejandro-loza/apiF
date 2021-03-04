package mx.finerio.api.services

import mx.finerio.api.dtos.ApiTransactionDto
import mx.finerio.api.dtos.BalanceDto
import mx.finerio.api.dtos.BalanceRowDto
import mx.finerio.api.domain.repository.TransactionRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountBalanceService {

  @Autowired
  AccountService accountService

  @Autowired
  TransactionService transactionService

  @Autowired
  TransactionRepository transactionRepository

  BalanceDto getBalanceByAccount( String accountId ) throws Exception {

    def account = accountService.findOne( accountId )
    def transactions =
        transactionRepository.findAllByAccountAndDateDeletedIsNull( account )
        .sort { tr1, tr2 -> tr2.bankDate <=> tr1.bankDate }
    def balanceDto = new BalanceDto( lastBalance: account.balance )
    def currentBalance = new BigDecimal( account.balance )

    for ( transaction in transactions ) {

      def amount = transaction.charge ? -transaction.amount : transaction.amount
      def balanceBefore = currentBalance - amount
      def balanceAfter = new BigDecimal( currentBalance )
      currentBalance = new BigDecimal( balanceBefore )
      balanceDto.history << new BalanceRowDto(
        transaction: new ApiTransactionDto(
            transactionService.getFields( transaction ) ),
        amountBeforeTransaction: balanceBefore.setScale(
            2, BigDecimal.ROUND_HALF_UP ),
        amountAfterTransaction: balanceAfter.setScale(
            2, BigDecimal.ROUND_HALF_UP )
      )

    }

    setAverage( balanceDto )
    return balanceDto

  }

  private void setAverage( BalanceDto balanceDto ) throws Exception  {

    def amount = balanceDto.history*.amountAfterTransaction.sum()
    def total = balanceDto.history.size()
    def average = 0.0

    if ( total != 0 ) {
      average  = amount / total
    }

    balanceDto.average = average.setScale( 2, BigDecimal.ROUND_HALF_UP )

  }

}

