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
package org.onap.aai.cl.api;

import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.cl.mdc.MdcOverride;
import org.slf4j.MDC;

/**
 * This class is used to help standardize how log lines are written and provide
 * profiling info.
 */
public abstract class LogLine {

  public static enum LogLineType {
    AUDIT, ERROR, METRICS
  }

  /**
   * Enumerates the predefined fields of the log line. Note that this
   * enumeration only exposes those fields that the client may set via the
   * {@link LogFields} object. Fields which are automatically populated by the
   * logging service or sourced from the {@link MdcContext} do not appear here.
   */
  public enum DefinedFields {

    STATUS_CODE, 
    RESPONSE_CODE, 
    RESPONSE_DESCRIPTION, 
    INSTANCE_UUID, 
    SEVERITY, 
    SERVER_IP, 
    CLIENT_IP, 
    CLASS_NAME, 
    PROCESS_KEY, 
    TARGET_SVC_NAME, 
    TARGET_ENTITY, 
    ERROR_CODE, 
    ERROR_DESCRIPTION, 
    CUSTOM_1, 
    CUSTOM_2, 
    CUSTOM_3, 
    CUSTOM_4;
  }

  protected String component = "";
  protected String logCode = "";
  protected String level = "";
  protected String message = "";
  protected MdcOverride override = new MdcOverride();
  protected LogFields fields = new LogFields();

  /**
   * Sets common values that the log line will use for populating the log
   * string.
   * 
   * @param component
   *          - The entity invoking the log.
   * @param logCode
   *          - String version of the log message code.
   * @param level
   *          - Log level (DEBUG, TRACE, INFO, WARN, ERROR...)
   * @param msg
   *          - The log message
   * @param fields
   *          - A map of predefined log line fields to values.
   * @param override
   *          - Structure which overrides selective fields in the
   *          {@link MdcContext}
   */
  public void init(String component, String logCode, String level, String msg, LogFields fields,
      MdcOverride override) {

    this.component = component;
    this.logCode = logCode;
    this.level = level;
    this.message = msg;
    this.override = override;
    this.fields = fields;
  }

  protected String getMdcValue(String attribute) {
    if (override.hasOverride(attribute)) {
      return override.getAttributeValue(attribute);
    }

    String value = (String) MDC.get(attribute) == null ? "" : (String) MDC.get(attribute);
    return value;
  }

  public abstract String getFormattedLine();

  protected String fieldValue(Enum field) {
    return (fields.fieldIsSet(field) ? fields.getField(field) : "");
  }
}
