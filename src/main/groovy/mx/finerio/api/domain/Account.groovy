package mx.finerio.api.domain

import groovy.transform.ToString

import javax.persistence.*
import javax.validation.constraints.*

import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'account')
@ToString(includeNames = true, includePackage = false)
class Account {

  @Id
  @Column(name = 'id', nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String id

    enum Type {
        DEBIT, CREDIT
    }

    @Column(name = 'version', nullable = false)
    Long version

    @Column(name = 'class', nullable = false)
    String clazz

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'user_id', nullable = false)
  User user

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = 'institution_id', nullable = false)
  FinancialInstitution institution

  @Column(name = 'name', nullable = false, length = 255)
  String name

  @Column(name = 'number', nullable = true, length = 255)
  String number

  @Column(name = 'date_created', nullable = false)
  Date dateCreated

  @Column(name = 'last_updated', nullable = false)
  Date lastUpdated

  @Column(name = 'balance', nullable = false )
  BigDecimal balance

  @Column(name = 'pending_deposit_amount', nullable = false )
  BigDecimal pendingDepositAmount

  @Column(name = 'pending_charge_amount', nullable = false )
  BigDecimal pendingChargeAmount

  @Column(name = 'last_balance', nullable = false )
  BigDecimal lastBalance

  @Column(name = 'nature', nullable = true, length = 255)
  String nature

  @Column(name = 'deleted', nullable = true, length = 255)
  boolean deleted = false

  @Column(name = 'date_deleted', nullable = false)
  Date dateDeleted

}
