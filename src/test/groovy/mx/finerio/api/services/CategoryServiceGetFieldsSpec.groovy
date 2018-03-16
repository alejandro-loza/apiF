package mx.finerio.api.services

import mx.finerio.api.domain.Category
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class CategoryServiceGetFieldsSpec extends Specification {

  def service = new CategoryService()

  def "invoking method successfully"() {

    when:
      def result = service.getFields( category )
    then:
      result instanceof Map
      result.id != null
      result.name != null
      result.parentId != null
    where:
      category = getCategory()

  }

  def "parameter 'category' is null"() {

    when:
      service.getFields( category )
    then:
      BadImplementationException e = thrown()
      e.message == 'categoryService.getFields.category.null'
    where:
      category = null

  }

  private Category getCategory() throws Exception {

    new Category(
      id: 'id',
      name: 'name',
      parent: new Category( id: 'id' )
    )

  }

}
