package mx.finerio.api.services

import mx.finerio.api.domain.Country
import mx.finerio.api.domain.FinancialInstitution
import mx.finerio.api.domain.repository.CountryRepository
import mx.finerio.api.exceptions.InstanceNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import mx.finerio.api.exceptions.BadImplementationException

@Service
class CountryService {

  @Autowired
  CountryRepository CountryRepository

  Country findOne( String id ) throws Exception {

    if ( id == null ) {
      throw new BadImplementationException(
              'countryService.findOne.id.null' )
    }
    def instance = CountryRepository.findOne( id )

    if ( !instance ) {
      throw new InstanceNotFoundException( 'country.not.found' )
    }

    instance
  }


  Map findAll() throws Exception {
    [ data: CountryRepository.findAll().findAll { it.dateDeleted == null },
         nextCursor: null ]
  }


  Map getFields( Country country ) throws Exception {

    if ( !country ) {
      throw new BadImplementationException(
          'countryService.getFields.country.null' )
    }

   def data = [ code: country.code,
      name: country.name,
      imageUrl: country.imageUrl ]
  }

}
