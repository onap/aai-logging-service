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

import org.onap.aai.cl.api.LogLine;


/** This class is used to help standardize how log lines are written and provide
 * profiling info. */
public class ErrorLogLine extends LogLine {

  /** (non-Javadoc)
   * @see org.onap.aai.cl.api.LogLine#getFormattedLine()
   */
  public String getFormattedLine() {

    // The error logger fields should be defined in logback.xml using the following pattern:
    //   %d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX}|%mdc{RequestId}|%thread|<AppName>|%mdc{PartnerName}|%logger||%.-5level|%msg%n"     
    return logCode + "|" +                                    // 9  error code
           message + "|" +                                    // 10 log message
           "";                                                // 11 extra details

  }
}
