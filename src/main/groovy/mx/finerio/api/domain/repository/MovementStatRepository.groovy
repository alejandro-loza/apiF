package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.Movement
import mx.finerio.api.domain.MovementStat

interface MovementStatRepository extends JpaRepository<MovementStat, Long>, JpaSpecificationExecutor {

}
