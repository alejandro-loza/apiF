package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*
import org.springframework.data.jpa.repository.Query

interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor {
  
  Client findOneByUsername( String username )

  List<Client> findAllByEnabledTrueAndDateDeletedIsNullAndEmailIsNotNull();

}
