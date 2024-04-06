/*-
 * ============LICENSE_START=======================================================
 * Common Logging Library
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights
 *                  reserved.
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

package org.onap.aai.cl.eelf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.i18n.EELFResolvableResourceEnum;

import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.cl.api.LogFields;
import org.onap.aai.cl.api.LogLine;
import org.onap.aai.cl.api.LogLine.DefinedFields;
import org.onap.aai.cl.api.LogLine.LogLineType;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.cl.mdc.MdcOverride;
import org.slf4j.Marker;

/** This suite of tests is intended to validate the functionality of our wrapper
 * around the {@link EELFLogger}. */
public class AAILoggerAdapterTest {

    private enum InvalidLogCodeEnum {
        BAD_LOGCODE1
    }

    private static final String LOGGER_NAME = "UnitTestLogAdapter";
    private static final String METRICS_LOGGER_NAME = "UnitTestMetricsLogAdapter";
    private static final String AUDIT_LOGGER_NAME = "UnitTestAuditLogAdapter";
    private static final String FIRST_ARG = "First Arg";
    private static final String SECOND_ARG = "Second Arg";

    private TestLogger logger;
    private AaiLoggerAdapter loggerAdapter;
    private AaiLoggerAdapter metricsLoggerAdapter;
    private AaiLoggerAdapter auditLoggerAdapter;

    /**
     * Setup before running tests.
     */
    @Before
    public void setup() {
        logger = new TestLogger();
        loggerAdapter = new AaiLoggerAdapter(logger, LogLineType.ERROR, LOGGER_NAME);
        metricsLoggerAdapter = new AaiLoggerAdapter(logger, LogLineType.METRICS, METRICS_LOGGER_NAME);
        auditLoggerAdapter = new AaiLoggerAdapter(logger, LogLineType.AUDIT, AUDIT_LOGGER_NAME);
    }

    /** This test validates the the {@link AaiLoggerAdapter} is able to deal with
     * log code enums which do not extend the {@link EELFResolvableEnum}
     * placeholder (meaning that the EELF framework would not know how to parse
     * them). */
    @Test
    public void badLogCodeEnumTest() {

        // For each supported log level, validate that if a log code is
        // specified which does not extend EELFResolvableEnum, then a
        // generic 'unparsable log code' statement is generated instead
        // of a correctly parsed log statement.

        loggerAdapter.info(InvalidLogCodeEnum.BAD_LOGCODE1, "arg1");
        assertBadLogCodeMessage(logger.getMessage(), InvalidLogCodeEnum.BAD_LOGCODE1);

        loggerAdapter.debug(InvalidLogCodeEnum.BAD_LOGCODE1, "arg1", "arg2");
        assertBadLogCodeMessage(logger.getMessage(), InvalidLogCodeEnum.BAD_LOGCODE1);

        loggerAdapter.warn(InvalidLogCodeEnum.BAD_LOGCODE1, "arg1", "arg2");
        assertBadLogCodeMessage(logger.getMessage(), InvalidLogCodeEnum.BAD_LOGCODE1);

        loggerAdapter.error(InvalidLogCodeEnum.BAD_LOGCODE1, "arg1", "arg2", "arg3");
        assertBadLogCodeMessage(logger.getMessage(), InvalidLogCodeEnum.BAD_LOGCODE1);

        loggerAdapter.error(InvalidLogCodeEnum.BAD_LOGCODE1, new LogFields(),
            new Throwable("Some exception"), "arg1");
        assertBadLogCodeMessage(logger.getMessage(), InvalidLogCodeEnum.BAD_LOGCODE1);
    }

    /** This test validates that INFO level logs are correctly parsed from the
     * resource bundle. */
    @Test
    public void logStatementInfoParsingTest() {

        // Generate a simple INFO log.
        loggerAdapter.info(UnitTestMsgs.SIMPLE_INFO_LOG, null);

        // Validate that the message was parsed from the bundle resource.
        logger.validateLogMsg("UT0001I This is a simple info log with no arguments.");

        // Generate an INFO log with arguments.
        LogFields fields = new LogFields().setField(LogLine.DefinedFields.TARGET_SVC_NAME, "test");
        loggerAdapter.info(UnitTestMsgs.INFO_LOG_WITH_ARGS, fields, FIRST_ARG, SECOND_ARG);

        // Validate that the message was parsed from the bundle resource.
        logger.validateLogMsg(
            "UT0002I This is an info log with some arguments " + FIRST_ARG + " and " + SECOND_ARG);

        // Validate that log fields are being correctly populated.
        logger.validateLogFields(LogLineType.ERROR, fields);
    }

