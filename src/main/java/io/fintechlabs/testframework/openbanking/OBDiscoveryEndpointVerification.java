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

// author: ddrysdale

package io.fintechlabs.testframework.openbanking;

import com.google.gson.JsonObject;

import io.fintechlabs.testframework.condition.Condition.ConditionResult;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointAuthorizationEndpoint;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointClaimsParameterSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointClaimsSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointDiscoveryUrl;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointGrantTypesSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointIdTokenSigningAlgValuesSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointJwksUri;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointRegistrationEndpoint;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointRequestObjectSigningAlgValuesSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointRequestParameterSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointRequestUriParameterSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointRequireRequestUriRegistration;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointResponseTypesSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointScopesSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointTokenEndpoint;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointTokenEndpointAuthMethodsSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointTokenEndpointAuthSigningAlgValuesSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointUserinfoSigningAlgValuesSupported;
import io.fintechlabs.testframework.condition.client.GetDynamicServerConfiguration;
import io.fintechlabs.testframework.testmodule.AbstractTestModule;
import io.fintechlabs.testframework.testmodule.PublishTestModule;

@PublishTestModule(
		testName = "ob-discovery-end-point-verification",
		displayName = "OB: Discovery Endpoint Verification",
		profile = "OB",
		configurationFields = {
			"server.discoveryUrl",
		}
)

public class OBDiscoveryEndpointVerification extends AbstractTestModule {

	@Override
	public void start() {

		setStatus(Status.RUNNING);

		callAndContinueOnFailure(CheckDiscEndpointDiscoveryUrl.class,ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointClaimsParameterSupported.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointClaimsSupported.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointGrantTypesSupported.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointResponseTypesSupported.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointRequestParameterSupported.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointRequestUriParameterSupported .class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointRequireRequestUriRegistration.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointScopesSupported.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointIdTokenSigningAlgValuesSupported.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointRequestObjectSigningAlgValuesSupported.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointTokenEndpointAuthMethodsSupported.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointTokenEndpointAuthSigningAlgValuesSupported .class, ConditionResult.FAILURE);

		call(condition(CheckDiscEndpointUserinfoSigningAlgValuesSupported.class)
			.skipIfElementMissing("server", "userinfo_endpoint")
			.onFail(ConditionResult.FAILURE)
			.onSkip(ConditionResult.INFO)
			.dontStopOnFailure()
			);

		callAndContinueOnFailure(CheckDiscEndpointTokenEndpoint.class, ConditionResult.FAILURE);
		callAndContinueOnFailure(CheckDiscEndpointAuthorizationEndpoint.class, ConditionResult.FAILURE);

		call(condition(CheckDiscEndpointRegistrationEndpoint.class)
			.skipIfElementMissing("server", "registration_endpoint")
			.onFail(ConditionResult.FAILURE)
			.onSkip(ConditionResult.INFO)
			.dontStopOnFailure()
			);

		callAndContinueOnFailure(CheckDiscEndpointJwksUri.class, ConditionResult.FAILURE);

		fireTestFinished();

	}

	@Override
	public void configure(JsonObject config, String baseUrl) {
		env.putString("base_url", baseUrl);
		env.putObject("config", config);
		callAndStopOnFailure(GetDynamicServerConfiguration.class);
		setStatus(Status.CONFIGURED);
		fireSetupDone();
	}

}
