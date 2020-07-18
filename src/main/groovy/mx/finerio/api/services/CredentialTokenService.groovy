package mx.finerio.api.services


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import mx.finerio.api.exceptions.BadImplementationException
import mx.finerio.api.domain.CredentialToken
import mx.finerio.api.domain.repository.CredentialTokenRepository


@Service
class CredentialTokenService {

  @Autowired
  CredentialTokenRepository credentailTokenRepository


  CredentialToken saveUpdateCredentialToken( String credentialId, String tokenClientId  ){

    validateCredentialId( credentialId )
    validateTokenClientId( tokenClientId )

    def credentialToken = credentailTokenRepository.findByCredentialId( credentialId )

    if( credentialToken != null ){
      credentialToken.tokenClientId = tokenClientId
      credentialToken.lastUpdated = new Date()
    }else{
      credentialToken = new CredentialToken(
            tokenClientId: tokenClientId,
            credentialId: credentialId,
            dateCreated: new Date(),
            lastUpdated: new Date()
        )
    }

    credentailTokenRepository.save( credentialToken )
  }


 String findTokenClientIdByCredentilId( String credentialId ){
  validateCredentialId( credentialId )
  def credentialToken = credentailTokenRepository.findByCredentialId( credentialId )

   if( credentialToken == null ){
      throw new BadImplementationException(
        'findTokenClientIdByCredentilId.credentialToken.notFound' )
   }

   credentialToken.tokenClientId 
 } 

private validateCredentialId( String credentialId ) {
  if( credentialId == null || credentialId.isEmpty() ){
    throw new BadImplementationException(
        'credentialTokenService.saveUpdateClientId.validateCredentialId.null' )
  }
}

private validateTokenClientId( String tokenClientId  ) {
 if( tokenClientId == null || tokenClientId.isEmpty() ){
    throw new BadImplementationException(
        'credentialTokenService.saveUpdateClientId.validateTokenClientId.null' )
  }

}

}