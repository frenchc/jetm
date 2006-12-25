package etm.contrib.aop.aspectwerkz;

import etm.core.monitor.MeasurementPoint;
import org.codehaus.aspectwerkz.AspectContext;
import org.codehaus.aspectwerkz.joinpoint.StaticJoinPoint;

/**
 * AspectWerkz aspect that supports method invocations using a common name for all
 * matching joinpoints. This may be helpful for recording exection times for a group
 * of methods such as DB Interactions.
 * <p/>
 * Interally it uses a static EtmMonitor provided by {@link etm.core.configuration.EtmManager#getEtmMonitor()}.
 * <p/>
 * Example usage that
 * records all method calls of all classes ending with <code>Service</code> using aop.xml.
 * <pre>
 * &lt;aspect class="etm.contrib.aop.aspectwerkz.EtmAspectWerkzAspect" deployment-model="perClass"&gt;
 *  &lt;pointcut name="monitorServices" expression="execution(* ..*Service.*(..))    "/&gt;
 *  &lt;advice name="monitor" type="around" bind-to="monitorServices"/&gt;
 *  &lt;param name="name" value="ServiceExecution"/&gt;
 * &lt;/aspect&gt;
 * </pre>
 *
 * @author void.fm
 * @version $Id$
 */
public class NamedEtmAspectWerkzAspect extends EtmAspectWerkzAspect {

  private static final String PARAM_JOINPOINT_NAME = "name";

  private String measurementPointName;

  public NamedEtmAspectWerkzAspect(AspectContext aContext) {
    measurementPointName = aContext.getParameter(PARAM_JOINPOINT_NAME);
  }

  public Object monitor(StaticJoinPoint joinPoint) throws Throwable {
    MeasurementPoint measurementPoint = new MeasurementPoint(etmMonitor, measurementPointName);
    try {
      return joinPoint.proceed();
    } catch (Throwable t) {
      alterNamePostException(measurementPoint, t);
      throw t;
    } finally {
      measurementPoint.collect();
    }

  }

}
