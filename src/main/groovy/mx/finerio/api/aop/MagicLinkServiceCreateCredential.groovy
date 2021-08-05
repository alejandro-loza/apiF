package mx.finerio.api.aop

import mx.finerio.api.dtos.CreateCredentialV2Dto
import mx.finerio.api.dtos.CreateCredentialV2Dto
import mx.finerio.api.domain.Customer

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
class MagicLinkServiceCreateCredential {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.MagicLinkServiceCreateCredential' )

    @Pointcut(
            value='execution(java.util.Map mx.finerio.api.services.MagicLinkService.createCredential(..)) && bean(magicLinkService) && args(customerLinkId,createCredentialV2Dto)',
            argNames='customerLinkId,createCredentialV2Dto'
    )
    public void createCredential( String customerLinkId, CreateCredentialV2Dto createCredentialV2Dto ) {}

    @Before('createCredential(customerLinkId,createCredentialV2Dto)')
    void before( String customerLinkId, CreateCredentialV2Dto createCredentialV2Dto ) {
        log.info( "<< customerLinkId: {}, createCredentialV2Dto{}", customerLinkId, createCredentialV2Dto )
    }

    @AfterReturning(
            pointcut='createCredential(java.lang.String,mx.finerio.api.dtos.CreateCredentialV2Dto)',
            returning='response'
    )
    void afterReturning( Map response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='createCredential(java.lang.String,mx.finerio.api.dtos.CreateCredentialV2Dto)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }

}

