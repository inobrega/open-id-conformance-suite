package net.openid.conformance.condition.as;

import com.google.common.base.Strings;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class EnsureNoClientAssertionSentToTokenEndpoint extends AbstractCondition {

	@Override
	@PreEnvironment(required = "token_endpoint_request")
	public Environment evaluate(Environment env) {

		String clientAssertionString = env.getString("token_endpoint_request", "body_form_params.client_assertion");
		if (Strings.isNullOrEmpty(clientAssertionString)) {
			logSuccess("Client did not send a client_assertion to token endpoint");
			return env;
		} else {
			throw error("client_assertion should not exist in request parameters, it is only required when the test is set to use private_key_jwt client authentication.");
		}
	}
}
