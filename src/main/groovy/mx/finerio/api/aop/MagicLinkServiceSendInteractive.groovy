package mx.finerio.api.aop

import mx.finerio.api.dtos.CredentialInteractiveMagicLinkDto
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
class MagicLinkServiceSendInteractive {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.MagicLinkServiceSendInteractive' )

    @Pointcut(
            value='execution(void mx.finerio.api.services.MagicLinkService.sendInteractive(..)) && bean(magicLinkServiceService) && args(customerLinkId,credentialId,credentialInteractiveMagicLinkDto)',
            argNames='customerLinkId,credentialId,credentialInteractiveMagicLinkDto'
    )
    public void sendInteractive(String customerLinkId, String credentialId, CredentialInteractiveMagicLinkDto credentialInteractiveMagicLinkDto ) {}

    @Before('sendInteractive(customerLinkId,credentialId,credentialInteractiveMagicLinkDto)')
    void before( String customerLinkId, String credentialId, CredentialInteractiveMagicLinkDto credentialInteractiveMagicLinkDto ) {
        log.info( "<< customerLinkId: {}, credentialId: {}, credentialInteractiveMagicLinkDto: {}", customerLinkId, credentialId, credentialInteractiveMagicLinkDto )
    }

    @AfterReturning(
            pointcut='sendInteractive(java.lang.String,java.lang.String,mx.finerio.api.dtos.CredentialInteractiveMagicLinkDto)'
    )
    void afterReturning() {
        log.info( '>> OK' )
    }

    @AfterThrowing(
            pointcut='sendInteractive(java.lang.String,java.lang.String,mx.finerio.api.dtos.CredentialInteractiveMagicLinkDto)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }
}

