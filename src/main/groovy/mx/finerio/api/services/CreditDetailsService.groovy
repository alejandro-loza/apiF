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
    def flag = nullAll( creditDetailsDto )
    if( flag ){  
      return null
    }
    createInstance( creditDetailsDto, account )

  }

  private CreditDetails createInstance( CreditDetailsDto creditDetailsDto, Account account ){

    def instance = creditDetailsRepository.findByAccountAndDateDeletedIsNull( account ) 
    if( !instance ){ 
      instance = new CreditDetails()
      instance.dateCreated = new Date()
    }
    instance.account = account
    instance.closingDate = creditDetailsDto.closing_date ? new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",
        creditDetailsDto.closing_date ) : null
    instance.nonInterestPayment = creditDetailsDto.non_interest_payment  
    instance.statementBalance = creditDetailsDto.statement_balance
    instance.minimumPayment = creditDetailsDto.minimum_payment  
    instance.limitCredit = creditDetailsDto.credit_limit 
    instance.dueDate = creditDetailsDto.due_date ? new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",
        creditDetailsDto.due_date ) : null
    instance.lastClosingDate = creditDetailsDto.last_closing_date ? new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",
        creditDetailsDto.last_closing_date ) : null
    instance.annualPercentageRate = creditDetailsDto.annual_porcentage_rate
    if( creditDetailsDto.card_number ){
      def cn = creditDetailsDto.card_number
      def maskNumber = ( cn.size() == 16 ) ? "XXXX${cn.reverse().take(4).reverse()}" : cn
      instance.cardNumber = maskNumber
    }else{
      instance.cardNumber = creditDetailsDto.card_number
    }
    instance.lastUpdated = new Date()
    creditDetailsRepository.save( instance )
    instance
  
  }

  private Boolean nullAll( CreditDetailsDto creditDetailsDto ){
    def flag = true
    if( creditDetailsDto.closing_date
    || creditDetailsDto.non_interest_payment
    || creditDetailsDto.statement_balance
    || creditDetailsDto.minimum_payment
    || creditDetailsDto.credit_limit
    || creditDetailsDto.due_date
    || creditDetailsDto.closing_date
    || creditDetailsDto.annual_porcentage_rate
    || creditDetailsDto.card_number
    ){ flag = false }  
    flag  
  }


}
