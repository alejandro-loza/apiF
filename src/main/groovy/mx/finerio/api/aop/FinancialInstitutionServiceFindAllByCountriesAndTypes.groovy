package mx.finerio.api.aop

import mx.finerio.api.domain.FinancialInstitution

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
class FinancialInstitutionServiceFindAllByCountriesAndTypes {

    final static Logger log = LoggerFactory.getLogger(
            'mx.finerio.api.aop.FinancialInstitutionServiceFindAllByCountriesAndTypes' )

    @Pointcut(
            value='execution(java.util.Map mx.finerio.api.services.FinancialInstitutionService.findAllByCountriesAndTypes(..)) && bean(financialInstitutionService) && args(params)',
            argNames='params'
    )
    public void findAllByCountriesAndTypes( Map params ) {}

    @Before('findAllByCountriesAndTypes(params)')
    void before( Map params ) {
        log.info( "<< params: {}", params )
    }

    @AfterReturning(
            pointcut='findAllByCountriesAndTypes(java.util.Map)',
            returning='response'
    )
    void afterReturning( Map response ) {
        log.info( '>> response: {}', response )
    }

    @AfterThrowing(
            pointcut='findAllByCountriesAndTypes(java.util.Map)',
            throwing='e'
    )
    void afterThrowing( Exception e ) {
        log.info( "XX ${e.class.simpleName} - ${e.message}" )
    }

}

