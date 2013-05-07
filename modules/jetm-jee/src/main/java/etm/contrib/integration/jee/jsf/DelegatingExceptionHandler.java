package etm.contrib.integration.jee.jsf;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.ExceptionHandlerWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: jens
 * Date: 07.05.13
 * Time: 08:11
 * To change this template use File | Settings | File Templates.
 */
public class DelegatingExceptionHandler extends ExceptionHandlerFactory {


  private ExceptionHandlerFactory delegate;

  public DelegatingExceptionHandler(ExceptionHandlerFactory aDelegate) {
    delegate = aDelegate;
  }


  @Override
  public ExceptionHandler getExceptionHandler() {
    return new WrappingExceptionHandler(delegate.getExceptionHandler());
  }

  class WrappingExceptionHandler extends ExceptionHandlerWrapper {

    private ExceptionHandler handler;

    WrappingExceptionHandler(ExceptionHandler aHandler) {
      handler = aHandler;
    }


    @Override
    public void handle() throws FacesException {
      try {
        super.handle();
//        stopMonitoringIfRequired();
      } catch (FacesException e) {
      }
    }

    public ExceptionHandler getWrapped() {
      return handler;
    }
  }
}
