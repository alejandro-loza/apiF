package mx.finerio.api.services

import mx.finerio.api.domain.Category
import mx.finerio.api.domain.repository.CategoryRepository

import spock.lang.Specification

class CategoryServiceFindAllSpec extends Specification {

  def service = new CategoryService()

  def categoryRepository = Mock( CategoryRepository )

  def setup() {
    service.categoryRepository = categoryRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.findAll()
    then:
      1 * categoryRepository.findAllByUserIsNullOrderByIdAsc() >>
        [
          new Category( name: 'Food' ),
          new Category( name: 'Financial' ),
          new Category( name: 'Entertainment' ) 
        ]

  }

}
