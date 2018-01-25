package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface CustomerRepository extends JpaRepository<Customer, Long>,
    JpaSpecificationExecutor {
  
  List findAllByClient( Client client )

}
