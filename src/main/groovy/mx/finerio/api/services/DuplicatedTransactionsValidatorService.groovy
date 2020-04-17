package mx.finerio.api.services

import groovy.json.*

import mx.finerio.api.dtos.*

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import wslite.http.auth.*
import wslite.rest.*

@Service
class DuplicatedTransactionsValidatorService {

  @Value( '${transactions-api.url}' )
  String transactionsApiUrl

  @Value( '${transactions-api.auth.username}' )
  String transactionsApiUsername

  @Value( '${transactions-api.auth.password}' )
  String transactionsApiPassword
  
  boolean validateTransactionsFromSameDate(
      DuplicatedTransactionDto transactionDto,
      List<DuplicatedTransactionDto> transactionsToCompare ) {
      
    for ( transactionToCompare in transactionsToCompare ) {
    
      if ( transactionToCompare.transactionId == null &&
          transactionToCompare.description == transactionDto.description &&
          transactionToCompare.deposit == transactionDto.deposit &&
          transactionToCompare.amount == transactionDto.amount ) {
        return true
      }
       
      if ( transactionToCompare.transactionId != null &&
          transactionToCompare.transactionId == transactionDto.transactionId &&
          transactionToCompare.deposit == transactionDto.deposit &&
          transactionToCompare.amount == transactionDto.amount ) {
        return true
      }
       
    }
    
    return false
    
  }
  
  boolean validateTransactions( DuplicatedTransactionDto transactionDto,
      List<DuplicatedTransactionDto> transactionsToCompare ) {
    
    if ( transactionsToCompare.isEmpty() ) { return false }
    def descriptions = [ transactionDto.description ]
    descriptions.addAll( transactionsToCompare*.description )
    def client = new RESTClient( transactionsApiUrl )
    client.authorization = new HTTPBasicAuthorization(
        transactionsApiUsername, transactionsApiPassword )
    def response = client.get( path: '/searchAll',
        query: [ list: descriptions.join( ',' ) ] )
    def json = new JsonSlurper().parseText(
        new String( response.data, 'UTF-8' ) )

    for ( item in json.result.results ) {
      if ( !item.reason.data.equals( "Not found" ) &&
          item.similarity.percent > 80 ) {
        return true
      }
      
    }
    
    return false
    
  }
  
}

