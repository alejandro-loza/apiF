package mx.finerio.api.services

import mx.finerio.api.dtos.ListDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.exceptions.BadRequestException

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ListService {

  private static final Integer MAX_RESULTS = 100

  Map findAll( ListDto listDto, JpaRepository repository, Object spec )
      throws Exception {

    if ( !listDto ) {
      throw new BadImplementationException(
          'listService.findAll.listDto.null' )
    }
 
    def maxResults = getMaxResults( listDto.maxResults )
    def pageRequest = new PageRequest( 0, maxResults + 1 )
    def instances = repository.findAll( spec, pageRequest ).content
    def response = [ data: instances, nextCursor: null ]

    if ( instances.size() == maxResults + 1 ) {
      response.data = instances.dropRight( 1 )
      response.nextCursor = instances.last().id
    }

    response

  }

  void validateFindAllDto( ListDto dto, Map params ) throws Exception {

    try {
      dto.maxResults = params.maxResults as Integer
    } catch ( NumberFormatException e ) {
      throw new BadRequestException( 'maxResults.invalid' )
    }

  }

  private Integer getMaxResults( Integer maxResultsToEvaluate )
      throws Exception {

    if ( maxResultsToEvaluate != null &&
        maxResultsToEvaluate > 0 && maxResultsToEvaluate <= MAX_RESULTS ) {
      return maxResultsToEvaluate 
    }

    MAX_RESULTS

  }

}
