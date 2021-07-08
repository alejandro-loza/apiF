package mx.finerio.api.aop

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component
import mx.finerio.api.dtos.CreateCredentialSatwsDto

@Component
@Aspect
class SatwsServiceCreateCredential{

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.SatwsServiceCreateCredential' )

  @Pointcut(
    value='execution(java.lang.String mx.finerio.api.services.SatwsService.createCredential(..)) && bean(satwsService) && args(dto)',
    argNames='dto'
  )
  public void createCredential( CreateCredentialSatwsDto dto ) {}

  @Before('createCredential(dto)')
  void before(CreateCredentialSatwsDto dto) {
    log.info( "<< dto: {}", dto )
  }

  @AfterReturning(
    pointcut='createCredential(mx.finerio.api.dtos.CreateCredentialSatwsDto)',
    returning='response'
  )
  void afterReturning( String response ) {
    log.info( '>> response: {}', response )
  }

  @AfterThrowing(
    pointcut='createCredential(mx.finerio.api.dtos.CreateCredentialSatwsDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
