package mx.finerio.api.services

import groovy.json.JsonSlurper

import mx.finerio.api.dtos.ApiListDto
import mx.finerio.api.dtos.CredentialErrorDto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient

@Service
class CredentialErrorService {

  @Value( '${scraper.catalog.errors.login.url}' )
  String loginUrl

  @Value( '${scraper.catalog.errors.login.path}' )
  String loginPath

  @Value( '${scraper.catalog.errors.login.clientId}' )
  String loginClientId

  @Value( '${scraper.catalog.errors.login.clientSecret}' )
  String loginClientSecret

  @Value( '${scraper.catalog.errors.url}' )
  String errorsUrl

  @Value( '${scraper.catalog.errors.path}' )
  String errorsPath

  private List<CredentialErrorDto> cache = null

  ApiListDto findAll() throws Exception {

    def dto = new ApiListDto()

    if ( this.cache != null ) {
      dto.data = this.cache
    } else {

      def accessToken = getAccessToken()
      def errors = getErrors( accessToken )
      def errorsParsed = createDtoList( errors )
      dto.data = errorsParsed
      this.cache = errorsParsed

    }

    return dto

  }

  private String getAccessToken() throws Exception {

    def client = new RESTClient( loginUrl )
    client.authorization = new HTTPBasicAuthorization( loginClientId,
        loginClientSecret )

    def response = client.post( path: loginPath ) {
      urlenc grant_type: 'client_credentials'
    }

    def jsonMap = new JsonSlurper().parseText( new String( response.data ) )
    return jsonMap.access_token

  }

  private List getErrors( String accessToken ) throws Exception {

    def client = new RESTClient( errorsUrl )
    def headers = [ 'Authorization': "Bearer ${accessToken}" ]
    def response = client.get( path: errorsPath, headers: headers )
    def jsonMap = new JsonSlurper().parseText( new String( response.data ) )
    return jsonMap.data

  }

  private List<CredentialErrorDto> createDtoList( List jsonArray )
      throws Exception {

    def dtoList = []

    for ( jsonObject in jsonArray ) {

      def dto = new CredentialErrorDto()
      dto.code = jsonObject.code
      dto.key = jsonObject.key
      dto.description = jsonObject.description
      dto.text = jsonObject.text
      dtoList << dto

    }

    return dtoList

  }

}
