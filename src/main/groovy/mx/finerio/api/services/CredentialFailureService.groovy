package mx.finerio.api.services

import mx.finerio.api.domain.Callback
import mx.finerio.api.dtos.FailureCallbackDto
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.services.AdminService.EntityType

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CredentialFailureService {

  @Autowired
  AdminService adminService

  @Autowired
  CallbackService callbackService

  @Autowired
  CredentialService credentialService

  @Autowired
  CredentialStatusHistoryService credentialStatusHistoryService

  @Transactional
  void processFailure( FailureCallbackDto dto ) throws Exception {

    if ( !dto ) {
      throw new BadImplementationException(
          'credentialFailureService.processFailure.dto.null' )
    }

    def strStatusCode = String.valueOf( dto?.data?.status_code )
    def credential = credentialService.setFailure(
        dto?.data?.credential_id, strStatusCode )
    credentialStatusHistoryService.update( credential )
    adminService.sendDataToAdmin( EntityType.CONNECTION,
        Boolean.valueOf( false ), credential )
    callbackService.sendToClient( credential?.customer?.client,
        Callback.Nature.FAILURE, [ credentialId: credential.id,
        message: credential.errorCode, code: strStatusCode ] )

  }
    
}
