package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface ConceptRepository extends JpaRepository<Concept, Long>, JpaSpecificationExecutor {

  Concept findByMovement( Movement movement )   

}
