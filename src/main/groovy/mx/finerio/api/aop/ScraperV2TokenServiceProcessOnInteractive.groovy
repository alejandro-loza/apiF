package mx.finerio.api.aop

import mx.finerio.api.dtos.ScraperV2TokenDto
import mx.finerio.api.domain.Callback

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component
import mx.finerio.api.domain.Client

@Component
@Aspect
class ScraperV2TokenServiceProcessOnInteractive {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.aop.ScraperV2TokenServiceProcessOnInteractive' )

  @Pointcut(
    value='execution(void mx.finerio.api.services.ScraperV2TokenService.processOnInteractive(..)) && args(scraperV2TokenDto,client)',
    argNames='scraperV2TokenDto,client'
  )
  public void processOnInteractive( ScraperV2TokenDto scraperV2TokenDto, Client client ) {}

  @Before('processOnInteractive(scraperV2TokenDto,client)')
  void before( ScraperV2TokenDto scraperV2TokenDto, Client client ) {
    log.info( "<<scraperV2TokenDto: {} client: {}", scraperV2TokenDto, client )
  }

  @AfterReturning(
    pointcut='processOnInteractive(mx.finerio.api.dtos.ScraperV2TokenDto,mx.finerio.api.domain.Client)'    
  )
  void afterReturning() {
    log.info( '>>ok' )
  }

  @AfterThrowing(
    pointcut='processOnInteractive(mx.finerio.api.dtos.ScraperV2TokenDto,mx.finerio.api.domain.Client)',
    throwing='e'
  )
  void afterThrowing( Exception e ) {
    log.info( "XX ${e.class.simpleName} - ${e.message}" )
  }
}
