package mx.finerio.api.services

import com.google.auth.oauth2.GoogleCredentials

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FirebaseService {

  @Autowired
  FirebaseService(
    @Value('${firebase.json.file}') String jsonFile,
    @Value('${firebase.database.url}' ) String databaseUrl ) { 
    setup( jsonFile, databaseUrl )
  }

  void saveOrUpdate( String path, String key, Map data ) throws Exception {

    validateSaveOrUpdateInput( path, key, data )
    def reference = FirebaseDatabase.getInstance().getReference( path )
    def dataToSave = [ (key): data ]
    reference.updateChildrenAsync( dataToSave )

  }

  private void setup( String jsonFile, String databaseUrl )
      throws Exception {

    def serviceAccount = new FileInputStream( jsonFile )
    def options = new FirebaseOptions.Builder()
      .setCredentials( GoogleCredentials.fromStream( serviceAccount ) ) 
      .setDatabaseUrl( databaseUrl )
      .build()
    FirebaseApp.initializeApp( options )

  }

  private validateSaveOrUpdateInput( String path, String key, Map data )
      throws Exception {

    if ( path == null ) { 
      throw new IllegalArgumentException(
          'firebaseService.saveOrUpdate.path.null' )
    }   

    if ( key == null ) { 
      throw new IllegalArgumentException(
          'firebaseService.saveOrUpdate.key.null' )
    }   

    if ( data == null ) { 
      throw new IllegalArgumentException(
          'firebaseService.saveOrUpdate.data.null' )
    }   

  }

}

