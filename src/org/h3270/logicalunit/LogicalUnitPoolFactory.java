package org.h3270.logicalunit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h3270.render.H3270Configuration;

public class LogicalUnitPoolFactory {

  protected final static Log logger = LogFactory.getLog(LogicalUnitPoolFactory.class);

  public static LogicalUnitPool createLogicalUnitPool(H3270Configuration configuration) {
      String luBuilderClass = configuration.getLogicalUnitBuilderClass();
      if (luBuilderClass != null) {
        try {
          LogicalUnitBuilder builder = (LogicalUnitBuilder) Class.forName(
              luBuilderClass).newInstance();
          return new LogicalUnitPool(builder);
        } catch (Exception e) {
          String message = "Cannot create an instance of class '" + luBuilderClass
                      + "'. See h3270 configuration of logical units.";
          logger.error(message);
          throw new RuntimeException(message, e);
        }
      }
      return null;
    }
}
