package mx.finerio.api.filters

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

import org.springframework.beans.factory.annotation.Autowired

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class JsonFilter implements Filter {

  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.filters.JsonFilter' )

  @Override
  void init( FilterConfig filterConfig ) throws ServletException {}

  @Override
  void doFilter( ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain ) throws IOException, ServletException {

    def httpRequest = servletRequest as HttpServletRequest
    def newRequest = httpRequest
    def body = httpRequest.inputStream.text
    log.info( "<< body: {}", body )
    newRequest = new RequestWrapper( httpRequest, body.bytes )
    filterChain.doFilter( newRequest, servletResponse )

  }

  @Override
  void destroy() {}

}
