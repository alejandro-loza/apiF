package mx.finerio.api.services

import groovy.json.JsonSlurper

import mx.finerio.api.dtos.ApiListDto
import mx.finerio.api.dtos.CredentialErrorDto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient

@Service
class ScraperV2ClientService {

  @Value( '${scraper.v2.login.url}' )
  String loginUrl

  @Value( '${scraper.v2.login.path}' )
  String loginPath

  @Value( '${scraper.v2.login.clientId}' )
  String loginClientId

  @Value( '${scraper.v2.login.clientSecret}' )
  String loginClientSecret

  @Value( '${scraper.v2.url}' )
  String scraperV2Url

  @Value( '${scraper.v2.path}' )
  String scraperV2Path

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

  List getErrors() throws Exception {
    def accessToken = getAccessToken()
    def client = new RESTClient( scraperV2Url )
    def headers = [ 'Authorization': "Bearer ${accessToken}" ]
    def response = client.get( path: scraperV2Path, headers: headers )
    def jsonMap = new JsonSlurper().parseText( new String( response.data ) )
    return jsonMap.data

  }



}
