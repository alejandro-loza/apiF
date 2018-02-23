package mx.finerio.api.services

import javax.validation.Valid

import mx.finerio.api.domain.BankConnection
import mx.finerio.api.domain.Credential
import mx.finerio.api.domain.repository.BankConnectionRepository
import mx.finerio.api.exceptions.BadImplementationException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BankConnectionService {

  @Autowired
  BankConnectionRepository bankConnectionRepository

  BankConnection create( Credential credential ) throws Exception {

    if ( !credential ) {
      throw new BadImplementationException(
          'bankConnectionService.create.credential.null' )
    }
 
    def bankConnection = new BankConnection()
    bankConnection.startDate = new Date()
    bankConnection.credential = credential
    bankConnection.status = BankConnection.Status.PENDING
    bankConnectionRepository.save( bankConnection )
    bankConnection

  }

}
