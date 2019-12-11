package mx.finerio.api.services

import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class FinancialInstitutionService {

  @Autowired
  FinancialInstitutionRepository financialInstitutionRepository

  Map findAll() throws Exception {
    [ data: financialInstitutionRepository.findAll().findAll {
        it.code != 'DINERIO' && it.status != 'DELETED' }, nextCursor: null ]
  }

  FinancialInstitution findOne( Long id ) throws Exception {

    if ( id == null ) {
      throw new BadImplementationException(
          'financialInstitutionService.findOne.id.null' )
    }
 
    def instance = financialInstitutionRepository.findOne( id )

    if ( !instance ) {
      throw new InstanceNotFoundException( 'financialInstitution.not.found' )
    }
 
    instance

  }

  FinancialInstitution findOneAndValidate( Long id ) throws Exception {

    if ( id == null ) {
      throw new BadImplementationException(
          'financialInstitutionService.findOneAndValidate.id.null' )
    }

    def financialInstitution = findOne( id )

    if ( financialInstitution.status != 'ACTIVE' ) {
      throw new BadRequestException( 'financialInstitution.disabled' )
    }

    financialInstitution

  }

  Map getFields( FinancialInstitution financialInstitution ) throws Exception {

    if ( !financialInstitution ) {
      throw new BadImplementationException(
          'financialInstitutionService.getFields.financialInstitution.null' )
    }

    [ id: financialInstitution.id, name: financialInstitution.name,
        status: financialInstitution.status ]

  }

}
