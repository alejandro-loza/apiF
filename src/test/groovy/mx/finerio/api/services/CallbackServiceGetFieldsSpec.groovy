package mx.finerio.api.services

import mx.finerio.api.domain.Callback
import mx.finerio.api.exceptions.BadImplementationException

import spock.lang.Specification

class CallbackServiceGetFieldsSpec extends Specification {

  def service = new CallbackService()

  def "invoking method successfully"() {

    when:
      def result = service.getFields( callback )
    then:
      result instanceof Map
      result.id != null
      result.url != null
      result.nature != null
      result.dateCreated != null
      result.lastUpdated != null
    where:
      callback = getCallback()

  }

  def "parameter 'callback' is null"() {

    when:
      service.getFields( callback )
    then:
      BadImplementationException e = thrown()
      e.message == 'callbackService.getFields.callback.null'
    where:
      callback = null

  }

  private Callback getCallback() throws Exception {

    new Callback(
      id: 1L,
      url: 'url',
      nature: Callback.Nature.SUCCESS,
      dateCreated: new Date(),
      lastUpdated: new Date()
    )

  }

}
