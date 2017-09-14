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
package org.onap.aai.cl.api;

import java.util.HashMap;
import java.util.Map;

public class LogFields {

  /** Map of field names to values. */
  private Map<Integer, String> fields = new HashMap<Integer, String>();

  /**
   * Retrieve the contents of the specified field entry.
   * 
   * @param field
   *          - The field to retrieve the value for.
   * 
   * @return - The value associated with the specified field, or null if there
   *         is no such entry.
   */
  public String getField(Enum field) {
    return fields.get(field.ordinal());
  }

  /**
   * Assigns a value to a specific field.
   * 
   * @param field
   *          - The field to assign a value to.
   * @param value
   *          - The value to assign to the field.
   * 
   * @return - The {@link LogFields} object (this is useful for parameter
   *         chaining.
   */
  public LogFields setField(Enum field, String value) {
    fields.put(field.ordinal(), value);
    return this;
  }

  /**
   * Assigns a value to a specific field.
   * 
     * @param field - The field to assign a value to.
     * @param value - The value to assign to the field.
   * 
   * @return - The {@link LogFields} object (this is useful for parameter
   *         chaining.
   */
  public LogFields setField(Enum field, int value) {
    fields.put(field.ordinal(), String.valueOf(value));
    return this;
  }

  /**
   * Determines whether or not a value has been assigned to a particular field.
   * 
   * @param field - The field to be checked.
   * 
   * @return - true if an entry exists for the specified field, false otherwise.
   */
  public boolean fieldIsSet(Enum field) {
    return fields.containsKey(field.ordinal()) && (fields.get(field.ordinal()) != null);
  }
}
