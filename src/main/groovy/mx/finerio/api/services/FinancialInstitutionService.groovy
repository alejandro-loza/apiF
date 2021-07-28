package mx.finerio.api.services

import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.FinancialInstitution.Status
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.domain.repository.CountryRepository
import mx.finerio.api.dtos.FinancialInstitutionNParamsListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import mx.finerio.api.dtos.FinancialInstitutionListDto
import mx.finerio.api.domain.FinancialIntitutionSpecs

@Service
class FinancialInstitutionService {

  @Autowired
  FinancialInstitutionRepository financialInstitutionRepository

  @Autowired
  ListService listService

  @Autowired
  CountryRepository countryRepository

  public static final def defaultInstitutionType = FinancialInstitution.InstitutionType.PERSONAL
  public static final def defaultCountry = 'MX'


  Map findAll( Map params ) throws Exception {
    
   if ( params == null ) {
      throw new BadImplementationException(
          'financialInstitutionService.findAll.params.null' )
    }
 
    def dto = getFindAllDto( params )
    def spec = FinancialIntitutionSpecs.findAll( dto )
    return listService.findAll( dto, financialInstitutionRepository, spec )
    
  }

  Map findAllByCountriesAndTypes( Map params ) throws Exception {

    if ( params == null ) {
      throw new BadImplementationException(
              'financialInstitutionService.findAllByCountriesAndTypes.params.null' )
    }

    def dto = getFindAllNCountriesAndTypesDto( params )
    def spec = FinancialIntitutionSpecs.findAllByCountriesAndTypes( dto )
    def dataList = listService.findAll( dto, financialInstitutionRepository, spec )

    dataList =  dataList.data.groupBy{ it.institutionType }.collect { institutionType, listValues ->
      [ type: institutionType.name().toLowerCase(),
        banks: listValues.collect {  [ id:it.id,
                                       name:it.name,
                                       code:it.code,
                                       status: it.status]},
      ] }

    [ data: dataList ]
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


  FinancialInstitution findOneByCode( String  code ) throws Exception {

    if ( code == null ) {
      throw new BadImplementationException(
          'financialInstitutionService.findOneByCode.code.null' )
    }
 
    def instance = financialInstitutionRepository.findOneByCode( code )

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

    if ( financialInstitution.status !=
            FinancialInstitution.Status.ACTIVE
            && financialInstitution.status !=
            FinancialInstitution.Status.PARTIALLY_ACTIVE ) {
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
        code: financialInstitution.code,
        status: financialInstitution.status == Status.PARTIALLY_ACTIVE ?
        Status.ACTIVE : financialInstitution.status ]

  }

  private FinancialInstitutionNParamsListDto getFindAllNCountriesAndTypesDto(Map params ) throws Exception {

    def dto = new FinancialInstitutionNParamsListDto()
    dto.types=[]
    dto.countries = []

    if( params.types ) {
      params.types.each {
        try{
          def type =
                  FinancialInstitution.InstitutionType.valueOf(
                          it.trim().toUpperCase() )
          dto.types << type
        }catch( IllegalArgumentException ex ){
          throw new BadRequestException(
                  'financialInstitution.type.not.found' )
        }
      }
    }

    if( params.countries ) {
      params.countries.each {
       def country = countryRepository.findOneByCode( it )
        if (country) {
          dto.countries << country
        } else {
          throw new BadRequestException( 'country.not.found' )
        }
      }
    }

    dto

  }

  private FinancialInstitutionListDto getFindAllDto( Map params ) throws Exception {

    def dto = new FinancialInstitutionListDto()
    listService.validateFindAllDto( dto, params )

    if( params.type ) {
      try{
        dto.type =
                FinancialInstitution.InstitutionType.valueOf(
                        params.type.trim().toUpperCase() )
      }catch( IllegalArgumentException ex ){
        throw new BadRequestException(
                'financialInstitution.type.not.found' )
      }

    }else{
      dto.type = defaultInstitutionType
    }


    def country

    if( params.country ) {

      country = countryRepository.findOneByCode( params.country )

      if( country ) {
        dto.country = country
      } else {
        throw new BadRequestException( 'country.not.found' )
      }

    }else{
      dto.country = countryRepository.findOneByCode( defaultCountry )
    }

    dto

  }

}
