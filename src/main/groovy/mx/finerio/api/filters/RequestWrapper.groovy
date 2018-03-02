package mx.finerio.api.filters

import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class RequestWrapper extends HttpServletRequestWrapper {

  byte[] body

  RequestWrapper( HttpServletRequest request, byte[] body ) {

    super( request )
    this.body = body

  }

  @Override
  ServletInputStream getInputStream() {
    new ApiServletInputStream( body )
  }

}

class ApiServletInputStream extends ServletInputStream {

  def inputStream

  ApiServletInputStream( byte[] data ) {
    this.inputStream = new ByteArrayInputStream( data )
  }

  @Override
  int read() throws IOException {
    inputStream.read()
  }

  @Override
  boolean isFinished() {
    inputStream.available() == 0
  }

  @Override
  boolean isReady() {
    true
  }

  @Override
  void setReadListener(ReadListener listener) {
      throw new RuntimeException( 'Not implemented' )
  }

}
