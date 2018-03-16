
package mx.finerio.api.controllers

import mx.finerio.api.services.CategoryService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CategoryController {

  @Autowired
  CategoryService categoryService

  @GetMapping('/categories')
  ResponseEntity findAll() {
  
    def response = categoryService.findAll()
    response.data = response.data.collect {
        categoryService.getFields( it ) }
    new ResponseEntity( response, HttpStatus.OK )

  }

}
