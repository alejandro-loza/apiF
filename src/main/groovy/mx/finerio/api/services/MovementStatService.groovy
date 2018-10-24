package mx.finerio.api.services

import java.sql.Date
import java.util.Map

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import mx.finerio.api.domain.MovementStat
import mx.finerio.api.domain.MovementStatSpecs
import mx.finerio.api.domain.repository.MovementStatRepository
import mx.finerio.api.dtos.ListDto
import mx.finerio.api.dtos.MovementStatListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException
import mx.finerio.api.domain.Movement.Type

@Service
class MovementStatService {

	@Autowired
	MovementStatRepository movementStatRepository
	
	@Autowired
	UserService userService
	
	@Autowired
	CategoryService categoryService
	
	Map findAll( Map params ) throws Exception {
		
			if ( params == null ) {
			  throw new BadImplementationException(
				  'movementStatService.findAll.params.null' )
			}
		 
			def dto = getFindAllDto( params )
			def spec = MovementStatSpecs.findAll( dto )
			def instances = movementStatRepository.findAll( spec )
		    
			if(dto.groupedByCategory) {
			  instances=doGroupByCategory(instances)
			}
			
			 [ data: instances ]
		  }
	
		  
  private MovementStatListDto getFindAllDto( Map params ) throws Exception {
	  
		  if ( !params.userId ) {
			throw new BadRequestException( 'movementStat.findAll.userId.null' )
		  }
	 
		  def dto = new MovementStatListDto()
		  
		  dto.user= userService.findById( params.userId )
		  
		  if ( params.category ) {
			  dto.category= categoryService.findOne( params.category )
			}
			
		  if ( params.groupedByCategory ) {
				validateGroupedByCategory(params,dto)
			  }
			
			if ( params.type) {
				if( "DEPOSIT".equals(params.type) || "CHARGE".equals(params.type)) {
					if("DEPOSIT".equals(params.type)) {
						dto.type=Type.DEPOSIT
					}else {
						dto.type=Type.CHARGE
					}
				}else {
					throw new BadImplementationException( 'movementStat.findAll.type.wrong' )
				}
			}
		  
			validateAmounts(params,dto)
 
			validateDates(params,dto)
	  
		  dto
	  
		}
		
		private void validateDates(Map params, MovementStatListDto dto) {
		
			if ( params.fromDate) {
				try {
					dto.fromDate=Date.valueOf( params.fromDate )
				  } catch ( IllegalArgumentException e ) {
					throw new BadRequestException( 'movementStat.findAll.fromDate.wrong' )
				  }
			}
			if ( params.toDate) {
				try {
					dto.toDate=Date.valueOf( params.toDate )
				  } catch ( NumberFormatException e ) {
					throw new BadRequestException( 'movementStat.findAll.toDate.wrong' )
				  }
			}
			
			if(dto.fromDate && dto.toDate) {
				if(dto.fromDate.after(dto.toDate)) {
					throw new BadImplementationException("movementStat.findAll.fromDate.afterToDate");
				}
			}
		}
		
		
	private void validateAmounts(Map params, MovementStatListDto dto) {
		
		if ( params.greaterAmount) {
			try {
				dto.greaterAmount=params.greaterAmount as BigDecimal
			  } catch ( NumberFormatException e ) {
				throw new BadRequestException( 'movementStat.findAll.greaterAmount.wrong' )
			  }
		}
		
		if ( params.lessAmount) {
			try {
				dto.lessAmount=params.lessAmount as BigDecimal
			  } catch ( NumberFormatException e ) {
				throw new BadRequestException( 'movementStat.findAll.lessAmount.wrong' )
			  }
		}
		if(dto.greaterAmount && dto.lessAmount) {
			if(dto.greaterAmount > dto.lessAmount) {
				throw new BadRequestException( 'movementStat.findAll.amountRange.wrong' )
			}
		}
		
	}	
	
	void validateGroupedByCategory(Map params, MovementStatListDto dto ) throws Exception {
		
			try {
			  dto.groupedByCategory = params.groupedByCategory as Boolean
			} catch ( NumberFormatException e ) {
			  throw new BadRequestException( 'movementStat.findAll.groupedByCategory.wrong' )
			}
		
		  }
		  
	 List doGroupByCategory(List instances ) throws Exception {
		 	  
			def categoyInstances=instances.collect{
				if(it.category.parent){
				     it.category=it.category.parent
				}
				it
			}
			
			def resInstances=[]
			
			categoyInstances.each{
				
			   def item=it
				
				def foundItem=resInstances.find{		
					it.category==item.category &&
					it.type==item.type &&
					it.initDate==item.initDate &&
					it.finalDate==item.finalDate
				}
				
				if (foundItem) {
					foundItem.amount+=it.amount
				}else {
					resInstances<<it
				}
				
			}
			resInstances	
	}
			  
	Map getFields( MovementStat movementStat ) throws Exception {
				
			if ( !movementStat ) {
			  throw new BadImplementationException(
				  'movementStat.getFields.movement.null' )
			}		
			
			def dateFormat='yyyy/MM/dd HH:mm:ss'
			
			[ id: movementStat.id, user:movementStat.user.id, 
			   category: movementStat.category?.id, type: movementStat.type,
			   amount:movementStat.amount, initDate:movementStat.initDate.format(dateFormat),
			   final_date:movementStat.finalDate.format(dateFormat)]			
		
		  }
			
}
