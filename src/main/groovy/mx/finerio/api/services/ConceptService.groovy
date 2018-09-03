package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConceptService {

  @Autowired
  ConceptRepository conceptRepository  

  @Autowired
  MovementRepository movementRepository  

  @Autowired
  CategoryRepository categoryRepository  

  @Autowired
  CleanerService cleanerService  

  @Autowired
  CategorizerService categorizerService  

  @Autowired
  AccountRepository accountRepository

  @Autowired
  UserService userService

    @Transactional
    Concept create( String movementId, Map attributes ) {
        def movement = movementRepository.findByIdAndDateDeletedIsNull(movementId)
        if (!movement) {
            return null
        }
        this.createConcept(movement, attributes)
    }

    private Concept createConcept(Movement movement, Map attributes) {

        def category = categoryRepository.findById(attributes.category?.id)
        if (movement.type == Movement.Type.CHARGE && !category) {
            def user = getUser(movement)
            def cleanedText = cleanerService.clean( attributes.description )
            movement.customDescription = cleanedText
            def result = categorizerService.search( cleanedText )

            if ( result?.categoryId ) {
              category = categoryRepository.findOne( result.categoryId )
            }

        }

        def type = attributes.type ? attributes.type as Concept.Type : Concept.Type.USER
        def item = conceptRepository.findByMovement(movement)?:new Concept()
	item.description = attributes.description
        item.amount = attributes.amount
        item.category = category
        item.type = type
	item.movement = movement
	item.version = 0
        this.updateAmounts( item )
        movementRepository.save( movement )
        conceptRepository.save( item )
        item
    }

    def updateAmounts(Concept concept) {
      def userAmount = concept.type == Concept.Type.USER ? concept.amount : 0
      def movAmount = concept.movement.amount
      if ( userAmount > movAmount ) {
      }
      def defaultConcept = concept.find { it.type == Concept.Type.DEFAULT }
      defaultConcept.amount = (movAmount - userAmount) < 0 ? 0 : movAmount - userAmount
    }

    User getUser(Movement mov){
      def account = accountRepository.findById( mov.account.id )
      def user = userService.findById( account.user.id )
      user 
    }


}
