package mx.finerio.api.services


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.domain.CredentialState
import mx.finerio.api.domain.repository.CredentialStateRepository


@Service
class CredentialStateService {

  @Autowired
  CredentialStateRepository credentailStateRepository


  CredentialState save( String credentialId, String state  ){

    validateCredentialId( credentialId )
    validateState( state )

    def credentialState = credentailStateRepository.findByCredentialId(
      credentialId )
    if ( credentialState == null ) {
      credentialState = new CredentialState(
        state: state,
        credentialId: credentialId,
        dateCreated: new Date(),
        lastUpdated: new Date()
      )
    } else {
      credentialState.state = state
      credentialState.lastUpdated = new Date()
    }
    
    credentailStateRepository.save( credentialState )
  }


 private String findStateByCredentilId( String credentialId ){
  validateCredentialId( credentialId )
  def credentialState = credentailStateRepository.findByCredentialId( credentialId )

   if( credentialState == null ){
    return null
   }

   credentialState.state 
 } 

 Boolean  addState( String credentialId, Map data ) {
   String state = findStateByCredentilId( credentialId )
     if( state ){
       data.state = state
       return true
     }
  false
 }

private validateCredentialId( String credentialId ) {
  if( credentialId == null || credentialId.isEmpty() ){
    throw new BadImplementationException(
        'credentialStateService.saveCredentialState.credentialId.null' )
  }
}

private validateState( String state  ) {
 if( state == null || state.isEmpty() ){
    throw new BadImplementationException(
        'credentialStateService.saveCredentialState.state.null' )
  }

}

}
