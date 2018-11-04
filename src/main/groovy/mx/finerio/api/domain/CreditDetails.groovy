package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'credit_details')
@ToString(includePackage = false, includeNames = true)
class CreditDetails {

  @Id @GeneratedValue
  @Column(name = 'id', updatable = false)
  Long id

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'account_id', nullable = false)
  Account account

  @Column(name = 'closing_date', nullable = true)
  Date closingDate
  
  @Column(name = 'non_interest_payment', nullable = true)
  BigDecimal nonInterestPayment

  @Column(name = 'statement_balance', nullable = true)
  BigDecimal statementBalance

  @Column(name = 'minimum_payment', nullable = true)
  BigDecimal minimumPayment

  @Column(name = 'limit_credit', nullable = true)
  BigDecimal limitCredit

  @Column(name = 'due_date', nullable = true)
  Date dueDate

  @Column(name = 'last_closing_date', nullable = true)
  Date lastClosingDate

  @Column(name = 'annual_percentage_rate', nullable = true)
  BigDecimal annualPercentageRate

  @Column(name = 'card_number', nullable = false, length = 30)
  String cardNumber

}
