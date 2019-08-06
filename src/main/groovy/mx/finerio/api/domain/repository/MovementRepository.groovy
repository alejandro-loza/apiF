package mx.finerio.api.domain.repository

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface MovementRepository extends JpaRepository<Movement, String>, JpaSpecificationExecutor {

  Movement findFirstByDateAndDescriptionAndAmountAndTypeAndAccountAndScraperDuplicatedIdIsNullOrderByDateCreatedDesc( Date date, String Description, BigDecimal amount, Movement.Type type, Account account )
  Movement findByIdAndDateDeletedIsNull( String id )

  List<Movement> findByAccountAndDateDeletedIsNull( Account account )
  List<Movement> findTop50ByAccountAndAmountAndTypeAndDateDeletedIsNull( Account account, BigDecimal amount, Movement.Type type )

  Page<Movement> findByAccountAndDateDeletedIsNull( Account account, Pageable pageable )

  List<Movement> findAllByAccountAndCustomDateGreaterThanEqualAndCustomDateLessThanAndDateDeletedIsNull( Account account, Date from, Date to )

}
