package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.CreditDetailsRepository
import mx.finerio.api.dtos.CreditDetailsDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CreditDetailsServiceCreateSpec extends Specification {

  def service = new CreditDetailsService()

  def accountService = Mock( AccountService )
  def creditDetailsRepository = Mock( CreditDetailsRepository )

  def setup() {

    service.accountService = accountService
    service.creditDetailsRepository = creditDetailsRepository

  }

  def "invoking method successfully"() {

    when:
      def result = service.create( creditDetailsDto, account )
    then:
      1 * creditDetailsRepository.findByAccount(
          _ as Account ) >> null
      1 * creditDetailsRepository.save(
          _ as CreditDetails ) >> getCreditDetails( 1L )
      result instanceof CreditDetails
    where:
      creditDetailsDto = getCreditDetailsDto()
      account = getAccount()

  }

  def "instance already exists"() {

    when:
      service.create( creditDetailsDto, account )
    then:
      1 * creditDetailsRepository.findByAccount(
          _ as Account ) >> getCreditDetails( 1L )
    where:
      creditDetailsDto = getCreditDetailsDto()
      account = getAccount()

  }

  def "parameter 'account' is null"() {

    when:
      service.create( creditDetailsDto, account )
    then:
      BadImplementationException e = thrown()
      e.message == 'creditDetailsService.create.account.null'
    where:
      creditDetailsDto = getCreditDetailsDto()
      account = null

  }

  def "parameter 'creditDetailsDto' is null"() {

    when:
      service.create( creditDetailsDto, account )
    then:
      BadImplementationException e = thrown()
      e.message == 'creditDetailsService.create.creditDetailsDto.null'
    where:
      creditDetailsDto = null
      account = null

  }


  private CreditDetails getCreditDetails( Long id ) throws Exception {

    new CreditDetails(
      id: id,  
      account: getAccount(),  
      annualPercentageRate: 0,
      cardNumber: "123123123456456426",
      closingDate: getDate("2018-11-10T00:00:00"),
      limitCredit: 10,
      dueDate: getDate("2018-11-10T00:00:00"),
      lastClosingDate: getDate("2018-11-10T00:00:00"),
      minimumPayment: 20,
      nonInterestPayment: 30,
      statementBalance: 0
    )

  }

  private CreditDetailsDto getCreditDetailsDto() throws Exception {

    new CreditDetailsDto(
      annual_porcentage_rate: 0,
      card_number: "123123123456456426",
      closing_date: "2018-11-10T00:00:00",
      credit_limit: 10,
      due_date: "2018-11-10T00:00:00",
      last_closing_date: "2018-11-10T00:00:00",
      minimum_payment: 20,
      non_interest_payment: 30,
      statement_balance: 0
    )

  }

  private Account getAccount() throws Exception {

    new Account(
      id: 1L,
      name: 'name',
      number: 'number',
      balance: 1.00,
      nature: 'nature',
      dateCreated: new Date()
    )

  }

  private Date getDate( String s ){
    new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",s ) ?: new Date()
  }

}
