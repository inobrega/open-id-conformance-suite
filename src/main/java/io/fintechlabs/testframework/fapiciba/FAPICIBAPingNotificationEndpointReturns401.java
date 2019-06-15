package io.fintechlabs.testframework.fapiciba;

import com.google.gson.JsonObject;
import io.fintechlabs.testframework.testmodule.PublishTestModule;
import io.fintechlabs.testframework.testmodule.Variant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@PublishTestModule(
	testName = "fapi-ciba-ping-with-mtls-backchannel-notification-endpoint-response-401",
	displayName = "FAPI-CIBA: Ping mode (MTLS client authentication) - backchannel notificatione endpoint returns a HTTP 401 Unauthorized response",
	summary = "The client's backchannel_notification_endpoint returns a HTTP 401 Unauthorized response and the authentication flow must still complete normally.",
	profile = "FAPI-CIBA",
	configurationFields = {
		"server.discoveryUrl",
		"client.client_id",
		"client.scope",
		"client.jwks",
		"client.hint_type",
		"client.hint_value",
		"mtls.key",
		"mtls.cert",
		"mtls.ca",
		"client2.client_id",
		"client2.scope",
		"client2.jwks",
		"mtls2.key",
		"mtls2.cert",
		"mtls2.ca",
		"resource.resourceUrl"
	}
)
public class FAPICIBAPingNotificationEndpointReturns401 extends FAPICIBAPingWithMTLS {
	@Variant(name = FAPICIBA.variant_ping_mtls)
	public void setupPingMTLS() {
		// FIXME: add other variants
		super.setupPingMTLS();
	}

	@Override
	protected Object handlePingCallback(JsonObject requestParts) {

		super.handlePingCallback(requestParts);
		return new ResponseEntity<Object>("CIBA Notification Endpoint returns a HTTP 401 Unauthorized response, even though the token is valid.", HttpStatus.UNAUTHORIZED);
	}

	protected void performPostAuthorizationFlow() {
		// just check access token, don't go on and try second client
		requestProtectedResource();
		fireTestFinished();
	}

}
