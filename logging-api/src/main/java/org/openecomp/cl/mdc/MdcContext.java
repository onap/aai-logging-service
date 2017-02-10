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

package org.openecomp.cl.mdc;

import org.slf4j.MDC;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class manages the MDC (mapped diagnostic context). Calling the init
 * method when a new event is processed will save thread-specific context
 * information which will be used when generating logs.
 */
public final class MdcContext {
  public static String MDC_REQUEST_ID = "RequestId";
  public static String MDC_SERVER_FQDN = "ServerFQDN";
  public static String MDC_SERVICE_NAME = "ServiceName";
  public static String MDC_PARTNER_NAME = "PartnerName";
  public static String MDC_START_TIME = "StartTime";
  public static String MDC_REMOTE_HOST = "RemoteHost";
  public static String MDC_SERVICE_INSTANCE_ID = "ServiceInstanceId";
  public static String MDC_CLIENT_ADDRESS = "ClientAddress";

  /**
   * Initializes the fields of the Mapped Diagnostic Context.
   * 
   * @param transId          - Unique transaction identifier.
   * @param serviceName      - The name of the service generating the diagnostic.
   * @param serviceInstance  - Unique identifier of the specific instance 
   *                           generating the diagnostic.
   * @param partnerName      - Name of the entity initiating the transction to 
   *                           be logged
   * @param clientAddress    - IP address of the transaction client.
   */
  public static void initialize(String transId, 
                                String serviceName, 
                                String serviceInstance,
                                String partnerName, 
                                String clientAddress) {
    MDC.clear();
    MDC.put(MDC_REQUEST_ID, transId);
    MDC.put(MDC_SERVICE_NAME, serviceName);
    MDC.put(MDC_SERVICE_INSTANCE_ID, serviceInstance);
    MDC.put(MDC_PARTNER_NAME, partnerName);
    MDC.put(MDC_CLIENT_ADDRESS, clientAddress);
    MDC.put(MDC_START_TIME,
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));

    try {
      MDC.put(MDC_SERVER_FQDN, InetAddress.getLocalHost().getCanonicalHostName());
    } catch (Exception e) {
      // If, for some reason we are unable to get the canonical host name, we
      // just want to leave the field unpopulated.  There is not much value
      // in doing anything else with an exception at this point.
    }

  }
}
