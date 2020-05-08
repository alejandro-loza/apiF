package mx.finerio.api.services

import org.springframework.stereotype.Service
import mx.finerio.api.domain.Customer
import org.springframework.scheduling.annotation.Async
import org.springframework.beans.factory.annotation.Autowired
import mx.finerio.api.domain.repository.AccountCredentialRepository
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.Account
import mx.finerio.api.domain.Transaction


@Service
class AdminService {

  @Autowired
  AdminQueueService adminQueueService

  @Autowired
  AccountCredentialRepository accountCredentialRepository

  enum Classes{
    Customer, Credential, Account, Transaction, Boolean
  }

  @Async
  void sendDataToAdmin( Object data, Object data2 = null ){

    Classes clazz = Classes.valueOf( data.getClass().getSimpleName() )

    switch ( clazz) {
      case Classes.Customer:
        Customer customer = (Customer) data 
        sendCustomerToAdmin( customer )
      break

      case Classes.Credential:
        Credential credential = (Credential) data 
        sendCredentialToAdmin( credential )
      break

      case Classes.Account:
        Account account = (Account) data 
        Credential credential = (Credential) data2 
        sendAccountToAdmin( account, credential )
      break

      case Classes.Transaction:
        Transaction transaction = (Transaction) data 
        sendTransactionToAdmin( transaction )
      break

      case Classes.Boolean:
        Boolean isSuccessful = (Boolean) data 
        Credential credential = (Credential) data2 
        sendConnectionToAdmin( credential, isSuccessful )
      break 

    } 
}

  private void sendCustomerToAdmin( Customer customer ){
     
    def clientId = customer?.client?.id
    def data = [ clientId: clientId, customerId: customer.id, date: customer.dateCreated.time ]
    adminQueueService.queueMessage( data, 'CREATE_CUSTOMER')

  }

  private void sendCredentialToAdmin( Credential credential ){  
     
    def clientId = credential?.customer?.client?.id
    def data = [ clientId: clientId, customerId: credential?.customer.id, 
      credentialId:credential?.id,date: credential.dateCreated.time ]
    adminQueueService.queueMessage( data, 'CREATE_CREDENTIAL')   
      
  }

   private void sendAccountToAdmin( Account account, Credential credential ){  
     
    def clientId = credential?.customer?.client?.id
    def data = [ clientId: clientId, customerId: credential?.customer.id, 
      credentialId:credential?.id,accountId:account.id, date: account.dateCreated.time ]
  
    adminQueueService.queueMessage( data, 'CREATE_ACCOUNT')      
  }

  private void sendTransactionToAdmin( Transaction transaction ){ 

    def accountCredential = accountCredentialRepository.findFirstByAccountId( transaction?.account.id )      
    def clientId = accountCredential?.credential?.customer?.client?.id
    def data = [ clientId: clientId, customerId: accountCredential?.credential?.customer.id, 
      credentialId:accountCredential?.credential?.id,accountId: transaction?.account.id, date: transaction.dateCreated.time ]
    adminQueueService.queueMessage( data, 'CREATE_MOVEMENT')   
      
  }

  private void sendConnectionToAdmin( Credential credential, Boolean isSuccessful ){  
         
    def clientId = credential?.customer?.client?.id
    String institutionCode = credential?.institution?.code
    def data = [ clientId: clientId, customerId: credential?.customer.id, 
    credentialId:credential?.id,institutionCode:institutionCode, 
    isSuccessful: isSuccessful, date: new Date().time ]
    adminQueueService.queueMessage( data, 'CREATE_CONNECTION')

  }

}

