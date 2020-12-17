package mx.finerio.api.services

import mx.finerio.api.domain.repository.ClientWidgetRepository
import mx.finerio.api.dtos.WidgetEventsDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WidgetEventsService {

  @Value('${firebase.path}')
  String firebasePath

  @Autowired
  CredentialService credentialService

  @Autowired
  FirebaseService firebaseService

  @Autowired
  ClientWidgetRepository clientWidgetRepository

  @Transactional(readOnly = true)
  void onCredentialCreated( WidgetEventsDto dto ) throws Exception {

    if ( !widgetExists( dto.credentialId ) ) { return }
    firebaseService.saveOrUpdate( firebasePath, dto.credentialId,
        [ status: 'CREATED' ] )

  }

  @Transactional(readOnly = true)
  void onAccountCreated( WidgetEventsDto dto ) throws Exception {

    if ( !widgetExists( dto.credentialId ) ) { return }
    firebaseService.saveOrUpdate( firebasePath,
        "${dto.credentialId}/accounts/${dto.accountId}",
        [ name: dto.accountName, status: 'ACCOUNT_CREATED' ] )

  }

  @Transactional(readOnly = true)
  void onTransactionsCreated( WidgetEventsDto dto ) throws Exception {

    if ( !widgetExists( dto.credentialId ) ) { return }
    firebaseService.saveOrUpdate( firebasePath,
        "${dto.credentialId}/accounts/${dto.accountId}",
        [ status: 'TRANSACTIONS_CREATED' ] )

  }

  @Transactional(readOnly = true)
  void onSuccess( WidgetEventsDto dto ) throws Exception {

    if ( !widgetExists( dto.credentialId ) ) { return }
    firebaseService.saveOrUpdate( firebasePath, dto.credentialId,
        [ status: 'SUCCESS' ] )

  }

  @Transactional(readOnly = true)
  void onFailure( WidgetEventsDto dto ) throws Exception {

    if ( !widgetExists( dto.credentialId ) ) { return }
    firebaseService.saveOrUpdate( firebasePath, dto.credentialId,
        [ status: 'FAILURE', message: dto.message, code: dto.code ] )

  }

  @Transactional(readOnly = true)
  void onInteractive( WidgetEventsDto dto ) throws Exception {

    if ( !widgetExists( dto.credentialId ) ) { return }
    firebaseService.saveOrUpdate( firebasePath, dto.credentialId,
        [ status: 'INTERACTIVE', bankToken: dto.bankToken ] )

  }

  private Boolean widgetExists( String credentialId ) throws Exception {

    def credential = credentialService.findAndValidate( credentialId )
    def client = credential.customer.client
    def clientWidget = clientWidgetRepository.findByClient( client )
    return clientWidget != null
   
  }

}
