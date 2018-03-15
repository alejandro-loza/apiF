package mx.finerio.api.services

import mx.finerio.api.domain.Category
import mx.finerio.api.domain.repository.CategoryRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import spock.lang.Specification

class CategoryServiceFindOneSpec extends Specification {

  def service = new CategoryService()

  def categoryRepository = Mock( CategoryRepository )

  def setup() {
    service.categoryRepository = categoryRepository
  }

  def "invoking method successfully"() {

    when:
      def result = service.findOne( id )
    then:
      1 * categoryRepository.findOne( _ as String ) >>
          new Category()
      result instanceof Category
    where:
      id = 'categoryId'

  }

  def "parameter 'id' is null"() {

    when:
      service.findOne( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'categoryService.findOne.id.null'
    where:
      id = null

  }

  def "parameter 'id' is blank"() {

    when:
      service.findOne( id )
    then:
      BadImplementationException e = thrown()
      e.message == 'categoryService.findOne.id.null'
    where:
      id = ''

  }

  def "instance not found"() {

    when:
      service.findOne( id )
    then:
      1 * categoryRepository.findOne( _ as String ) >> null
      InstanceNotFoundException e = thrown()
      e.message == 'category.not.found'
    where:
      id = 'notFound'

  }

}
