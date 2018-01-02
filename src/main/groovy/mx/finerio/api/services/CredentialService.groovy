package mx.finerio.api.services

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

import mx.finerio.api.domain.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.io.IOException

import okhttp3.OkHttpClient
import okhttp3.*

import groovy.json.JsonSlurper

@Service
class CredentialService {

  @Autowired
  CredentialPersistenceService credentialPersistenceService

  @Autowired
  DevScraperService scraperService

  void requestData( String credentialId ) throws Exception {

    def credential = credentialPersistenceService.findOne( credentialId )

    if ( !credential ) {
      throw new IllegalArgumentException(
          'credential.requestData.credential.null' )
    }

    def data = [
      id: credential.id,
      username: credential.username,
      password: credential.password,
      iv: credential.iv,
      user: [ id: credential.user.id ],
      institution: [ id: credential.institution.id ],
      securityCode: credential.securityCode
    ]

    scraperService.requestData( data )

  }

}
