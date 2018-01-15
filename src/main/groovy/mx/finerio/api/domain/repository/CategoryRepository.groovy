package mx.finerio.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import mx.finerio.api.domain.*

interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor {

  Category findById( String id )   

}