    /** Validate metrics log. */
    @Test
    public void logStatementMetricsTest() {
        // Generate an INFO log with arguments.
        MdcContext.initialize("xx-yy-zz", "MyService", "MyInstance", "MyPartner", "12.0.0.2");
        metricsLoggerAdapter.info(UnitTestMsgs.INFO_LOG_WITH_ARGS,
            new LogFields().setField(LogLine.DefinedFields.RESPONSE_DESCRIPTION, "Accepted")
                .setField(LogLine.DefinedFields.RESPONSE_CODE, 202),
            "arg1", "arg2");

        // Validate that the log code is correctly encoded in the log statement
        // and that the message was parsed from the bundle resource.
        logger.validateMetricsMsg("UT0002I This is an info log with some arguments arg1 and arg2");
        logger.validateMetricsRespStatus("202", "Accepted");

        // Validate MDC override
        MdcOverride override = new MdcOverride();
        override.addAttribute(MdcContext.MDC_REQUEST_ID, "New-requestID");

        LogFields fields = new LogFields()
            .setField(LogLine.DefinedFields.RESPONSE_DESCRIPTION, "Accepted")
            .setField(LogLine.DefinedFields.RESPONSE_CODE, 202);

        metricsLoggerAdapter.info(UnitTestMsgs.INFO_LOG_WITH_ARGS, fields, override, "arg1", "arg2");
        logger.validateMetricsRequestId("New-requestID");

        // Validate that log fields are being correctly populated.
        logger.validateLogFields(LogLineType.METRICS, fields);
    }

    /** Validate metrics log. */
    @Test
    public void logStatementAuditTest() {
        // Generate an INFO log with arguments.
        MdcContext.initialize("xx-yy-aa", "MyService", "MyInstance", "MyPartner", "12.0.0.8");
        LogFields fields = new LogFields()
            .setField(LogLine.DefinedFields.RESPONSE_DESCRIPTION, "Accepted")
            .setField(LogLine.DefinedFields.RESPONSE_CODE, 202);
        auditLoggerAdapter.info(UnitTestMsgs.INFO_LOG_WITH_ARGS, fields, "arg1", "arg2");

        // Validate that the log code is correctly encoded in the log statement
        // and that the message was parsed from the bundle resource.
        logger.validateAuditMsg("UT0002I This is an info log with some arguments arg1 and arg2");
        logger.validateAuditRespStatus("202", "Accepted");

        // Validate that log fields are being correctly populated.
        logger.validateLogFields(LogLineType.AUDIT, fields);
    }

    /** This test validates that ERROR level logs are correctly parsed from the
     * resource bundle. */
    @Test
    public void logStatementErrorParsingTest() {

        // Generate a simple ERROR log.
        loggerAdapter.error(UnitTestMsgs.SIMPLE_ERROR_LOG, null);

        // Validate that the message was parsed from the bundle resource.
        logger.validateLogMsg("UT0001E This is a simple error log with no arguments.");

        // Generate an ERROR log with arguments.
        loggerAdapter.error(UnitTestMsgs.ERROR_LOG_WITH_ARGS, new LogFields(), FIRST_ARG, SECOND_ARG);

        // Validate that the message was parsed from the bundle resource.
        logger.validateLogMsg(
            "UT0002E This is an error log with some arguments " + FIRST_ARG + " and " + SECOND_ARG);
    }

    /** This test validates that WARNING level logs are correctly parsed from the
     * resource bundle. */
    @Test
    public void logStatementWarnParsingTest() {

        // Generate a simple ERROR log.
        loggerAdapter.warn(UnitTestMsgs.SIMPLE_WARN_LOG, null);

        // Validate that the message was parsed from the bundle resource.
        logger.validateLogMsg("UT0001W This is a simple warning log with no arguments.");

        // Generate a WARNING log with arguments.
        loggerAdapter.warn(UnitTestMsgs.WARN_LOG_WITH_ARGS, FIRST_ARG, SECOND_ARG);

        // Validate that the message was parsed from the bundle resource.
        logger.validateLogMsg(
            "UT0002W This is a warning log with some arguments " + FIRST_ARG + " and " + SECOND_ARG);
    }

