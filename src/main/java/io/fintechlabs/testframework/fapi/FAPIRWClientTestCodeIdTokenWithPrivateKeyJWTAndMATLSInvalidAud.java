package io.fintechlabs.testframework.fapi;

import io.fintechlabs.testframework.condition.ConditionError;
import io.fintechlabs.testframework.condition.as.AddInvalidAudValueToIdToken;
import io.fintechlabs.testframework.openbanking.AbstractFAPIOBClientPrivateKeyExpectNothingAfterAuthorisationEndpoint;
import io.fintechlabs.testframework.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-rw-client-test-code-id-token-with-private-key-jwt-and-matls-invalid-aud",
	displayName = "FAPI-RW: client test - invalid aud in id_token from authorization_endpoint should be rejected (code id_token with private_key_jwt and MATLS)",
	profile = "FAPI-RW",
	configurationFields = {
		"server.jwks",
		"client.client_id",
		"client.scope",
		"client.redirect_uri",
		"client.certificate",
		"client.jwks",
	}
)

public class FAPIRWClientTestCodeIdTokenWithPrivateKeyJWTAndMATLSInvalidAud extends AbstractFAPIRWClientPrivateKeyExpectNothingAfterAuthorisationEndpoint {

	@Override
	protected void addCustomValuesToIdToken() {

		callAndStopOnFailure(AddInvalidAudValueToIdToken.class, "OIDCC-3.1.3.7.3");
	}

	@Override
	protected Object authorizationCodeGrantType(String requestId) {

		throw new ConditionError(getId(), "Client has incorrectly called token_endpoint after receiving an id_token with an invalid aud value from the authorization_endpoint.");

	}

}