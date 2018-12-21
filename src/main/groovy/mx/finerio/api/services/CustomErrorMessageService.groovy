package mx.finerio.api.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
class CustomErrorMessageService {
  final static Logger log = LoggerFactory.getLogger(
      'mx.finerio.api.services.CustomErrorMessageService' )

   	@Value('${mail.service.template.customMsg.error408}')
	String templateName408

   	@Value('${mail.service.template.customMsg.error203}')
	String templateName203

   	@Value('${mail.service.template.customMsg.error401}')
	String templateName401

   	@Value('${mail.service.template.customMsg.error4011}')
	String templateName4011

   	@Value('${mail.service.template.customMsg.error503}')
	String templateName503

   	@Value('${mail.service.template.customMsg.error500}')
	String templateName500

	@Autowired
	EmailRestService emailRestService

	void sendCustomEmail( String emailId, String statusCode ) {

		try{
			emailRestService.send( emailId, this."templateName${statusCode}", [:] )
		}catch(groovy.lang.MissingPropertyException e){
			log.error("Unknown status code: ${statusCode} mail not sent")
		
		}
	}

}

