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

package io.fintechlabs.testframework.condition.client;

import com.google.gson.JsonObject;
import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;

import java.time.Instant;

/**
 * @author jheenan
 */
public class AddIatExpToRequestObject extends AbstractCondition {

	public AddIatExpToRequestObject(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.condition.Condition#evaluate(io.fintechlabs.testframework.testmodule.Environment)
	 */
	@Override
	@PreEnvironment(required = { "request_object_claims"})
	public Environment evaluate(Environment env) {

		JsonObject requestObjectClaims = env.getObject("request_object_claims");

		Instant iat = Instant.now();
		Instant exp = iat.plusSeconds(5 * 60);

		requestObjectClaims.addProperty("iat", iat.getEpochSecond());
		requestObjectClaims.addProperty("exp", exp.getEpochSecond());

		env.putObject("request_object_claims", requestObjectClaims);

		logSuccess("Added exp & iat to request object claims", args(
			"iat", requestObjectClaims.getAsJsonPrimitive("iat"),
			"exp", requestObjectClaims.getAsJsonPrimitive("exp"))
		);

		return env;
	}
}