    /** This test validates that DEBUG level logs are correctly parsed from the
     * resource bundle. */
    @Test
    public void logStatementDebugParsingTest() {

        String simpleDebugMsg = "My simple debug message";

        // Generate a simple DEBUG log with no error code.
        loggerAdapter.debug(simpleDebugMsg);

        // Validate that the message was parsed from the bundle resource.
        logger.validateLogMsg(simpleDebugMsg);

        // Generate an ERROR log with arguments.
        loggerAdapter.debug(UnitTestMsgs.DEBUG_LOG_WITH_ARGS, FIRST_ARG, SECOND_ARG);

        // Validate that the message was parsed from the bundle resource.
        logger.validateLogMsg(
            "UT0001D This is a debug log with some arguments " + FIRST_ARG + " and " + SECOND_ARG);
    }

    /** This test validates that TRACE level logs are correctly parsed from the
     * resource bundle. */
    @Test
    public void logStatementTraceParsingTest() {

        // Generate a simple TRACE log with no error code.
        loggerAdapter.trace(UnitTestMsgs.SIMPLE_TRACE_LOG, null);

        // Validate that the message was parsed from the bundle resource.
        logger.validateLogMsg("UT0001T This is a simple trace log with no arguments.");
        logger.validateLogLevel(EELFLogger.Level.TRACE);

        // Generate an ERROR log with arguments.
        loggerAdapter.trace(UnitTestMsgs.TRACE_LOG_WITH_ARGS, FIRST_ARG, SECOND_ARG);

        // Validate that the message was parsed from the bundle resource.
        logger.validateLogMsg(
            "UT0002T This is a trace log with some arguments " + FIRST_ARG + " and " + SECOND_ARG);
        logger.validateLogLevel(EELFLogger.Level.TRACE);
    }

    /** This is a convenience method that validates that a generated log message
     * contains the expected values when a log code could not be parsed.
     *
     * @param aLogMessage
     *          - The log message to be validated.
     * @param aLogCode
     *          - The error code that was passed to the logger. */
    private void assertBadLogCodeMessage(String aLogMessage, Enum aLogCode) {

        assertTrue("Expected 'unparseable log code' generic error string",
            logger.getMessage().contains(AaiLoggerAdapter.BAD_ENUM_MSG));
        assertTrue("Expected error string to include log code",
            logger.getMessage().contains(aLogCode.toString()));
    }

    /** This test validates the formatMsg method returns a properly formatted
     * message. */
    @Test
    public void formatMsgTest() {

        String expected1 = "UT0001I This is a simple info log with no arguments.";
        String expected2 = "UT0002I This is an info log with some arguments " + FIRST_ARG + " and " + SECOND_ARG;

        String message1 = loggerAdapter.formatMsg(UnitTestMsgs.SIMPLE_INFO_LOG);
        assertEquals("Invalid formatted msg1", message1, expected1);

        String message2 = loggerAdapter.formatMsg(UnitTestMsgs.INFO_LOG_WITH_ARGS, FIRST_ARG,
            SECOND_ARG);
        assertEquals("Invalid formatted msg2", message2, expected2);
    }

    /** This is an implementation of the {@link EELFLogger} which just caches the
     * last log statement passed to it and provides some convenience methods for
     * validating the contents of the log message.
     * <p>
     * The instance of the {@link AaiLoggerAdapter} that we are testing against
     * will use this implementation instead of a real {@link EELFLogger}. */
    private class TestLogger implements EELFLogger {

        // Some indices to use for extracting specific fields from the
        // log statement.
        private static final int MESSAGE_INDEX = 1;

        private static final int METRICE_TRANS_ID_INDEX = 2;
        private static final int METRICS_RESP_CODE_INDEX = 11;
        private static final int METRICS_RESP_STRING_INDEX = 12;
        private static final int METRICS_MSG_INDEX = 28;

        private static final int AUDIT_RESP_CODE_INDEX = 9;
        private static final int AUDIT_RESP_STRING_INDEX = 10;
        private static final int AUDIT_MSG_INDEX = 25;

        /** Stores the last log statement passed to the logger. */
        private String logMessage;

        private EELFLogger.Level logLevel;

        @Override
        public void warn(String msg) {
            logMessage = msg;
            logLevel = EELFLogger.Level.WARN;
        }

