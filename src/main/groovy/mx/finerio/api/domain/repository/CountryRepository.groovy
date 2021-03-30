package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface CountryRepository extends JpaRepository<Country, String>, JpaSpecificationExecutor {

	Country findOneByCode( String code )
  
}
