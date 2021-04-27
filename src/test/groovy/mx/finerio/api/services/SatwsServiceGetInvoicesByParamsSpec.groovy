package mx.finerio.api.services


import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.dtos.SatwsEventDto
import mx.finerio.api.dtos.SatwsEventDataDto
import mx.finerio.api.dtos.SatwsObjectDto
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Customer
import spock.lang.Specification

class SatwsServiceGetInvoicesByParamsSpec extends Specification {

  def service = new SatwsService()

  def financialInstitutionService = Mock( FinancialInstitutionService )
  def credentialService = Mock( CredentialService )
  def credentialFailureService= Mock( CredentialFailureService ) 
  def customerService= Mock( CustomerService )
  def satwsClientService= Mock( SatwsClientService )
  

  def setup() {

    service.financialInstitutionService = financialInstitutionService
    service.credentialService = credentialService
    service.credentialFailureService = credentialFailureService
    service.customerService = customerService
    service.satwsClientService = satwsClientService
  
  }

  def "customerId is null"() {

    when:
      service.getInvoicesByParams( params )
    then:
      BadImplementationException e = thrown()
      e.message == 'satwsService.getInvoicesByParams.params.customerId.null'
    where:
     params = [:]

  }

  def "financialInstitution is null"() {

    when:
      service.getInvoicesByParams( params )
    then:
    1 * financialInstitutionService.findOneByCode( _ as String ) >> null
      BadImplementationException e = thrown()
      e.message == 'satwsService.getRfcByCustomerId.financialInstitution.notFound'
    where:
     params = [ customerId: 5615 ] 

  }

  def "customer is null"() {

    when:
      service.getInvoicesByParams( params )
    then:
    1 * financialInstitutionService.findOneByCode( _ as String ) >> new FinancialInstitution()
    1 * customerService.findOne( _ as Long ) >> null
      BadImplementationException e = thrown()
      e.message == 'satwsService.getRfcByCustomerId.customer.notFound'
    where:
     params = [ customerId: 5615 ] 

  }

  def "credential is null"() {

    when:
      service.getInvoicesByParams( params )
    then:
    1 * financialInstitutionService.findOneByCode( _ as String ) >> new FinancialInstitution()
    1 * customerService.findOne( _ as Long ) >> new Customer()
    1 * credentialService.findByCustomerAndFinancialIntitution( _ as Customer, _ as FinancialInstitution ) >> null
      BadImplementationException e = thrown()
      e.message == 'satwsService.getRfcByCustomerId.credential.notFound'
    where:
     params = [ customerId: 5615 ] 

  }

  def "execution sucess"() {

   when:
      def result = service.getInvoicesByParams( params )
    then:
    1 * financialInstitutionService.findOneByCode( _ as String ) >> new FinancialInstitution()
    1 * customerService.findOne( _ as Long ) >> new Customer()
    1 * credentialService.findByCustomerAndFinancialIntitution( _ as Customer, _ as FinancialInstitution ) >> new Credential(username:'anyusername')
    1 * satwsClientService.getInvoicesByParams( _ as String, ['customerId':null] ) >> [:]
    result instanceof Map        
    where:
     params = [ customerId: 5615 ] 

  }
  
}
