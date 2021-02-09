package mx.finerio.api.aop

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component
import mx.finerio.api.dtos.CreateCredentialDto

@Component
@Aspect
class ProdScraperV2ServiceCreateCredential{

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ProdScraperV2ServiceCreateCredential' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.ProdScraperV2Service.createCredential(..)) && bean(prodScraperV2Service) && args(createCredentialDto)',
    argNames='createCredentialDto'
  )
  public void createCredential( CreateCredentialDto createCredentialDto ) {}

  @Before('createCredential(createCredentialDto)')
  void before(CreateCredentialDto createCredentialDto) {
    log.info( "<< createCredentialDto: *****" )
  }

  @AfterReturning(
    pointcut='createCredential(mx.finerio.api.dtos.CreateCredentialDto)'
    
  )
  void afterReturning() {
    log.info( '>> ok')
  }

  @AfterThrowing(
    pointcut='createCredential(mx.finerio.api.dtos.CreateCredentialDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
