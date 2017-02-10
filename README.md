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

# Common Logging Library

The common logging library provides a single library that can be used by any microservice to produce logs.  
An abstraction layer is provided which exposes a public API to the microservices and shields them from the underlying implementation.

The current version of the library is backed by an implementation which wraps the EELF Logging framework, however, the intent is that other logging implementations could be substituted in the future without impacting the public API used by the microservice clients.

---

## Getting Started

### Making The Library Available To Your Microservice
In order to make the logging library available to your microservice, include the following dependency in your service's pom.xml:


    <!-- Common logging framework -->		
    <dependency>
        <groupId>org.openecomp.aai.logging-service</groupId>
        <artifactId>common-service</artifactId>
        <version>1.0.0</version>
    </dependency>


### Getting a Logger
There are three classes of _logger_ provided by the logging library:
* Standard logger - Used to generate most typical log statements.
* Audit logger - Used specifically to generate audit logs.
* Metrics logger - Used specifically to generate metrics logs.

In order to generate log statements, your code needs to acquire an instance of whichever *Logger* type that you need.  This is accomplished by requesting a *Logger* from the *LoggerFactory*

    Logger logger        = LoggerFactory.getInstance().getLogger( {my logger name} ); 
    Logger auditLogger   = LoggerFactory.getInstance().getAuditLogger( {my logger name} );
    Logger metricsLogger = LoggerFactory.getInstance().getMetricsLogger( {my logger name} );
    
where *{my logger name}* should be replaced with whatever name you want assigned to your logger instance.


### Log Statement Templates
The current version of the logging library is backed by the EELF Logging Framework.  This provides the ability to create log statement templates with arguments to be filled in at runtime.

**IMPORTANT:** When creating an enumerated class for message keys using the Common Logging Library, the class must implement `LogMessageEnum` and not `EELFResolvableErrorEnum` as detailed in the wiki link.

### Generating Basic Log Statements
Log statements can be generated at the following log levels:  **DEBUG**, **TRACE** **INFO**, **WARNING** and **ERROR** as follows:

    // Note, see the next section for a description of how to set the 'fields' argument.
    
    // Debug Level Logs. 
    logger.debug("Some debug message")
    logger.debug(MyMsgEnum.A_DEBUG_MSG, fields, "arg1", "arg2"...) 
	
    // Trace Level Logs.
    logger.trace(MyMsgEnum.A_TRACE_MSG, fields, "arg1", "arg2"...)
    
    // Info Level Logs.
    logger.info(MyMsgEnum.AN_INFO_MSG, fields, "arg1", "arg2"...)
    logger.info(MyMsgEnum.AN_INFO_MSG, fields, "a status string", a_status_code, "arg1", "arg2"...)
    logger.info(MyMsgEnum.AN_INFO_MSG, fields, "a status string", a_status_code, an_MDC_override, "arg1"...)
    
    // Warning Level Logs.    
    logger.warn(MyMsgEnum.A_WARNING_MSG, "arg1", "arg2"...)
    
    // Error Level Logs.
    logger.error(MyMsgEnum.AN_ERROR_MSG, "arg1", "arg2"...)

### Standardized Log Fields
There are a number of standard fields which the client may provide values for.  These fields will be automatically populated with the supplied values, in fixed positions within the generated log string.  

The client may pass values for these fields by populating a _LogFields_ object with _field name_/_value_ pairs as illustrated in the following example:

    LogFields fields = new LogFields()
                          .setField(LogLine.DefinedFields.CLIENT_IP, "1.2.3.4")
                          .setField(LogLine.DefinedFields.CLASS_NAME, "somename");
                           
In the logging examples in the previous section, the _fields_ argument, which we glossed over, would be set it this manner.  

The following fields are currently defined as settable by the client:
                         
        STATUS_CODE,           // High level success or failure of the request  (COMPLETE or ERROR)
        RESPONSE_CODE,         // Application-specific error code
        RESPONSE_DESCRIPTION,  // Human readable description of the application specific response code
        INSTANCE_UUID,         // universally unique identifier used to differentiate between multiple 
                               // instances of the same (named), log writing component
        SEVERITY,              // 0, 1, 2, 3  
        SERVER_IP,             // The logging component host server’s IP address  
        CLIENT_IP,             // Requesting remote client application’s IP address
        CLASS_NAME,            // This is the name of the class that has caused the log record to be created
        PROCESS_KEY,
        TARGET_SVC_NAME,       // External API/operation activities invoked on TargetEntity
        TARGET_ENTITY,         // Component or subcomponent invoked for this operation.
        CUSTOM_1,              // For whatever custom use the client determines.
        CUSTOM_2,              // For whatever custom use the client determines.
        CUSTOM_3,              // For whatever custom use the client determines.
        CUSTOM_4;              // For whatever custom use the client determines.
                           
### The Mapped Diagnostic Context
The _Mapped Diagnostic Context_ or MDC is a tool to help distinguish interleaved log output from different sources.  It lets the application thread place information in a diagnostic context that can later be retrieved by the logging components. 

#### Initializing the _MDC Context_
Application threads may initialize their MDC Context according to the following pattern:

	MDCContext.initialize({transaction id}, {service name}, {service instance}, {partner name} {client address});
	
Once initialized, the values stored in the _MDC Context_ will be used by the logger to auto populate the relevant fields in the standard log statement.

#### Supported Fields
The logging library makes use of a number of 'known' fields in the _MDC Context_.  These field names are exposed as static variables by the _MDCContext_ class:

    MDCContext.MDC_REQUEST_ID
    MDCContext.MDC_SERVER_FQDN
    MDCContext.MDC_SERVICE_NAME
    MDCContext.MDC_PARTNER_NAME
    MDCContext.MDC_START_TIME
    MDCContext.MDC_REMOTE_HOST
    MDCContext.MDC_SERVICE_INSTANCE_ID
    MDCContext.MDC_CLIENT_ADDRESS

If values are assigned to any of these fields, then they will be automatically inserted into the appropriate fields in the standard log statement generated by the logger, although it is not *required* that they be set.
	 
#### Overriding the Contents of the _MDC Context_
In certain situations, it is useful for the application thread to override or insert a value into one of the _MDC Context_ fields on a one-time basis when invoking the _logger_ (a good example is passing time stamp data to a metrics logger).

The following example illustrates how to achieve this:

    public void doSomeOperation() {
    
        // Instantiate a new MDCOverride object.
        MDCOverride override = new MDCOverride();
        
        // Grab the current time to use later for metrics calculations.
        long startTimeInMs = System.currentTimeMillis();
        
        // ...and add it as an attribute to our MDC Override 
        // object.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");   
        override.addAttribute(MDCContext.MDC_START_TIME, formatter.format(startTimeInMs));
                
        // do a bunch of really important stuff...
        
        // Finally, invoke a metrics logger, passing in our instance of the
        // MDCOverride object.
        metricsLogger.info(MyMsgEnum.A_LOG_CODE, 
                           a_status_code,
                           override,
                           an_argument,
                           another_argument); 
    }
