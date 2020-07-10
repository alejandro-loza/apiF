package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.Client
import mx.finerio.api.domain.Callback

interface CallbackRepository extends JpaRepository<Callback, Long>,
    JpaSpecificationExecutor {
  
  Callback findFirstByClientAndNatureAndDateDeletedIsNull(
      Client client, Callback.Nature nature )

  List<Callback> findAllByClientAndDateDeletedIsNull( Client client )

  List<Callback> findAllByNatureAndDateDeletedIsNull(
      Callback.Nature nature )

}
