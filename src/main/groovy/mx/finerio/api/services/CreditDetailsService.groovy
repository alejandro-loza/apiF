package mx.finerio.api.services

import groovy.json.JsonBuilder

import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.domain.repository.*
import mx.finerio.api.domain.*
import mx.finerio.api.dtos.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CreditDetailsService {

  @Autowired
  AccountService accountService

  @Autowired
  CreditDetailsRepository creditDetailsRepository

  CreditDetails create( CreditDetailsDto creditDetailsDto, Account account ) throws Exception {

    if ( !creditDetailsDto ) {
      throw new BadImplementationException(
          'creditDetailsService.create.creditDetailsDto.null' )
    }
 
    if ( !account ) {
      throw new BadImplementationException(
          'creditDetailsService.create.account.null' )
    }
 
    def instance = creditDetailsRepository.findByAccount( account )

    if ( instance ) {
      throw new BadRequestException( 'creditDetails.create.exists' )
    }

    instance = new CreditDetails()
    instance.account = account
    instance.closingDate = new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",
        creditDetailsDto.closing_date ) ?: new Date()
    instance.nonInterestPayment = creditDetailsDto.non_interest_payment
    instance.statementBalance = creditDetailsDto.statement_balance
    instance.minimumPayment = creditDetailsDto.minimum_payment
    instance.limitCredit = creditDetailsDto.credit_limit
    instance.dueDate = new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",
        creditDetailsDto.due_date ) ?: new Date()
    instance.lastClosingDate = new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",
        creditDetailsDto.last_closing_date ) ?: new Date()
    instance.annualPercentageRate = creditDetailsDto.annual_porcentage_rate
    instance.cardNumber = creditDetailsDto.card_number
    creditDetailsRepository.save( instance )
 
    instance

  }


}
