package mx.finerio.api.domain.repository

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface MovementRepository extends JpaRepository<Movement, Long>, JpaSpecificationExecutor {

  Movement findByDateAndDescriptionAndAmountAndTypeAndAccount( Date date, String Description, BigDecimal amount, Movement.Type type, Account account )   
  Movement findById( String id )   

  List<Movement> findByAccount( Account account )   

  Page<Movement> findByAccount( Account account, Pageable pageable )   

}
