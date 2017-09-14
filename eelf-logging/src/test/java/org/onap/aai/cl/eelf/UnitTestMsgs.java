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

package org.onap.aai.cl.eelf;

import org.onap.aai.cl.eelf.LogMessageEnum;

import com.att.eelf.i18n.EELFResourceManager;

public enum UnitTestMsgs implements LogMessageEnum {

	SIMPLE_INFO_LOG,
	INFO_LOG_WITH_ARGS,
	SIMPLE_ERROR_LOG,
	ERROR_LOG_WITH_ARGS,
	SIMPLE_WARN_LOG,
	WARN_LOG_WITH_ARGS,
	SIMPLE_TRACE_LOG,
	TRACE_LOG_WITH_ARGS,
	DEBUG_LOG_WITH_ARGS;

  /** Static initializer to ensure the resource bundles for this class are
   * loaded... */
  static {
    EELFResourceManager.loadMessageBundle("logging/UnitTestMsgs");
  }
}
