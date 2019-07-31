package io.fintechlabs.testframework.fapi;

import io.fintechlabs.testframework.testmodule.PublishTestModule;
import io.fintechlabs.testframework.testmodule.Variant;

@PublishTestModule(
	testName = "fapi-rw-id2-ensure-response-mode-query",
	displayName = "FAPI-RW-ID2: ensure response_mode query",
	summary = "This test includes response_mode=query in the authorization request. The authorization server should show an error message that response_mode=query is not allowed when response_type is 'code id_token' (a screenshot of which should be uploaded), should return an error to the client, or must successfully authenticate.",
	profile = "FAPI-RW-ID2",
	configurationFields = {
		"server.discoveryUrl",
		"client.client_id",
		"client.scope",
		"client.jwks",
		"mtls.key",
		"mtls.cert",
		"mtls.ca",
		"client2.client_id",
		"client2.scope",
		"client2.jwks",
		"mtls2.key",
		"mtls2.cert",
		"mtls2.ca",
		"resource.resourceUrl",
		"resource.institution_id"
	},
	notApplicableForVariants = {
		FAPIRWID2.variant_openbankinguk_mtls,
		FAPIRWID2.variant_openbankinguk_privatekeyjwt
	}
)
public class FAPIRWID2EnsureResponseModeQuery extends AbstractFAPIRWID2EnsureResponseModeQuery {

	@Variant(name = variant_mtls)
	public void setupMTLS() {
		super.setupMTLS();
	}

	@Variant(name = variant_privatekeyjwt)
	public void setupPrivateKeyJwt() {
		super.setupPrivateKeyJwt();
	}
}