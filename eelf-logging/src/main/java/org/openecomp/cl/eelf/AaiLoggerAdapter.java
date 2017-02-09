/*-
 * ============LICENSE_START=======================================================
 * Common Logging Library
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.openecomp.cl.eelf;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFLogger.Level;
import com.att.eelf.i18n.EELFResolvableErrorEnum;
import com.att.eelf.i18n.EELFResourceManager;

import org.openecomp.cl.api.LogFields;
import org.openecomp.cl.api.LogLine;
import org.openecomp.cl.api.LogLine.LogLineType;
import org.openecomp.cl.api.Logger;
import org.openecomp.cl.mdc.MdcOverride;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a logging implementation which wraps the EELF logging
 * framework.
 */
public class AaiLoggerAdapter implements Logger {

  public static final String BAD_ENUM_MSG = "UNRECOGNIZABLE ERROR CODE ";

  /** Field name to use for the message portion of our log lines. */
  public static final String MESSAGE_PREFIX = "Msg";

  /**
   * A place holder to use for fields in the standardized log message that we
   * are not explicitly setting.
   */
  public static final String NOT_APPLICABLE = "na";

  /**
   * The instance of the actual EELF logger that we will be sending our messages
   * to.
   */
  private EELFLogger eelfLogger;

  /**
   * This indicates the logging format type. It is used for deciding the string
   * builder for constructing standardized log statements.
   */
  private LogLineType logLineType;

  /** An identifier for the component that is generating the log statements. */
  private String component = NOT_APPLICABLE;

  /**
   * Creates a new instance of the {@link AaiLoggerAdapter}, backed by the
   * supplied {@link EELFLogger} instance.
   * 
   * @param eelfLogger
   *          - The instance of {@link EELFLogger} that this logger will invoke.
   */
  public AaiLoggerAdapter(EELFLogger eelfLogger, LogLineType logLineType, String componentName) {

    // Store the supplied EELFLogger instance.
    this.eelfLogger = eelfLogger;
    this.logLineType = logLineType;
    this.component = componentName;
  }

  @Override
  public boolean isTraceEnabled() {
    return eelfLogger.isTraceEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return eelfLogger.isInfoEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return eelfLogger.isErrorEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return eelfLogger.isWarnEnabled();
  }

  @Override
  public boolean isDebugEnabled() {
    return eelfLogger.isDebugEnabled();
  }

  /**
   * Sets a number of the common fields which prefix all standard log
   * statements.
   */
  private void initLogLine(LogLine logLine, String level, String logCode, String msg,
      LogFields fields) {
    logLine.init(component, logCode, level, msg, fields, new MdcOverride());
  }

  private void initLogLine(LogLine logLine, String level, String logCode, String msg,
      LogFields fields, MdcOverride override) {
    logLine.init(component, logCode, level, msg, fields, override);
  }

  @Override
  public void info(Enum logCode, String... arguments) {
    info(logCode, new LogFields(), arguments);
  }

  @Override
  public void info(Enum logCode, LogFields fields, String... arguments) {

    // We expect our error code to be compatible with the templating
    // functionality provided by the EELF framework, so make sure
    // that this is the case before we try to decode our error code.
    if (logCode instanceof LogMessageEnum) {
      // Cast our error code enum to make the EELF framework happy.
      LogMessageEnum eelfLogCode = (LogMessageEnum) logCode;

      // Initialize the log line
      LogLine logLine = getLogLine();
      initLogLine(logLine, Level.INFO.toString(), EELFResourceManager.getIdentifier(eelfLogCode),
          EELFResourceManager.format(eelfLogCode, arguments),
          (fields == null) ? new LogFields() : fields);

      // Pass our log string to the EELF logging framework.
      eelfLogger.info(logLine.getFormattedLine());
    } else {
      eelfLogger.error(BAD_ENUM_MSG + logCode.toString());
    }
  }

  @Override
  public void info(Enum logCode, LogFields fields, MdcOverride override, String... arguments) {

    // We expect our error code to be compatible with the templating
    // functionality provided by the EELF framework, so make sure
    // that this is the case before we try to decode our error code.
    if (logCode instanceof LogMessageEnum) {
      // Cast our error code enum to make the EELF framework happy.
      LogMessageEnum eelfLogCode = (LogMessageEnum) logCode;

      // Initialize the log line
      LogLine logLine = getLogLine();
      initLogLine(logLine, Level.INFO.toString(), EELFResourceManager.getIdentifier(eelfLogCode),
          EELFResourceManager.format(eelfLogCode, arguments),
          (fields == null) ? new LogFields() : fields, override);

      // Pass our log string to the EELF logging framework.
      eelfLogger.info(logLine.getFormattedLine());
    } else {
      eelfLogger.error(BAD_ENUM_MSG + logCode.toString());
    }
  }

  @Override
  public void debug(String message) {
    // Initialize the log line
    LogLine logLine = getLogLine();
    initLogLine(logLine, Level.DEBUG.toString(), "", message, new LogFields());

    // Pass our log string the the EELF logging framework.
    eelfLogger.debug(logLine.getFormattedLine());
  }

  @Override
  public void debug(Enum logCode, String... arguments) {
    debug(logCode, new LogFields(), arguments);
  }

