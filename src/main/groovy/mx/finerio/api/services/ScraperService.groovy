package mx.finerio.api.services

import mx.finerio.api.domain.Credential

interface ScraperService {

  Map requestData( Credential credential ) throws Exception

  Map requestData( Map data ) throws Exception

}
