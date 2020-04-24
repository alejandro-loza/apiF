package mx.finerio.api.ao

import mx.finerio.api.dtos.CreateAllAccountExtraDataDto

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component

@Component
@Aspect
class AccountExtraDataServiceCreateAll {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.AccountExtraDataServiceCreateAll' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.AccountExtraDataService.createAll(..)) && bean(accountExtraDataService) && args(dto)',
    argNames='dto'
  )
  public void createAll( CreateAllAccountExtraDataDto dto ) {}

  @Before('createAll(dto)')
  void before(CreateAllAccountExtraDataDto dto ) {
    log.info( "<< dto: {}", dto )
  }

  @AfterReturning(
    pointcut='createAll(mx.finerio.api.dtos.CreateAllAccountExtraDataDto)'
  )
  void afterReturning() {
    log.info( '>> OK' )
  }

  @AfterThrowing(
    pointcut='createAll(mx.finerio.api.dtos.CreateAllAccountExtraDataDto)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }

}
