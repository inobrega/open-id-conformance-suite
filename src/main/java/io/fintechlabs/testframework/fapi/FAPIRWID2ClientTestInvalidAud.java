package io.fintechlabs.testframework.fapi;

import io.fintechlabs.testframework.condition.ConditionError;
import io.fintechlabs.testframework.condition.as.AddInvalidAudValueToIdToken;
import io.fintechlabs.testframework.testmodule.PublishTestModule;
import io.fintechlabs.testframework.testmodule.Variant;

@PublishTestModule(
	testName = "fapi-rw-id2-client-test-invalid-aud",
	displayName = "FAPI-RW-ID2: client test - invalid aud in id_token from authorization_endpoint, should be rejected",
	summary = "This test should end with the client displaying an error message that the aud value in the id_token does not match the client id",
	profile = "FAPI-RW-ID2",
	configurationFields = {
		"server.jwks",
		"client.client_id",
		"client.scope",
		"client.redirect_uri",
		"client.certificate",
		"client.jwks",
	},
	notApplicableForVariants = {
		FAPIRWID2ClientTest.variant_openbankinguk_mtls,
		FAPIRWID2ClientTest.variant_openbankinguk_privatekeyjwt
	}
)

public class FAPIRWID2ClientTestInvalidAud extends AbstractFAPIRWID2ClientExpectNothingAfterAuthorisationEndpoint {

	@Variant(name = variant_mtls)
	public void setupMTLS() {
		super.setupMTLS();
	}

	@Variant(name = variant_privatekeyjwt)
	public void setupPrivateKeyJwt() {
		super.setupPrivateKeyJwt();
	}

	@Override
	protected void addCustomValuesToIdToken() {

		callAndStopOnFailure(AddInvalidAudValueToIdToken.class, "OIDCC-3.1.3.7-3");
	}

	@Override
	protected Object authorizationCodeGrantType(String requestId) {

		throw new ConditionError(getId(), "Client has incorrectly called token_endpoint after receiving an id_token with an invalid aud value from the authorization_endpoint.");
	}

}