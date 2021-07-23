package mx.finerio.api.services

import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.validation.FinancialInstitutionCreateCommand


interface FinancialInstitutionService {

  Map findAll( Map params ) throws Exception

  FinancialInstitution findOne( Long id ) throws Exception

  FinancialInstitution getByIdAndCustomer(Long id, Customer customer) throws Exception

  FinancialInstitution findOneByCode( String  code ) throws Exception

  FinancialInstitution findOneAndValidate( Long id ) throws Exception

  Map getFields( FinancialInstitution financialInstitution ) throws Exception

  FinancialInstitution create(FinancialInstitutionCreateCommand cmd)

}
