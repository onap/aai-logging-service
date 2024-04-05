/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.cl.eelf;

import com.att.eelf.configuration.EELFManager;

import java.util.HashMap;
import java.util.Map;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.api.LoggerFactoryInterface;
import org.onap.aai.cl.api.LogLine.LogLineType;

/** This is an implementation of the {@link LoggerFactoryInterface} which
 * constructs a {@link Logger} implementation which is compatible with the EELF
 * framework. */
public class LoggerFactory implements LoggerFactoryInterface {

  /** The instance for our factory singleton. */
  private static LoggerFactory instance;

  /** This cache maintains a mapping of logger names to instances so that if a
   * logger with the same name is requested multiple times we can return the
   * same instance each time. */
  private Map<String, Logger> errorLoggerCache = new HashMap<String, Logger>();

  /** This cache maintains a mapping of metric logger names to instances so that
   * if a logger with the same name is requested multiple times we can return
   * the same instance each time. */
  private Map<String, Logger> metricLoggerCache = new HashMap<String, Logger>();

  /** This cache maintains a mapping of audit logger names to instances so that
   * if a logger with the same name is requested multiple times we can return
   * the same instance each time. */
  private Map<String, Logger> auditLoggerCache = new HashMap<String, Logger>();

  /** Returns the single instance of our factory singleton.
   * 
   * @return - An instance of the {@link LoggerFactory} */
  public static synchronized LoggerFactory getInstance() {

    // If we don't already have an instance then create it now.
    if (instance == null) {
      instance = new LoggerFactory();
    }

    // Return our singleton instance.
    return instance;
  }

  /** 
   * Instantiates a new {@link LoggerFactory}. 
   */
  protected LoggerFactory() {

  }

  /**
   * (non-Javadoc)
   * 
   * @see org.ecomp.cl.api.LoggerFactoryInterface#getLogger(java.lang.String)
   */
  public Logger getLogger(String name) {

    // Check the cache to see if we have already instantiated a logger
    // with the supplied name.
    if (!errorLoggerCache.containsKey(name)) {

      // Nothing in the cache, so let's instantiate a logger now.
      Logger logger = new AaiLoggerAdapter(EELFManager.getLogger(name), LogLineType.ERROR, name);
      errorLoggerCache.put(name, logger);
    }

    // Return the requested logger instance.
    return errorLoggerCache.get(name);
  }

  /** 
   * (non-Javadoc)
   * 
   * @see org.ecomp.cl.api.LoggerFactoryInterface#getLogger(java.lang.Class)
   */
  public Logger getLogger(Class<?> clazz) {

    return getLogger(clazz.getName());
  }

  /** 
   * (non-Javadoc)
   * 
   * @see org.ecomp.cl.api.LoggerFactoryInterface#getAuditLogger(java.lang.String)
   */
  public Logger getAuditLogger(String name) {

    // Check the cache to see if we have already instantiated a logger
    // with the supplied name.
    if (!auditLoggerCache.containsKey(name)) {

      // Nothing in the cache, so let's instantiate a logger now.
      Logger logger = new AaiLoggerAdapter(EELFManager.getAuditLogger(), LogLineType.AUDIT, name);
      auditLoggerCache.put(name, logger);
    }

    // Return the requested logger instance.
    return auditLoggerCache.get(name);
  }

  /** 
   * (non-Javadoc)
   * 
   * @see org.ecomp.cl.api.LoggerFactoryInterface#getAuditLogger(java.lang.Class)
   */
  public Logger getAuditLogger(Class<?> clazz) {

    return getAuditLogger(clazz.getName());
  }

  /** 
   * (non-Javadoc)
   * 
   * @see org.ecomp.cl.api.LoggerFactoryInterface#getMetricsLogger(java.lang.String)
   */
  public Logger getMetricsLogger(String name) {

    // Check the cache to see if we have already instantiated a logger
    // with the supplied name.
    if (!metricLoggerCache.containsKey(name)) {

      // Nothing in the cache, so let's instantiate a logger now.
      Logger logger = new AaiLoggerAdapter(EELFManager.getMetricsLogger(), LogLineType.METRICS,
          name);
      metricLoggerCache.put(name, logger);
    }

    // Return the requested logger instance.
    return metricLoggerCache.get(name);
  }

  /** 
   * (non-Javadoc)
   * 
   * @see org.ecomp.cl.api.LoggerFactoryInterface#getMetricsLogger(java.lang.Class)
   */
  public Logger getMetricsLogger(Class<?> clazz) {

    return getMetricsLogger(clazz.getName());
  }
}
