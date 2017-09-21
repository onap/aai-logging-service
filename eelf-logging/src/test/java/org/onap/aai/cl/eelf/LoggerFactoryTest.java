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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.aai.cl.api.Logger;

/** This suite of tests is intended to exercise the basic functionality of the
 * {@link LoggerFactory}. */
public class LoggerFactoryTest {

    /** This test validates that the {@link LoggerFactory} is a singleton. */
    @Test
    public void loggerFactorySingletonTest() {

        // Call getInstance() on the LoggerFactory twice.
        LoggerFactory loggerFactory1 = LoggerFactory.getInstance();
        LoggerFactory loggerFactory2 = LoggerFactory.getInstance();

        // Validate that what we got was two references to the same instance
        // of the LoggerFactory (ie: yes, it really is a singleton).
        assertTrue("Expected references to the SAME LoggerFactory instance",
            loggerFactory1 == loggerFactory2);
    }

    /** This test validates that we can request {@link Logger} instances from the
     * {@link LoggerFactory} by specifying either a name to use as the logger's
     * identifier, or a class. */
    @Test
    public void getLoggerTest() {

        // Get an instance of the LoggerFactory.
        LoggerFactory loggerFactory = LoggerFactory.getInstance();

        // Request a Logger instance by specifying the name we want for
        // the logger.
        Logger myLoggerByName = loggerFactory.getLogger("UnitTestLogger");

        // Validate that we got a logger back.
        assertNotNull(myLoggerByName);

        // Now, request a Logger instance by specifying a class as our logger
        // identifier.
        Logger myLoggerByClass = loggerFactory.getLogger(LoggerFactoryTest.class);

        // Validate that we got a logger back.
        assertNotNull(myLoggerByClass);
    }

    /** This test validates that the {@link LoggerFactory} will only produce a
     * single unique {@link Logger} instance for each requested log name.
     * <p>
     * In other words, two requests for a {@link Logger} with the same name will
     * produce two references to the same instance, not two unique {@link Logger}
     * instances. */
    @Test
    public void getLoggerSingleInstanceForNameTest() {

        // Get an instance of the LoggerFactory.
        LoggerFactory loggerFactory = LoggerFactory.getInstance();

        // Request a Logger instance by specifying the name we want for
        // the logger.
        Logger myFirstLogger = loggerFactory.getLogger("UnitTestLogger");

        // Request another Logger instance, but specify the same name for
        // the logger's identifier.
        Logger mySecondLogger = loggerFactory.getLogger("UnitTestLogger");

        // Validate, that because there was already a Logger instance with the
        // specified name, we just get back a reference to that instance.
        assertTrue("Expected references to the same logger instance", myFirstLogger == mySecondLogger);

        // Now, make a third logger request, but specify a different name.
        Logger myThirdLogger = loggerFactory.getLogger("AnotherUnitTestLogger");

        // Validate that, in this case, we really do get a new instance.
        assertFalse("Expected a unique logger instance", myFirstLogger == myThirdLogger);
        assertFalse("Expected a unique logger instance", mySecondLogger == myThirdLogger);
    }

    /** This test validates that the {@link LoggerFactory} will only produce a
     * single unique {@link Logger} instance for each requested class.
     * <p>
     * In other words, two requests for a {@link Logger} with the same class will
     * produce two references to the same instance, not two unique {@link Logger}
     * instances. */
    @Test
    public void getLoggerSingleInstanceForClassTest() {

        // Get an instance of the LoggerFactory.
        LoggerFactory loggerFactory = LoggerFactory.getInstance();

        // Request a Logger instance by specifying the class to use as the
        // logger's identifier.
        Logger myFirstLogger = loggerFactory.getLogger(LoggerFactoryTest.class);

        // Request another Logger instance, but specify the same class for
        // the logger's identifier.
        Logger mySecondLogger = loggerFactory.getLogger(LoggerFactoryTest.class);

        // Validate, that because there was already a Logger instance with the
        // specified class, we just get back a reference to that instance.
        assertTrue("Expected references to the same logger instance", myFirstLogger == mySecondLogger);

        // Now, make a third logger request, but specify a different class.
        Logger myThirdLogger = loggerFactory.getLogger(LoggerFactory.class);

        // Validate that, in this case, we really do get a new instance.
        assertFalse("Expected a unique logger instance", myFirstLogger == myThirdLogger);
        assertFalse("Expected a unique logger instance", mySecondLogger == myThirdLogger);
    }
}
