/*-
 * ============LICENSE_START=======================================================
 * Common Logging Library
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights
 * 						reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.cl.api;

import org.openecomp.cl.mdc.MdcOverride;

/** Defines the common API which all Logger implementations must expose. */
public interface Logger {

  /**
   * Indicate whether or not TRACE level logging is enabled.
   *
   * @return true if TRACE level logs are enabled, false otherwise
   */
  public boolean isTraceEnabled();

  /**
   * Indicate whether or not INFO level logging is enabled.
   *
   * @return true if INFO level logs are enabled, false otherwise
   */
  public boolean isInfoEnabled();

  /**
   * Indicate whether or not ERROR level logging is enabled.
   *
   * @return true if ERROR level logs are enabled, false otherwise
   */
  public boolean isErrorEnabled();

  /**
   * Indicate whether or not WARNING level logging is enabled.
   *
   * @return true if WARNING level logs are enabled, false otherwise
   */
  public boolean isWarnEnabled();

  /**
   * Indicate whether or not DEBUG level logging is enabled.
   *
   * @return true if DEBUG level logs are enabled, false otherwise
   */
  public boolean isDebugEnabled();

  /**
   * Log an INFO message based on a message key defined in a resource bundle
   * with arguments.
   * 
   * @param logCode
   *          - Log message identifier.
   * @param arguments
   *          - Arguments to populate the log message template with.
   */
  public void info(Enum logCode, String... arguments);

  /**
   * Log an INFO message based on a message key defined in a resource bundle
   * with arguments.
   * 
   * @param logCode
   *          - Log message identifier.
   * @param fields
   *          - Map containing values for any log fields which the client wants
   *          to populate.
   * @param arguments
   *          - Arguments to populate the log message template with.
   */
  public void info(Enum logCode, LogFields fields, String... arguments);

  /**
   * Log an INFO message based on a message key defined in a resource bundle
   * with arguments.
   * 
     * @param logCode   - Log message identifier.
     * @param fields    - Map containing values for any log fields which the 
     *                    client wants to populate.
     * @param override  - A set of values to override values stored in the MDC context
     * @param arguments - Arguments to populate the log message template with.
   */
  public void info(Enum logCode, LogFields fields, MdcOverride override, String... arguments);

  /**
   * Log a WARNING message based on a message key defined in a resource bundle
   * with arguments.
   * 
     * @param logCode   - Log message identifier.
     * @param arguments - Arguments to populate the log message template with.
   */
  public void warn(Enum logCode, String... arguments);

  /**
   * Log a WARNING message based on a message key defined in a resource bundle
   * with arguments.
   * 
   * @param logCode   - Log message identifier.
   * @param fields    - Map containing values for any log fields which the 
   *                    client wants to populate.
   * @param arguments - Arguments to populate the log message template with.
   */
  public void warn(Enum logCode, LogFields fields, String... arguments);

  /**
   * Log a TRACE message based on a message key defined in a resource bundle
   * with arguments.
   * 
   * @param logCode   - Log message identifier.
   * @param arguments - Arguments to populate the log message template with.
   */
  public void trace(Enum logCode, String... arguments);

  /**
   * Log a TRACE message based on a message key defined in a resource bundle
   * with arguments.
   * 
   * @param logCode   - Log message identifier.
   * @param fields    - Map containing values for any log fields which the 
   *                    client wants to populate.
   * @param arguments - Arguments to populate the log message template with.
   */
  public void trace(Enum logCode, LogFields fields, String... arguments);

  /**
   * Log a simple, non-templated DEBUG message.
   * 
   * @param logMessage - The message to be logged.
   */
  public void debug(String logMessage);

  /**
   * Log a DEBUG message based on a message key defined in a resource bundle
   * with arguments.
   * 
   * @param logCode   - Log message identifier.
   * @param arguments - Arguments to populate the log message template with.
   */
  public void debug(Enum logCode, String... arguments);

  /**
   * Log a DEBUG message based on a message key defined in a resource bundle
   * with arguments.
   * 
   * @param logCode   - Log message identifier.
   * @param fields    - Map containing values for any log fields which the 
   *                    client wants to populate.
   * @param arguments - Arguments to populate the log message template with.
   */
  public void debug(Enum logCode, LogFields fields, String... arguments);

  /**
   * Log an ERROR message based on a message key defined in a resource bundle
   * with arguments.
   * 
   * @param logCode   - Log message identifier.
   * @param arguments - Arguments to populate the log message template with.
   */
  public void error(Enum logCode, String... arguments);

  /**
   * Log an ERROR message based on a message key defined in a resource bundle
   * with arguments.
   * 
   * @param logCode   - Log message identifier.
   * @param fields    - Map containing values for any log fields which the 
   *                    client wants to populate.
   * @param arguments - Arguments to populate the log message template with.
   */
  public void error(Enum logCode, LogFields fields, String... arguments);

  /**
   * Log an ERROR message based on a message key defined in a resource bundle
   * with arguments and a throwable exception.
   * 
   * @param logCode   - Log message identifier.
   * @param ex        - The exception to be logged.
   * @param arguments - Arguments to populate the log message template with.
   */
  public void error(Enum logCode, Throwable ex, String... arguments);

  /**
   * Log an ERROR message based on a message key defined in a resource bundle
   * with arguments and a throwable exception.
   * 
   * @param logCode   - Log message identifier.
   * @param fields    - Map containing values for any log fields which the 
   *                    client wants to populate.
   * @param ex        - The exception to be logged.
   * @param arguments - Arguments to populate the log message template with.
   */
  public void error(Enum logCode, LogFields fields, Throwable ex, String... arguments);

  /**
   * Format the given log using the supplied arguments
   * @param logCode   - Log message identifier.
   * @param arguments - Arguments to populate the log message template with.
   */
  public String formatMsg(Enum logCode, String... arguments);

}
