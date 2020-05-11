package net.openid.conformance.condition.rs;

import com.google.gson.JsonObject;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.client.AbstractSignJWT;
import net.openid.conformance.testmodule.Environment;

public class OIDCCLoadUserInfoWithAggregatedClaims extends AbstractSignJWT {

	@Override
	@PostEnvironment(required = "user_info")
	public Environment evaluate(Environment env) {

		JsonObject user = new JsonObject();

		user.addProperty("sub", "user-subject-1234531");

		//directly copied from the example in the spec
		String claims="{" +
			"\"address\": {\n" +
			"\"street_address\": \"1234 Hollywood Blvd.\",\n" +
			"\"locality\": \"Los Angeles\",\n" +
			"\"region\": \"CA\",\n" +
			"\"postal_code\": \"90210\",\n" +
			"\"country\": \"US\"" +
			"},\n" +
			"\"phone_number\": \"+1 (310) 123-4567\"\n" +
			"}";

		//using alg none as python suite signs with alg none
		String signedJwt = signWithAlgNone(claims);
		JsonObject claimNames = new JsonObject();
		claimNames.addProperty("address", "src1");
		claimNames.addProperty("phone_number", "src1");
		user.add("_claim_names", claimNames);
		JsonObject claimSources = new JsonObject();
		claimSources.addProperty("src1", signedJwt);
		user.add("_claim_sources", claimSources);

		env.putObject("user_info", user);

		logSuccess("Added user information", args("user_info", user));

		return env;
	}

	/**
	 * this method won't be actually called when signing using alg none
	 */
	@Override
	protected void logSuccessByJWTType(Environment env, JWTClaimsSet claimSet, JWK jwk, JWSHeader header, String jws, JsonObject verifiableObj) {
		logSuccess("Signed aggregated claims with alg 'none'");
	}
}
