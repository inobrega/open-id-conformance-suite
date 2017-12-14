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

package io.fintechlabs.testframework.condition;

import java.text.ParseException;

import com.google.gson.JsonObject;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.fintechlabs.testframework.logging.EventLog;
import io.fintechlabs.testframework.testmodule.Environment;

public class SignRequestObject extends AbstractCondition {

	/**
	 * @param testId
	 * @param log
	 * @param optional
	 */
	public SignRequestObject(String testId, EventLog log, boolean optional) {
		super(testId, log, optional);
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.condition.Condition#evaluate(io.fintechlabs.testframework.testmodule.Environment)
	 */
	@Override
	public Environment evaluate(Environment env) {

		JsonObject requestObjectClaims = env.get("request_object_claims");
		JsonObject jwks = env.get("jwks");

		if (requestObjectClaims == null) {
			return error("Couldn't find request object claims");
		}

		if (jwks == null) {
			return error("Couldn't find jwks");
		}

		if (!requestObjectClaims.has("iss")) {
			String clientId = env.getString("client_id");
			if (clientId != null) {
				requestObjectClaims.addProperty("iss", clientId);
			} else {
				// Only a "should" requirement
				log("Request object contains no issuer and client ID not found");
			}
		}

		if (!requestObjectClaims.has("aud")) {
			String serverIssuerUrl = env.getString("server", "issuer");
			if (serverIssuerUrl != null) {
				requestObjectClaims.addProperty("aud", serverIssuerUrl);
			} else {
				// Only a "should" requirement
				log("Request object contains no audience and server issuer URL not found");
			}
		}

		try {
			JWTClaimsSet claimSet = JWTClaimsSet.parse(requestObjectClaims.toString());

			JWKSet jwkSet = JWKSet.parse(jwks.toString());

			if (jwkSet.getKeys().size() == 1) {
				// figure out which algorithm to use
				JWK jwk = jwkSet.getKeys().iterator().next();

				JWSSigner signer = null;
				if (jwk.getKeyType().equals(KeyType.RSA)) {
					signer = new RSASSASigner((RSAKey) jwk);
				} else if (jwk.getKeyType().equals(KeyType.EC)) {
					signer = new ECDSASigner((ECKey) jwk);
				} else if (jwk.getKeyType().equals(KeyType.OCT)) {
					signer = new MACSigner((OctetSequenceKey) jwk);
				}

				if (signer == null) {
					return error("Couldn't create signer from key", args("jwk", jwk.toJSONString()));
				}

				JWSHeader header = new JWSHeader(JWSAlgorithm.parse(jwk.getAlgorithm().getName()), null, null, null, null, null, null, null, null, null, jwk.getKeyID(), null, null);

				SignedJWT requestObject = new SignedJWT(header, claimSet);

				requestObject.sign(signer);

				env.putString("request_object", requestObject.serialize());

				logSuccess("Signed the request object", args("request_object", requestObject.serialize()));

				return env;
			} else {
				return error("Expected only one JWK in the set", args("found", jwkSet.getKeys().size()));
			}
		} catch (ParseException e) {
			return error(e);
		} catch (JOSEException e) {
			return error(e);
		}

	}

}