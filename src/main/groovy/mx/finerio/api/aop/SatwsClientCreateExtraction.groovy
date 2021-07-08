package mx.finerio.api.aop

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component
import mx.finerio.api.dtos.CreateExtractionDto


@Component
@Aspect
class SatwsClientServiceCreateExtraction{

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsClientServiceCreateExtraction' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.SatwsClientService.createExtraction(..)) && bean(satwsClientService) && args(dto)',
    argNames='dto'
  )
  public void createExtraction( CreateExtractionDto dto ) {}

  @Before('createExtraction(dto)')
  void before(CreateExtractionDto dto) {
    log.info( '<< dto: {}', dto )
  }

  @AfterReturning(
    pointcut='createExtraction(mx.finerio.api.dtos.CreateExtractionDto)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='createExtraction(mx.finerio.api.dtos.CreateExtractionDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}