package mx.finerio.api.domain.repository

import mx.finerio.api.domain.Advice
import mx.finerio.api.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface AdviceRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor {
  List<Advice> findAllByCategoryAndDateDeletedIsNull(Category category)
}
