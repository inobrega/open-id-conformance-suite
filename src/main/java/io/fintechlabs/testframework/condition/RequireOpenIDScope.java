/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package io.fintechlabs.testframework.condition;

import java.util.List;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;

import io.fintechlabs.testframework.logging.EventLog;
import io.fintechlabs.testframework.testmodule.Environment;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * @author jricher
 *
 */
public class RequireOpenIDScope extends AbstractCondition {

	/**
	 * @param testId
	 * @param log
	 * @param optional
	 */
	public RequireOpenIDScope(String testId, EventLog log, boolean optional) {
		super(testId, log, optional);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.condition.Condition#evaluate(io.fintechlabs.testframework.testmodule.Environment)
	 */
	@Override
	public Environment evaluate(Environment env) {
		String scope = env.getString("scope");
		
		List<String> scopes = Lists.newArrayList(Splitter.on(" ").split(scope));
		
		if (!scopes.contains("openid")) {
			return error("Couldn't find openid scope");
		} else {
			logSuccess("Found openid scope in scopes list", args("scopes", scopes));
			return env;
		}
	}

}