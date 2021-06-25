package mx.finerio.api.services.imp

import mx.finerio.api.domain.Customer
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.FinancialInstitution.Status
import mx.finerio.api.domain.FinancialIntitutionSpecs
import mx.finerio.api.domain.Country
import mx.finerio.api.domain.repository.CountryRepository
import mx.finerio.api.domain.repository.FinancialInstitutionRepository
import mx.finerio.api.dtos.FinancialInstitutionListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException
import mx.finerio.api.services.CustomerService
import mx.finerio.api.services.ListService
import mx.finerio.api.services.FinancialInstitutionService
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.validation.FinancialInstitutionCreateCommand
import mx.finerio.api.validation.ValidationCommand
import org.springframework.stereotype.Service


@Service
class FinancialInstitutionServiceImp implements FinancialInstitutionService {

  @Autowired
  FinancialInstitutionRepository financialInstitutionRepository

  @Autowired
  ListService listService

  @Autowired
  CountryRepository countryRepository

  @Autowired
  CustomerService customerService

  static final def defaultInstitutionType = FinancialInstitution.InstitutionType.PERSONAL
  static final def defaultCountry = 'MX'


  @Override
  FinancialInstitution create(FinancialInstitutionCreateCommand cmd) throws Exception {
    verifyBody(cmd)
    Customer customer = customerService.findOne(cmd.customerId)

    verifyUniqueCode(cmd, customer)
    FinancialInstitution financialInstitution = new FinancialInstitution()

    financialInstitution.with {
      code = cmd.code
      internalCode = cmd.internalCode
      description = cmd.description
      name = cmd.name
      provider = getProviderEnum(cmd)
      status = getStatusEnum(cmd)
      institutionType = getInstitutionTypeEnum(cmd)
      country = searchCountry(cmd.country)
      financialInstitution.customer = customer
      dateCreated = new Date()
      version = 0
    }

    financialInstitutionRepository.save(financialInstitution)
  }

  @Override
  Map findAll( Map params ) throws Exception {
    
   if ( params == null ) {
      throw new BadImplementationException(
          'financialInstitutionService.findAll.params.null' )
    }
 
    def dto = getFindAllDto( params )
    def spec = FinancialIntitutionSpecs.findAll( dto )
    return listService.findAll( dto, financialInstitutionRepository, spec )
    
  }

  @Override
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

  @Override
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


  @Override
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

  @Override
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

    Country country

    if( params.country ) {
      country = searchCountry( params.country )
    }else{
       dto.country = countryRepository.findOneByCode( defaultCountry )
    }
    
    dto

  }

  private Country searchCountry(String countryCode) {
    Optional.ofNullable(countryRepository.findOneByCode( countryCode ))
            .orElseThrow({ -> new BadRequestException('country.not.found') })
  }


  void verifyBody(ValidationCommand cmd) {
    if (!cmd) {
      throw new BadRequestException('request.body.invalid')
    }
  }

  private void verifyUniqueCode(ValidationCommand cmd, Customer customer) {
    if(findByCode(cmd, customer)){
      throw new BadRequestException('financialInstitution.code.nonUnique')
    }
  }

  private FinancialInstitution findByCode(ValidationCommand cmd, Customer customer) {
    financialInstitutionRepository.findByCodeAndCustomerAndDateDeletedIsNull(String.valueOf(cmd["code"]), customer)
  }

  private Status getStatusEnum(FinancialInstitutionCreateCommand cmd) {
    try {
     return Status.valueOf(cmd.status.toString())
    }
    catch (IllegalArgumentException e) {
      throw new BadRequestException('financialInstitution.status.invalid')
    }
  }

  private FinancialInstitution.InstitutionType getInstitutionTypeEnum(FinancialInstitutionCreateCommand cmd) {
    try {
      return FinancialInstitution.InstitutionType.valueOf(cmd.institutionType.toString())
    }
    catch (IllegalArgumentException e) {
      throw new BadRequestException('financialInstitution.institutionType.invalid')
    }
  }

  private FinancialInstitution.Provider getProviderEnum(FinancialInstitutionCreateCommand cmd) {
    try {
      return FinancialInstitution.Provider.valueOf(cmd.provider.toString())
    }
    catch (IllegalArgumentException e) {
      throw new BadRequestException('financialInstitution.provider.invalid')
    }
  }

}
