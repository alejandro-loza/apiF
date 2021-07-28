package mx.finerio.api.aop

import mx.finerio.api.domain.Client
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
class ClientConfigIsInstitutionGranted {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.ClientConfigIsInstitutionGranted' )

    @Pointcut(
            value='execution(java.lang.Boolean mx.finerio.api.services.ClientConfigService.isInstitutionGranted(..)) && bean(clientConfigService)&& args(client,institutionCode)',
            argNames='client,institutionCode'
    )
    public void isInstitutionGranted( Client client, String institutionCode ) {}

    @Before('isInstitutionGranted(client,institutionCode)')
    void before(  Client client, String institutionCode  ) {
        log.info( "<< client: {} institutionCode: {}", client, institutionCode )
    }

    @AfterReturning(
            pointcut='isInstitutionGranted(mx.finerio.api.domain.Client,String)',
            returning='response'
    )
    void afterReturning( Boolean response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='isInstitutionGranted(mx.finerio.api.domain.Client,String)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }

}
