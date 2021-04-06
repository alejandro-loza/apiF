package mx.finerio.api.services

import mx.finerio.api.domain.Country
import mx.finerio.api.domain.repository.CountryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import mx.finerio.api.exceptions.BadImplementationException

@Service
class CountryService {

  @Autowired
  CountryRepository CountryRepository

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
