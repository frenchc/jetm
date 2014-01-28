package etm.contrib.integration.cdi.common.spi;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

/**
 * A literal to instantiate the {@link Named} annotation.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class NamedLiteral extends AnnotationLiteral<Named> implements Named {

  private String name;

  public NamedLiteral(String name) {
    this.name = name;
  }

  public String value() {
    return this.name;
  }
}
