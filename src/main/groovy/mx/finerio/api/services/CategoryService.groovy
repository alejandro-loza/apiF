package mx.finerio.api.services

import mx.finerio.api.domain.Category
import mx.finerio.api.domain.repository.CategoryRepository
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CategoryService {

  @Autowired
  CategoryRepository categoryRepository

  Map findAll() throws Exception {
    [ data: categoryRepository.findAllByUserIsNullOrderByIdAsc(),
        nextCursor: null ]
  }

  Category findOne( String id ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'categoryService.findOne.id.null' )
    }
 
    def category = categoryRepository.findOne( id )

    if ( !category ) {
      throw new InstanceNotFoundException( 'category.not.found' )
    }
 
    category

  }

  Map getFields( Category category ) throws Exception {

    if ( !category ) {
      throw new BadImplementationException(
          'categoryService.getFields.category.null' )
    }

    [ id: category.id, name: category.name,
        parentId: category.parent?.id ]

  }

}
