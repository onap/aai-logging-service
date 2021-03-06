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
package org.onap.aai.cl.mdc;

import java.util.HashMap;
import java.util.Map;

/**
 * This class stores a map of MDC context attribute/values which can be used to
 * override the actual MDC context.
 */
public class MdcOverride {
  private Map<String, String> overrides = new HashMap<String, String>();

  public void addAttribute(String attr, String val) {
    overrides.put(attr, val);
  }

  public String getAttributeValue(String attr) {
    return overrides.get(attr);
  }

  public boolean hasOverride(String attr) {
    return overrides.containsKey(attr);
  }
}
