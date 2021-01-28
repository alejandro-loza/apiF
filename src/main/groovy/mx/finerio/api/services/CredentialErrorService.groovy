package mx.finerio.api.services

import groovy.json.JsonSlurper

import mx.finerio.api.dtos.ApiListDto
import mx.finerio.api.dtos.CredentialErrorDto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import org.springframework.beans.factory.annotation.Autowired

@Service
class CredentialErrorService {
  
  @Autowired
  ScraperV2ClientService scraperV2ClientService

  private List<CredentialErrorDto> cache = null

  ApiListDto findAll() throws Exception {

    def dto = new ApiListDto()

    if ( this.cache != null ) {
      dto.data = this.cache
    } else {
      
      def errors = scraperV2ClientService.getErrors()
      def errorsParsed = createDtoList( errors )
      dto.data = errorsParsed
      this.cache = errorsParsed

    }

    return dto

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
