package io.fintechlabs.testframework.fapi;

import io.fintechlabs.testframework.condition.ConditionError;
import io.fintechlabs.testframework.condition.as.AddInvalidNonceValueToIdToken;
import io.fintechlabs.testframework.openbanking.AbstractFAPIOBClientPrivateKeyExpectNothingAfterAuthorisationEndpoint;
import io.fintechlabs.testframework.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-rw-id2-client-test-with-private-key-jwt-and-mtls-holder-of-key-invalid-nonce",
	displayName = "FAPI-RW-ID2: client test - invalid nonce in id_token from authorization_endpoint should be rejected (with private_key_jwt and mtls holder of key)",
	profile = "FAPI-RW-ID2",
	configurationFields = {
		"server.jwks",
		"client.client_id",
		"client.scope",
		"client.redirect_uri",
		"client.certificate",
		"client.jwks",
	}
)

public class FAPIRWClientTestCodeIdTokenWithPrivateKeyJWTAndMATLSInvalidNonce extends AbstractFAPIRWClientPrivateKeyExpectNothingAfterAuthorisationEndpoint {

	@Override
	protected void addCustomValuesToIdToken() {

		callAndStopOnFailure(AddInvalidNonceValueToIdToken.class, "OIDCC-3.1.3.7-11");
	}

	@Override
	protected Object authorizationCodeGrantType(String requestId) {

		throw new ConditionError(getId(), "Client has incorrectly called token_endpoint after receiving an id_token with an invalid nonce value from the authorization_endpoint.");

	}

}
