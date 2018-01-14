package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface ConfigRepository extends JpaRepository<Config, Long>, JpaSpecificationExecutor {

  Config findByItem( Config.Item item )   

}