  @Override
  public void debug(Enum logCode, LogFields fields, String... arguments) {

    // We expect our log code to be compatible with the templating
    // functionality provided by the EELF framework, so make sure
    // that this is the case before we try to decode it.
    if (logCode instanceof LogMessageEnum) {
      // Cast our error code enum to make the EELF framework happy.
      LogMessageEnum eelfLogCode = (LogMessageEnum) logCode;

      // Initialize the log lineLogLine logLine = getLogLine();
      LogLine logLine = getLogLine();
      initLogLine(logLine, Level.DEBUG.toString(), EELFResourceManager.getIdentifier(eelfLogCode),
          EELFResourceManager.format(eelfLogCode, arguments),
          (fields == null) ? new LogFields() : fields);

      // Pass our log string to the EELF logging framework.
      eelfLogger.debug(logLine.getFormattedLine());
    } else {
      eelfLogger.error(BAD_ENUM_MSG + logCode.toString());
    }
  }

  @Override
  public void warn(Enum logCode, String... arguments) {
    warn(logCode, new LogFields(), arguments);
  }

  @Override
  public void warn(Enum logCode, LogFields fields, String... arguments) {

    // We expect our log code to be compatible with the templating
    // functionality provided by the EELF framework, so make sure
    // that this is the case before we try to decode our it.
    if (logCode instanceof LogMessageEnum) {
      // Cast our error code enum to make the EELF framework happy.
      LogMessageEnum eelfLogCode = (LogMessageEnum) logCode;

      // Initialize the log line
      LogLine logLine = getLogLine();
      initLogLine(logLine, Level.WARN.toString(), EELFResourceManager.getIdentifier(eelfLogCode),
          EELFResourceManager.format(eelfLogCode, arguments),
          (fields == null) ? new LogFields() : fields);

      // Pass our log string to the EELF logging framework.
      eelfLogger.warn(logLine.getFormattedLine());
    } else {
      eelfLogger.error(BAD_ENUM_MSG + logCode.toString());
    }
  }

  @Override
  public void trace(Enum logCode, String... arguments) {
    trace(logCode, new LogFields(), arguments);
  }

  @Override
  public void trace(Enum logCode, LogFields fields, String... arguments) {

    // We expect our log code to be compatible with the templating
    // functionality provided by the EELF framework, so make sure
    // that this is the case before we try to decode our it.
    if (logCode instanceof LogMessageEnum) {
      // Cast our error code enum to make the EELF framework happy.
      LogMessageEnum eelfLogCode = (LogMessageEnum) logCode;

      // Initialize the log line
      LogLine logLine = getLogLine();
      initLogLine(logLine, Level.TRACE.toString(), EELFResourceManager.getIdentifier(eelfLogCode),
          EELFResourceManager.format(eelfLogCode, arguments),
          (fields == null) ? new LogFields() : fields);

      // Pass our log string to the EELF logging framework.
      eelfLogger.trace(logLine.getFormattedLine());

    } else {
      eelfLogger.error(BAD_ENUM_MSG + logCode.toString());
    }
  }

  @Override
  public void error(Enum logCode, String... arguments) {
    error(logCode, new LogFields(), arguments);
  }

  @Override
  public void error(Enum logCode, LogFields fields, String... arguments) {

    // We expect our log code to be compatible with the templating
    // functionality provided by the EELF framework, so make sure
    // that this is the case before we try to decode it.
    if (logCode instanceof LogMessageEnum) {

      // Cast our error code enum to make the EELF framework happy.
      LogMessageEnum eelfLogCode = (LogMessageEnum) logCode;

      // Initialize the log line
      LogLine logLine = getLogLine();
      initLogLine(logLine, Level.ERROR.toString(), EELFResourceManager.getIdentifier(eelfLogCode),
          EELFResourceManager.format(eelfLogCode, arguments),
          (fields == null) ? new LogFields() : fields);

      // Pass our log string to the EELF logging framework.
      eelfLogger.error(logLine.getFormattedLine());

    } else {
      eelfLogger.error(BAD_ENUM_MSG + logCode.toString());
    }
  }

  @Override
  public void error(Enum logCode, Throwable ex, String... arguments) {
    error(logCode, new LogFields(), ex, arguments);
  }

  @Override
  public void error(Enum logCode, LogFields fields, Throwable ex, String... arguments) {

    // We expect our log code to be compatible with the templating
    // functionality provided by the EELF framework, so make sure
    // that this is the case before we try to decode it.
    if (logCode instanceof LogMessageEnum) {
      // Cast our error code enum to make the EELF framework happy.
      LogMessageEnum eelfLogCode = (LogMessageEnum) logCode;

      // Initialize the log line
      LogLine logLine = getLogLine();
      initLogLine(logLine, Level.ERROR.toString(), EELFResourceManager.getIdentifier(eelfLogCode),
          EELFResourceManager.format(eelfLogCode, arguments),
          (fields == null) ? new LogFields() : fields);

      // Pass our log string to the EELF logging framework.
      eelfLogger.error(logLine.getFormattedLine(), ex);

    } else {
      eelfLogger.error(BAD_ENUM_MSG + logCode.toString());
    }
  }

  @Override
  public String formatMsg(Enum logCode, String... arguments) {
    return EELFResourceManager.getMessage((EELFResolvableErrorEnum) logCode, arguments);
  }

  private LogLine getLogLine() {
    if (logLineType == LogLineType.AUDIT) {
      return new AuditLogLine();
    }

    if (logLineType == LogLineType.ERROR) {
      return new ErrorLogLine();
    }

    if (logLineType == LogLineType.METRICS) {
      return new MetricsLogLine();
    }

    eelfLogger.warn("Unsupported LogLineType: " + logLineType);
    return null;
  }
}
