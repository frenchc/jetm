package etm.contrib.aop.aspectwerkz;

import etm.core.monitor.EtmPoint;
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
 * @version $Revision$
 */
public class NamedEtmAspectWerkzAspect extends EtmAspectWerkzAspect {

  private static final String PARAM_JOINPOINT_NAME = "name";

  private String etmPointName;

  public NamedEtmAspectWerkzAspect(AspectContext aContext) {
    etmPointName = aContext.getParameter(PARAM_JOINPOINT_NAME);
  }

  public Object monitor(StaticJoinPoint joinPoint) throws Throwable {
    EtmPoint etmPoint = etmMonitor.createPoint(etmPointName);
    try {
      return joinPoint.proceed();
    } catch (Throwable t) {
      alterNamePostException(etmPoint, t);
      throw t;
    } finally {
      etmPoint.collect();
    }

  }

}
