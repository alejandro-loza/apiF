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

import java.util.Map

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MovementService {
	
  @Autowired
  CategoryRepository categoryRepository
  
  @Autowired
  CleanerService cleanerService

  @Autowired
  MovementRepository movementRepository

  @Autowired
  AccountService accountService

  @Autowired
  CredentialPersistenceService credentialPersistenceService

  @Autowired
  ConceptService conceptService
  
  @Autowired
  CategorizerService categorizerService

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
      create( account, transaction, account.deleted )
    }

  }
  
  Movement generateAndSetCategory(String movementId) {
	  def movement = movementRepository.findByIdAndDateDeletedIsNull(movementId)
	  if (!movement) {
		  return null
	  }
	  generateAndSetCategory(movement)
  }
  
   void generateAndSetCategory(Movement movement) {
	  
	  def category = null
          def deposit = movement.type == Movement.Type.DEPOSIT
          def cleanedText = cleanerService.clean( movement.description, deposit )
          movement.customDescription = cleanedText
          def result = categorizerService.search( cleanedText, deposit )

          if ( result?.categoryId ) {
            category = categoryRepository.findOne( result.categoryId )
          }

	  movement.category=category
	  movement.hasConcepts=false
	  movementRepository.save( movement )
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

  void updateDuplicated( Movement movement ) throws Exception {

    if ( !movement ) {
      throw new BadImplementationException(
          'movementService.updateDuplicated.movement.null' )
    }
 
    movement.duplicated = true
    movement.lastUpdated = new Date()
    movementRepository.save( movement )

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

  List getMovementsToTransference( Movement movement, Movement.Type type ){

    if ( !movement ) {
      throw new BadImplementationException(
          'movementService.getMovementsToDuplicated.movement.null' )
    }
    if ( !type ) {
      throw new BadImplementationException(
          'movementService.getMovementsToDuplicated.type.null' )
    }
    List accounts = accountService.findAllByUser( movement.account )
    def movements = sumMovementList( accounts, movement, type ) ?: []
    movements

  }

  private List sumMovementList( List list, Movement mov, Movement.Type type ){

    List listfinal = []
    list.each{
      def movements = movementRepository.findTop50ByAccountAndAmountAndTypeAndDateDeletedIsNull(
        it, mov.amount , type )
      listfinal += movements
    }
    listfinal

  }

  List getMovementsToDuplicated( Movement movement ){

    if ( !movement ) {
      throw new BadImplementationException(
          'movementService.getMovementsToDuplicated.id.null' )
    }
    def movements = movementRepository.findTop50ByAccountAndAmountAndTypeAndDateDeletedIsNull(
        movement.account, movement.amount, movement.type ) ?: []
    movements

  }

  @Transactional
  Movement create( Account account, Transaction transaction,
      boolean deleted ) throws Exception {

    def date = new Date().parse( "yyyy-MM-dd'T'HH:mm:ss",
        transaction.made_on ) ?: new Date()
    def rawAmount = transaction.amount
    def amount = new BigDecimal( rawAmount ).abs().setScale( 2, BigDecimal.ROUND_HALF_UP )
    def type = rawAmount < 0 ? Movement.Type.CHARGE : Movement.Type.DEPOSIT
    def description = transaction.description.take( 255 )
    if ( description?.size() == 0 ) { return null }
    def items = findAllMovementsByAccount( account, date )
    def instance = getDuplicatedInstance( items, description, amount, type,
        transaction.extra_data?.transaction_Id )
    def movement = instance ?: new Movement()
    movement.account = account
    movement.date = date
    movement.customDate = movement.customDate ?: date
    movement.description = description
    movement.customDescription = movement.customDescription ?: description
    movement.amount = amount
    movement.balance = amount
    movement.type = type
    def now = new Date()
    movement.dateCreated = movement.dateCreated ?: now
    movement.lastUpdated = now
    movement.scraperDuplicatedId = transaction.extra_data?.transaction_Id

    if ( deleted ) {
      movement.dateDeleted = now
    }

    if ( !instance ) {
      generateAndSetCategory( movement )
      return movement
    }

    movementRepository.save( movement )

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

  private List findAllMovementsByAccount( Account account, Date date )
      throws Exception {

    def fromString = date.format( "yyyy-MM-dd" )
    def from = Date.parse( "yyyy-MM-dd", fromString )
    def cal = Calendar.instance
    cal.time = from
    cal.add( Calendar.DAY_OF_MONTH, 1 )
    def to = cal.time
    return movementRepository.findAllByAccountAndCustomDateGreaterThanEqualAndCustomDateLessThanAndDateDeletedIsNull(
        account, from, to )

  }

  private Movement getDuplicatedInstance( List items, String description,
      BigDecimal amount, Movement.Type type, String transactionId )
      throws Exception {

    for ( item in items ) {

      if ( ( item.scraperDuplicatedId == null &&
          item.description.equals( description ) &&
          item.amount.equals( amount ) &&
          item.type.equals( type ) ) ||
          ( item.scraperDuplicatedId != null &&
              item.scraperDuplicatedId.equals( transactionId ) ) ) {

        return item

      }

    }

    return null

  }

}
