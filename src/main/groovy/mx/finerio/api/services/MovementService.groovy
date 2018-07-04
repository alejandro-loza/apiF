package mx.finerio.api.services

import mx.finerio.api.domain.*
import mx.finerio.api.domain.repository.*
import mx.finerio.api.dtos.Transaction
import mx.finerio.api.dtos.TransactionData
import mx.finerio.api.dtos.MovementListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.exceptions.InstanceNotFoundException

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MovementService {

  @Autowired
  MovementRepository movementRepository

  @Autowired
  AccountService accountService

  @Autowired
  CredentialPersistenceService credentialPersistenceService

  @Autowired
  ConceptService conceptService

  @Autowired
  ListService listService

  @Autowired
  SecurityService securityService

  List createAll( TransactionData transactionData ) throws Exception {

    if ( !transactionData ) {
      throw new BadImplementationException(
          'movementService.createAll.transactionData.null' )
    }

    def account = accountService.findById( transactionData.account_id )

    transactionData.transactions.findResults { transaction ->
      create( account, transaction )
    }

  }

  void createConcept( Movement movement ) throws Exception {

    def conceptData = [
      description: movement.description,
      amount: movement.amount,
      type: Concept.Type.DEFAULT ]
    conceptService.create( movement.id, conceptData )

  }

  Map findAll( Map params ) throws Exception {

    if ( params == null ) {
      throw new BadImplementationException(
          'movementService.findAll.params.null' )
    }
 
    def dto = getFindAllDto( params )
    def spec = MovementSpecs.findAll( dto )
    listService.findAll( dto, movementRepository, spec )

  }

  Movement findOne( String id ) throws Exception {

    if ( !id ) {
      throw new BadImplementationException(
          'movementService.findOne.id.null' )
    }
 
    def instance = movementRepository.findOne( id )

    if ( !instance || instance.dateDeleted ) {
      throw new InstanceNotFoundException( 'movement.not.found' )
    }
 
    try {
      accountService.findOne( instance?.account?.id )
    } catch ( InstanceNotFoundException e ) {
      throw new InstanceNotFoundException( 'movement.not.found' )
    }
    
    instance

  }

  Map getFields( Movement movement ) throws Exception {

    if ( !movement ) {
      throw new BadImplementationException(
          'movementService.getFields.movement.null' )
    }
 
    [ id: movement.id, description: movement.description,
        amount: movement.amount, type: movement.type,
        dateCreated: movement.customDate ]

  }

  private Movement create( Account account, Transaction transaction )
      throws Exception {

    def date = new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",
        transaction.made_on ) ?: new Date()
    def rawAmount = transaction.amount
    def amount = new BigDecimal( rawAmount ).abs().setScale( 2, BigDecimal.ROUND_HALF_UP )
    def type = rawAmount < 0 ? Movement.Type.CHARGE : Movement.Type.DEPOSIT
    def description = transaction.description.take( 255 )
    def instance = movementRepository.findByDateAndDescriptionAndAmountAndTypeAndAccount(
        date, description, amount, type, account )
    def movement = instance ?: new Movement()
    movement.account = account
    movement.date = date
    movement.customDate = movement.customDate ?: date
    movement.description = description
    movement.customDescription = movement.customDescription ?: description
    movement.amount = amount
    movement.balance = amount
    movement.type = type
    movement.dateCreated = movement.dateCreated ?: new Date()
    movement.lastUpdated = new Date()
    movementRepository.save( movement )

    if ( !instance ) {
      return movement
    }

    null

  }

  private MovementListDto getFindAllDto( Map params ) throws Exception {

    if ( !params.accountId ) {
      throw new BadRequestException( 'movement.findAll.accountId.null' )
    }

    def dto = new MovementListDto()
    dto.account = accountService.findOne( params.accountId )
    listService.validateFindAllDto( dto, params )

    if ( params.cursor ) {
      def cursorInstance = findOne( params.cursor )
      dto.dateCreated = cursorInstance.customDate
    }

    dto

  }

}
