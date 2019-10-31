package net.openid.conformance.condition.client;

import com.google.gson.JsonElement;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;
import net.openid.conformance.testmodule.OIDFJSON;

public class FAPIRWCheckTLSClientCertificateBoundAccessTokens extends AbstractCondition {

	@Override
	@PreEnvironment(required = "server")
	public Environment evaluate(Environment env) {

		JsonElement element = env.getElementFromObject("server", "tls_client_certificate_bound_access_tokens");
		if (element == null || element.isJsonObject()) {
			throw error("tls_client_certificate_bound_access_tokens in server was missing");
		}

		if (!element.getAsJsonPrimitive().isBoolean()) {
			throw error("Type of tls_client_certificate_bound_access_tokens must be boolean.");
		}

		if (!OIDFJSON.getBoolean(element)) {
			throw error("tls_client_certificate_bound_access_tokens must be 'true'", args("actual", OIDFJSON.getBoolean(element)));
		}

		logSuccess("tls_client_certificate_bound_access_tokens was 'true'");

		return env;
	}
}