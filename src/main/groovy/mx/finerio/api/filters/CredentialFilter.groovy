package mx.finerio.api.filters

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import javax.crypto.BadPaddingException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

import mx.finerio.api.services.MessageService
import mx.finerio.api.services.RsaCryptService
import mx.finerio.api.exceptions.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class CredentialFilter implements Filter {

  @Autowired
  MessageService messageService

  @Autowired
  RsaCryptService rsaCryptService

  @Override
  void init( FilterConfig filterConfig ) throws ServletException {}

  @Override
  void doFilter( ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain ) throws IOException, ServletException {

    try {

      def httpRequest = servletRequest as HttpServletRequest
      def newRequest = httpRequest
      httpRequest.
  
      if ( [ 'POST', 'PUT' ].contains( httpRequest.method ) ) {
  
        def body = httpRequest.inputStream.text
        def newBody = decryptBody( body )
        newRequest = new RequestWrapper( httpRequest, newBody.bytes )
  
      }
  
      filterChain.doFilter( newRequest, servletResponse )

    } catch ( BadPaddingException e ) {

      def errors = [ errors: [
          messageService.findOne( 'cryptedText.invalid' ) ] ]
      servletResponse.setStatus( HttpStatus.BAD_REQUEST.value() )
      servletResponse.getWriter().write( new JsonBuilder( errors ).toString() )

    }catch ( BadRequestException e ) {

       def errors = [ errors: [
      messageService.findOne( 'rsaCryptService.decrypt.wrongKey' ) ] ]
      servletResponse.setStatus( HttpStatus.BAD_REQUEST.value() )
      servletResponse.getWriter().write( new JsonBuilder( errors ).toString() )

    }

  }

  @Override
  void destroy() {}

  private String decryptBody( String body ) throws Exception {

    def bodyMap = new JsonSlurper().parseText( body )

    [ 'username', 'password', 'securityCode', 'state' ].each {

      if ( bodyMap."${it}" ) {
        bodyMap."${it}" = rsaCryptService.decrypt( bodyMap."${it}" )
      }

    }

    new JsonBuilder( bodyMap ).toString()

  }

}
