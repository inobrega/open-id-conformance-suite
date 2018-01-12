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

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;

import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.Condition;
import io.fintechlabs.testframework.condition.PostEnvironment;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.condition.Condition.ConditionResult;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;

/**
 * @author jricher
 *
 */
public class GenerateJWKsFromClientSecret extends AbstractCondition {

	/**
	 * @param testId
	 * @param log
	 * @param optional
	 */
	public GenerateJWKsFromClientSecret(String testId, TestInstanceEventLog log, ConditionResult conditionResultOnFailure, String... requirements) {
		super(testId, log, conditionResultOnFailure, requirements);
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.condition.Condition#evaluate(io.fintechlabs.testframework.testmodule.Environment)
	 */
	@Override
	@PreEnvironment(strings = "client_id", required = "client")
	@PostEnvironment(required = "jwks")
	public Environment evaluate(Environment env) {
		String clientId = env.getString("client_id");
		String clientSecret = env.getString("client", "client_secret");
		
		if (Strings.isNullOrEmpty(clientSecret)) {
			return error("Couldn't find client secret");
		}
		
		// generate a JWK Set for the client's secret
		JWK jwk = new OctetSequenceKey.Builder(clientSecret.getBytes())
				.algorithm(JWSAlgorithm.HS256) // TODO make this configurable
				.keyID(clientId) // TODO make this configurable
				.keyUse(KeyUse.SIGNATURE)
				.build();
		
		JWKSet jwks = new JWKSet(jwk);
		
		JsonObject reparsed = new JsonParser().parse(jwks.toJSONObject(false).toJSONString()).getAsJsonObject();
		
		env.put("jwks", reparsed);
		
		logSuccess("Generated JWK Set from symmetric key", args("jwks", reparsed));
		
		return env;
		
	}

}