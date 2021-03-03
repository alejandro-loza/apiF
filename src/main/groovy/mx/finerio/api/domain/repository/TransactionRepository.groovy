package mx.finerio.api.domain.repository

import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.Transaction

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query

interface TransactionRepository
    extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor {

  List<Transaction> findAllByAccountAndBankDateGreaterThanEqualAndBankDateLessThanAndDateDeletedIsNull( Account account, Date from, Date to )

  @Query(value = "select tr.id, tr.description, tr.cleaned_description, tr.amount, tr.charge, tr.bank_date, tr.category_id, tr.duplicated, tr.balance from transactions as tr inner join account as ac on tr.account_id = ac.id inner join account_credential as acc on ac.id = acc.account_id inner join credential as cr on acc.credential_id = cr.id where cr.customer_id = ?1 and cr.date_deleted is null", nativeQuery = true)
  List findAllByCustomerId( Long customerId )

}
