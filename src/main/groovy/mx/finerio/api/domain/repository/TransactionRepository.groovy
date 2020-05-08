package mx.finerio.api.domain.repository

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Transaction

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface TransactionRepository
    extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor {

  List<Transaction> findAllByAccountAndBankDateGreaterThanEqualAndBankDateLessThanAndDateDeletedIsNull(
        Account account, Date from, Date to )

}
