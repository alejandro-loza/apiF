package mx.finerio.api.domain.repository

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.ClientMtls

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ClientMtlsRepository extends JpaRepository<ClientMtls, Long>, JpaSpecificationExecutor { 

  ClientMtls findByClient( Client client )

}
