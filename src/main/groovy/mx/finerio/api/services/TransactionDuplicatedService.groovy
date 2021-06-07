package mx.finerio.api.services

import mx.finerio.api.contants.Constants
import mx.finerio.api.domain.Transaction
import mx.finerio.api.domain.repository.TransactionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

@Service
class TransactionDuplicatedService {

  @Autowired
  TransactionsApiService transactionsApiService

  @Autowired
  TransactionRepository transactionRepository

  @Value( '${categories.atm.id}' )
  private String atmId

  @Value( '${duplicated.description-porcentage}' )
  private int descriptionPercentage

  @Value( '${duplicated.days-difference}' )
  private int daysDifference

  @Transactional
  void duplicatedTransaction(Transaction transaction ) throws Exception {

    if ( isDepositCreditAccount(transaction) || isValidAtmTrans(transaction) ) {
      updateDuplicatedTrans(transaction)
      return
    }
    def dupTrans = transactionRepository
            .findAllByAccountAndAmountAndChargeAndDateDeletedIsNull(
                    transaction.account, transaction.amount, transaction.charge )
    if ( dupTrans ) {
      def dupTransMinDays = dupTrans
              .stream()
              .filter({ trans -> TimeUnit.DAYS.convert(
                      Math.abs(trans.bankDate.time - transaction.bankDate.time),
                      TimeUnit.MILLISECONDS) <= daysDifference && transaction.id != trans.id })
              .collect(Collectors.toList())
      findPercentByTransactionsApi( dupTransMinDays, transaction )
    }

  }

  private void findPercentByTransactionsApi( List dupTransMinDays, Transaction transaction) {
    if ( dupTransMinDays.size() >= 1 ){
      Map params = [:]
      params.endpoint = "searchAll"
      params.params = [ list: dupTransMinDays.description.join(",") ]
      def restFind = transactionsApiService.find( params )
      if( !restFind?.results ){
        return
      }
      def reasonResponse = restFind.results.findAll{
        ( it.reason.data != "Not found" ) || ( it.similarity.percent >= descriptionPercentage )
      }
      if( reasonResponse ){
        updateDuplicatedTrans( transaction )
      }
    }
  }

  private void updateDuplicatedTrans(Transaction transaction) {
    transaction.duplicated = true
    transaction.lastUpdated = new Timestamp(System.currentTimeMillis())
    transactionRepository.save(transaction)
  }

  private boolean isValidAtmTrans( Transaction transaction ) {
    transaction.charge && transaction?.category?.id.equals(atmId) && isValidAmount(transaction.amount)
  }

  private boolean isDepositCreditAccount( Transaction transaction ) {
    !transaction.charge && transaction.account?.nature.equals( Constants.CREDIT )
  }

  private boolean isValidAmount( BigDecimal amount ) {
    BigDecimal.ZERO.compareTo(
            amount.remainder( Constants.ATM_MULTIPLE )) == Constants.ZERO
  }

}
