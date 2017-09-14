/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.cl.eelf;

import org.onap.aai.cl.api.LogLine;
import org.onap.aai.cl.mdc.MdcContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** This class is used to help standardize how log lines are written and provide
 * profiling info. */
public class AuditLogLine extends LogLine {

  /** (non-Javadoc)
   *  @see org.onap.aai.cl.api.LogLine#getFormattedLine()
   */
  public String getFormattedLine() {

    // calculate start/end/elapsed times
    Date currentDateTime = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    String startTimeString = getMdcValue(MdcContext.MDC_START_TIME);
    String endTimeString = formatter.format(currentDateTime);
    long elapsedTime = 0;
    Date startDateTime;
    try {
      startDateTime = formatter.parse(startTimeString);
      elapsedTime = currentDateTime.getTime() - startDateTime.getTime();
    } catch (ParseException e) {
      // Leave an elapsed time of 0 if the start time was not properly formatted
    }
    String elapsedTimeString = Long.toString(elapsedTime);

    return startTimeString + "|" +                              // 1 start time
        endTimeString + "|" +                                   // 2 end time
        getMdcValue(MdcContext.MDC_REQUEST_ID) + "|" +          // 3 transaction id
        getMdcValue(MdcContext.MDC_SERVICE_INSTANCE_ID) + "|" + // 4 service instance
        Thread.currentThread().getName() + "|" +                // 5 thread id
        getMdcValue(MdcContext.MDC_SERVER_FQDN) + "|" +         // 6 physical/virtual server name
        getMdcValue(MdcContext.MDC_SERVICE_NAME) + "|" +        // 7 service name
        getMdcValue(MdcContext.MDC_PARTNER_NAME) + "|" +        // 8 partner name
        fieldValue(DefinedFields.STATUS_CODE) + "|" +           // 9 status code
        fieldValue(DefinedFields.RESPONSE_CODE) + "|" +         // 10 response code
        fieldValue(DefinedFields.RESPONSE_DESCRIPTION) + "|" +  // 11 response description
        fieldValue(DefinedFields.INSTANCE_UUID) + "|" +         // 12 instance UUID
        level + "|" +                                           // 13 log level
        fieldValue(DefinedFields.SEVERITY) + "|" +              // 14 log severity
        fieldValue(DefinedFields.SERVER_IP) + "|" +             // 15 server ip
        elapsedTimeString + "|" +                               // 16 elapsed time
        getMdcValue(MdcContext.MDC_SERVER_FQDN) + "|" +         // 17 server name
        getMdcValue(MdcContext.MDC_CLIENT_ADDRESS) + "|" +      // 18 client ip address
        fieldValue(DefinedFields.CLASS_NAME) + "|" +            // 19 class name
        "" + "|" +                                              // 20 deprecated
        fieldValue(DefinedFields.PROCESS_KEY) + "|" +           // 21 process key
        fieldValue(DefinedFields.CUSTOM_1) + "|" +              // 22 custom 1
        fieldValue(DefinedFields.CUSTOM_2) + "|" +              // 23 custom 2
        fieldValue(DefinedFields.CUSTOM_3) + "|" +              // 24 custom 3
        fieldValue(DefinedFields.CUSTOM_4) + "|" +              // 25 custom 4
        message;                                                // 26 details
  }
}