        @Override
        public void warn(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void warn(String msg, Throwable th) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void debug(String msg) {
            logMessage = msg;
            logLevel = EELFLogger.Level.DEBUG;
        }

        @Override
        public void debug(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void debug(String msg, Throwable th) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void trace(String msg) {
            logMessage = msg;
            logLevel = EELFLogger.Level.TRACE;
        }

        @Override
        public void trace(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void trace(String msg, Throwable th) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void info(String msg) {
            logMessage = msg;
            logLevel = EELFLogger.Level.INFO;
        }

        @Override
        public void info(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void error(String msg) {
            logMessage = msg;
            logLevel = EELFLogger.Level.ERROR;
        }

        @Override
        public void error(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void error(String msg, Throwable th) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void log(Level level, String msg, Throwable th, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void auditEvent(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void auditEvent(Level level, String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void metricsEvent(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void metricsEvent(Level level, String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void securityEvent(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void securityEvent(Level level, String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void performanceEvent(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void performanceEvent(Level level, String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void applicationEvent(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void applicationEvent(Level level, String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void serverEvent(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void serverEvent(Level level, String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void policyEvent(String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void policyEvent(Level level, String msg, Object... arguments) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }


        @Override
        public void setLevel(Level level) {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        @Override
        public void disableLogging() {
            throw new UnsupportedOperationException(
                "AAILoggerAdapter is not expected to call into this method.");
        }

        /** Convenience method to retrieve the log string that was produced by the
         * logger.
         *
         * @return - A log string. */
        public String getMessage() {
            return logMessage;
        }

        /** Validates that the parsed log message encoded in the last produced log
         * message matches the supplied expected value.
         *
         * @param aMessage
         *          - The expected log message. */
        public void validateLogMsg(String aMessage) {

            // Tokenize the log string.
            String[] tokens = tokenizeLogString();

            // Verify the log message.
            assertTrue("Unexpected log message in log string", aMessage.equals(tokens[MESSAGE_INDEX]));
        }

        public void validateLogFields(LogLineType logType, LogFields fields) {

            // Tokenize the log string.
            String[] tokens = tokenizeLogString();

            switch (logType) {

                case ERROR:
                    break;

                case METRICS:
                    if (fields.getField(DefinedFields.TARGET_SVC_NAME) != null) {
                        assertTrue(fields.getField(DefinedFields.TARGET_SVC_NAME).equals(tokens[9]));
                    }
                    if (fields.getField(DefinedFields.STATUS_CODE) != null) {
                        assertTrue(fields.getField(DefinedFields.STATUS_CODE).equals(tokens[10]));
                    }
                    if (fields.getField(DefinedFields.RESPONSE_CODE) != null) {
                        assertTrue(fields.getField(DefinedFields.RESPONSE_CODE).equals(tokens[11]));
                    }
                    if (fields.getField(DefinedFields.RESPONSE_DESCRIPTION) != null) {
                        assertTrue(fields.getField(DefinedFields.RESPONSE_DESCRIPTION).equals(tokens[12]));
                    }
                    if (fields.getField(DefinedFields.INSTANCE_UUID) != null) {
                        assertTrue(fields.getField(DefinedFields.INSTANCE_UUID).equals(tokens[13]));
                    }
                    if (fields.getField(DefinedFields.SEVERITY) != null) {
                        assertTrue(fields.getField(DefinedFields.SEVERITY).equals(tokens[15]));
                    }
                    if (fields.getField(DefinedFields.SERVER_IP) != null) {
                        assertTrue(fields.getField(DefinedFields.SERVER_IP).equals(tokens[16]));
                    }
                    if (fields.getField(DefinedFields.CLIENT_IP) != null) {
                        assertTrue(fields.getField(DefinedFields.CLIENT_IP).equals(tokens[19]));
                    }
                    if (fields.getField(DefinedFields.CLASS_NAME) != null) {
                        assertTrue(fields.getField(DefinedFields.CLASS_NAME).equals(tokens[20]));
                    }
                    if (fields.getField(DefinedFields.PROCESS_KEY) != null) {
                        assertTrue(fields.getField(DefinedFields.PROCESS_KEY).equals(tokens[22]));
                    }
                    if (fields.getField(DefinedFields.TARGET_ENTITY) != null) {
                        assertTrue(fields.getField(DefinedFields.TARGET_ENTITY).equals(tokens[23]));
                    }
                    if (fields.getField(DefinedFields.CUSTOM_1) != null) {
                        assertTrue(fields.getField(DefinedFields.CUSTOM_1).equals(tokens[24]));
                    }
                    if (fields.getField(DefinedFields.CUSTOM_2) != null) {
                        assertTrue(fields.getField(DefinedFields.CUSTOM_2).equals(tokens[25]));
                    }
                    if (fields.getField(DefinedFields.CUSTOM_3) != null) {
                        assertTrue(fields.getField(DefinedFields.CUSTOM_3).equals(tokens[26]));
                    }
                    if (fields.getField(DefinedFields.CUSTOM_4) != null) {
                        assertTrue(fields.getField(DefinedFields.CUSTOM_4).equals(tokens[27]));
                    }
                    break;

                case AUDIT:
                    if (fields.getField(DefinedFields.STATUS_CODE) != null) {
                        assertTrue(fields.getField(DefinedFields.STATUS_CODE).equals(tokens[8]));
                    }
                    if (fields.getField(DefinedFields.RESPONSE_CODE) != null) {
                        assertTrue(fields.getField(DefinedFields.RESPONSE_CODE).equals(tokens[9]));
                    }
                    if (fields.getField(DefinedFields.RESPONSE_DESCRIPTION) != null) {
                        assertTrue(fields.getField(DefinedFields.RESPONSE_DESCRIPTION).equals(tokens[10]));
                    }
                    if (fields.getField(DefinedFields.INSTANCE_UUID) != null) {
                        assertTrue(fields.getField(DefinedFields.INSTANCE_UUID).equals(tokens[11]));
                    }
                    if (fields.getField(DefinedFields.SEVERITY) != null) {
                        assertTrue(fields.getField(DefinedFields.SEVERITY).equals(tokens[13]));
                    }
                    if (fields.getField(DefinedFields.SERVER_IP) != null) {
                        assertTrue(fields.getField(DefinedFields.SERVER_IP).equals(tokens[14]));
                    }
                    if (fields.getField(DefinedFields.CLASS_NAME) != null) {
                        assertTrue(fields.getField(DefinedFields.CLASS_NAME).equals(tokens[18]));
                    }
                    if (fields.getField(DefinedFields.PROCESS_KEY) != null) {
                        assertTrue(fields.getField(DefinedFields.PROCESS_KEY).equals(tokens[20]));
                    }
                    if (fields.getField(DefinedFields.CUSTOM_1) != null) {
                        assertTrue(fields.getField(DefinedFields.CUSTOM_1).equals(tokens[21]));
                    }
                    if (fields.getField(DefinedFields.CUSTOM_2) != null) {
                        assertTrue(fields.getField(DefinedFields.CUSTOM_2).equals(tokens[22]));
                    }
                    if (fields.getField(DefinedFields.CUSTOM_3) != null) {
                        assertTrue(fields.getField(DefinedFields.CUSTOM_3).equals(tokens[23]));
                    }
                    if (fields.getField(DefinedFields.CUSTOM_4) != null) {
                        assertTrue(fields.getField(DefinedFields.CUSTOM_4).equals(tokens[24]));
                    }
                    break;
                default:
                    break;
            }
        }

        private void validateMetricsMsg(String value) {
            String[] tokens = tokenizeLogString();
            assertTrue("Unexpected message in log string", value.equals(tokens[METRICS_MSG_INDEX]));
        }

        private void validateMetricsRespStatus(String code, String codeStr) {
            String[] tokens = tokenizeLogString();
            assertTrue("Unexpected resp code in log string",
                code.equals(tokens[METRICS_RESP_CODE_INDEX]));
            assertTrue("Unexpected resp string in log string",
                codeStr.equals(tokens[METRICS_RESP_STRING_INDEX]));
        }

        private void validateMetricsRequestId(String value) {
            String[] tokens = tokenizeLogString();
            assertTrue("Unexpected req id in log string", value.equals(tokens[METRICE_TRANS_ID_INDEX]));
        }

        private void validateAuditMsg(String value) {
            String[] tokens = tokenizeLogString();
            assertTrue("Unexpected message in log string", value.equals(tokens[AUDIT_MSG_INDEX]));
        }

        private void validateAuditRespStatus(String code, String codeStr) {
            String[] tokens = tokenizeLogString();
            assertTrue("Unexpected resp code in log string", code.equals(tokens[AUDIT_RESP_CODE_INDEX]));
            assertTrue("Unexpected resp string in log string",
                codeStr.equals(tokens[AUDIT_RESP_STRING_INDEX]));
        }

        public void validateLogLevel(EELFLogger.Level expectedLevel) {

            assertEquals("Unexpected log level", expectedLevel, logLevel);
        }

        /** This method breaks up the log string into individual tokenized fields,
         * delimited by the '|' character.
         *
         * @return - Array of log message tokens. */
        private String[] tokenizeLogString() {
            System.out.println("\n\n---\n" + logMessage + "\n-------");
            return logMessage.split("\\|");
        }

        @Override
        public String getName() {

            throw new UnsupportedOperationException("Unimplemented method 'getName'");
        }

        @Override
        public void trace(String format, Object arg) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }

        @Override
        public void trace(String format, Object arg1, Object arg2) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }

        @Override
        public boolean isTraceEnabled(Marker marker) {

            throw new UnsupportedOperationException("Unimplemented method 'isTraceEnabled'");
        }

        @Override
        public void trace(Marker marker, String msg) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }

        @Override
        public void trace(Marker marker, String format, Object arg) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }

        @Override
        public void trace(Marker marker, String format, Object arg1, Object arg2) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }

        @Override
        public void trace(Marker marker, String format, Object... argArray) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }

        @Override
        public void trace(Marker marker, String msg, Throwable t) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }

        @Override
        public void debug(String format, Object arg) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public boolean isDebugEnabled(Marker marker) {

            throw new UnsupportedOperationException("Unimplemented method 'isDebugEnabled'");
        }

        @Override
        public void debug(Marker marker, String msg) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public void debug(Marker marker, String format, Object arg) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public void debug(Marker marker, String format, Object arg1, Object arg2) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public void debug(Marker marker, String format, Object... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public void debug(Marker marker, String msg, Throwable t) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public void info(String format, Object arg) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void info(String format, Object arg1, Object arg2) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void info(String msg, Throwable t) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public boolean isInfoEnabled(Marker marker) {

            throw new UnsupportedOperationException("Unimplemented method 'isInfoEnabled'");
        }

        @Override
        public void info(Marker marker, String msg) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void info(Marker marker, String format, Object arg) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void info(Marker marker, String format, Object arg1, Object arg2) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void info(Marker marker, String format, Object... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void info(Marker marker, String msg, Throwable t) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void warn(String format, Object arg) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public boolean isWarnEnabled(Marker marker) {

            throw new UnsupportedOperationException("Unimplemented method 'isWarnEnabled'");
        }

        @Override
        public void warn(Marker marker, String msg) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public void warn(Marker marker, String format, Object arg) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public void warn(Marker marker, String format, Object arg1, Object arg2) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public void warn(Marker marker, String format, Object... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public void warn(Marker marker, String msg, Throwable t) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public void error(String format, Object arg) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public void error(String format, Object arg1, Object arg2) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public boolean isErrorEnabled(Marker marker) {

            throw new UnsupportedOperationException("Unimplemented method 'isErrorEnabled'");
        }

        @Override
        public void error(Marker marker, String msg) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public void error(Marker marker, String format, Object arg) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public void error(Marker marker, String format, Object arg1, Object arg2) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public void error(Marker marker, String format, Object... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public void error(Marker marker, String msg, Throwable t) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public void warn(Locale locale, EELFResolvableResourceEnum resource, Throwable th, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public void info(Locale locale, EELFResolvableResourceEnum resource, Throwable th, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void debug(Locale locale, EELFResolvableResourceEnum resource, Throwable th, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public void error(Locale locale, EELFResolvableResourceEnum resource, Throwable th, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public void trace(Locale locale, EELFResolvableResourceEnum resource, Throwable th, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }

        @Override
        public void warn(Locale locale, EELFResolvableResourceEnum resource, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public void info(Locale locale, EELFResolvableResourceEnum resource, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void debug(Locale locale, EELFResolvableResourceEnum resource, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public void error(Locale locale, EELFResolvableResourceEnum resource, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public void trace(Locale locale, EELFResolvableResourceEnum resource, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }

        @Override
        public void warn(EELFResolvableResourceEnum resource, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public void info(EELFResolvableResourceEnum resource, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void debug(EELFResolvableResourceEnum resource, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public void error(EELFResolvableResourceEnum resource, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public void trace(EELFResolvableResourceEnum resource, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }

        @Override
        public void warn(EELFResolvableResourceEnum resource, Throwable th, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'warn'");
        }

        @Override
        public void info(EELFResolvableResourceEnum resource, Throwable th, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'info'");
        }

        @Override
        public void debug(EELFResolvableResourceEnum resource, Throwable th, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'debug'");
        }

        @Override
        public void error(EELFResolvableResourceEnum resource, Throwable th, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'error'");
        }

        @Override
        public void trace(EELFResolvableResourceEnum resource, Throwable th, String... arguments) {

            throw new UnsupportedOperationException("Unimplemented method 'trace'");
        }
    }
}
