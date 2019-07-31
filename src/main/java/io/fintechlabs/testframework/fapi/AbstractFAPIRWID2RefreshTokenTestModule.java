package io.fintechlabs.testframework.fapi;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import io.fintechlabs.testframework.condition.Condition;
import io.fintechlabs.testframework.condition.client.AddPromptConsentToAuthorizationEndpointRequestIfScopeContainsOfflineAccess;
import io.fintechlabs.testframework.condition.client.AddScopeToTokenEndpointRequest;
import io.fintechlabs.testframework.condition.client.CallTokenEndpointAndReturnFullResponse;
import io.fintechlabs.testframework.condition.client.CheckErrorFromTokenEndpointResponseErrorInvalidGrant;
import io.fintechlabs.testframework.condition.client.CheckForScopesInTokenResponse;
import io.fintechlabs.testframework.condition.client.CheckForSubjectInIdToken;
import io.fintechlabs.testframework.condition.client.CheckTokenEndpointHttpStatus400;
import io.fintechlabs.testframework.condition.client.CheckTokenEndpointReturnedJsonContentType;
import io.fintechlabs.testframework.condition.client.CheckTokenTypeIsBearer;
import io.fintechlabs.testframework.condition.client.CompareIdTokenClaims;
import io.fintechlabs.testframework.condition.client.CreateRefreshTokenRequest;
import io.fintechlabs.testframework.condition.client.EnsureAccessTokenContainsAllowedCharactersOnly;
import io.fintechlabs.testframework.condition.client.EnsureAccessTokenValuesAreDifferent;
import io.fintechlabs.testframework.condition.client.EnsureMinimumAccessTokenEntropy;
import io.fintechlabs.testframework.condition.client.EnsureRefreshTokenContainsAllowedCharactersOnly;
import io.fintechlabs.testframework.condition.client.ExtractAccessTokenFromTokenResponse;
import io.fintechlabs.testframework.condition.client.ExtractExpiresInFromTokenEndpointResponse;
import io.fintechlabs.testframework.condition.client.ExtractIdTokenFromTokenResponse;
import io.fintechlabs.testframework.condition.client.ExtractRefreshTokenFromTokenResponse;
import io.fintechlabs.testframework.condition.client.FAPIValidateIdTokenSigningAlg;
import io.fintechlabs.testframework.condition.client.ValidateErrorFromTokenEndpointResponseError;
import io.fintechlabs.testframework.condition.client.ValidateExpiresIn;
import io.fintechlabs.testframework.condition.client.ValidateIdToken;
import io.fintechlabs.testframework.condition.client.ValidateIdTokenNonce;
import io.fintechlabs.testframework.condition.client.ValidateIdTokenSignature;
import io.fintechlabs.testframework.condition.client.WaitForOneSecond;

public abstract class AbstractFAPIRWID2RefreshTokenTestModule extends AbstractFAPIRWID2ServerTestModule {

	protected void addPromptConsentToAuthorizationEndpointRequest() {
		callAndStopOnFailure(AddPromptConsentToAuthorizationEndpointRequestIfScopeContainsOfflineAccess.class, "OIDCC-11");
	}

	@Override
	protected void createAuthorizationRequest() {
		super.createAuthorizationRequest();
		addPromptConsentToAuthorizationEndpointRequest();
	}

	protected boolean sendRefreshTokenRequestAndCheckIdTokenClaims() {
		addIdTokenClaimsToEnv("first_id_token_claims");
		callAndContinueOnFailure(ExtractRefreshTokenFromTokenResponse.class, Condition.ConditionResult.INFO);
		//stop if no refresh token is returned
		if(Strings.isNullOrEmpty(env.getString("refresh_token"))) {
			fireTestFinished();
			return true;
		}
		callAndContinueOnFailure(EnsureRefreshTokenContainsAllowedCharactersOnly.class, Condition.ConditionResult.FAILURE, "RFC6749-A.17");
		refreshTokenRequest();
		//compare only when refresh response contains an id_token
		if(addIdTokenClaimsToEnv("second_id_token_claims")) {
			callAndContinueOnFailure(CompareIdTokenClaims.class, Condition.ConditionResult.FAILURE, "OIDCC-12.2");
		}
		return false;
	}

	protected boolean addIdTokenClaimsToEnv(String targetKey) {
		JsonObject idToken = env.getObject("id_token");
		if(idToken!=null) {
			env.putObject(targetKey, idToken.get("claims").getAsJsonObject());
			return true;
		}
		return false;
	}

	protected void refreshTokenRequest() {
		eventLog.startBlock(currentClientString() + "Refresh Token Request");
		callAndStopOnFailure(CreateRefreshTokenRequest.class);
		if (whichClient == 1) {
			callAndStopOnFailure(AddScopeToTokenEndpointRequest.class, "RFC6749-6");
		}

		addClientAuthenticationToTokenEndpointRequest();

		//wait 1 second to make sure that iat values will be different
		callAndStopOnFailure(WaitForOneSecond.class);

		callAndStopOnFailure(CallTokenEndpointAndReturnFullResponse.class);

		callAndStopOnFailure(ExtractAccessTokenFromTokenResponse.class);

		callAndContinueOnFailure(CheckTokenTypeIsBearer.class, Condition.ConditionResult.FAILURE, "FAPI-R-6.2.2-1");
		callAndContinueOnFailure(EnsureMinimumAccessTokenEntropy.class, Condition.ConditionResult.FAILURE, "FAPI-R-5.2.2-16");
		callAndContinueOnFailure(EnsureAccessTokenContainsAllowedCharactersOnly.class, Condition.ConditionResult.FAILURE, "RFC6749-A.12");
		callAndContinueOnFailure(ExtractExpiresInFromTokenEndpointResponse.class);
		skipIfMissing(new String[] { "expires_in" }, null, Condition.ConditionResult.INFO,
			ValidateExpiresIn.class, Condition.ConditionResult.FAILURE, "RFC6749-5.1");

		callAndContinueOnFailure(CheckForScopesInTokenResponse.class, Condition.ConditionResult.FAILURE, "FAPI-R-5.2.2-15");

		String secondAccessToken = env.getString("token_endpoint_response", "access_token");
		env.putString("second_access_token", secondAccessToken);

		callAndContinueOnFailure(EnsureAccessTokenValuesAreDifferent.class);

		env.removeObject("id_token");
		callAndContinueOnFailure(ExtractIdTokenFromTokenResponse.class);

		env.removeNativeValue("refresh_token");
		callAndContinueOnFailure(ExtractRefreshTokenFromTokenResponse.class, Condition.ConditionResult.INFO);

		eventLog.endBlock();
	}

	protected void refreshTokenRequestExpectingError() {
		callAndStopOnFailure(CreateRefreshTokenRequest.class);
		if (whichClient == 1) {
			callAndStopOnFailure(AddScopeToTokenEndpointRequest.class, "RFC6749-6");
		}

		addClientAuthenticationToTokenEndpointRequest();

		callAndContinueOnFailure(CallTokenEndpointAndReturnFullResponse.class);
		callAndStopOnFailure(ValidateErrorFromTokenEndpointResponseError.class);
		callAndContinueOnFailure(CheckTokenEndpointHttpStatus400.class, Condition.ConditionResult.FAILURE, "OIDCC-3.1.3.4");
		callAndContinueOnFailure(CheckTokenEndpointReturnedJsonContentType.class, Condition.ConditionResult.FAILURE, "OIDCC-3.1.3.4");
		callAndContinueOnFailure(CheckErrorFromTokenEndpointResponseErrorInvalidGrant.class, Condition.ConditionResult.FAILURE, "RFC6749-5.2");
	}

	protected void performIdTokenValidation() {
		addIdTokenClaimsToEnv("first_id_token_claims");

		callAndContinueOnFailure(ValidateIdToken.class, Condition.ConditionResult.FAILURE, "FAPI-RW-5.2.2-3");

		callAndContinueOnFailure(ValidateIdTokenNonce.class, Condition.ConditionResult.FAILURE,"OIDCC-2");

		performProfileIdTokenValidation();

		callAndContinueOnFailure(ValidateIdTokenSignature.class, Condition.ConditionResult.FAILURE, "FAPI-RW-5.2.2-3");

		callAndContinueOnFailure(CheckForSubjectInIdToken.class, Condition.ConditionResult.FAILURE, "FAPI-R-5.2.2-24", "OB-5.2.2-8");
		callAndContinueOnFailure(FAPIValidateIdTokenSigningAlg.class, Condition.ConditionResult.FAILURE, "FAPI-RW-8.6");
	}

	@Override
	protected void performPostAuthorizationFlow() {
		if (whichClient == 1) {
			// call the token endpoint and complete the flow

			createAuthorizationCodeRequest();

			requestAuthorizationCode();
			String firstAccessToken = env.getString("token_endpoint_response", "access_token");
			env.putString("first_access_token", firstAccessToken);

			if(sendRefreshTokenRequestAndCheckIdTokenClaims()) {
				return;
			}

			requestProtectedResource();

			verifyAccessTokenWithResourceEndpoint();

			// Try the second client

			//remove refresh token from 1st client
			env.removeNativeValue("refresh_token");

			performAuthorizationFlowWithSecondClient();
		} else {
			// call the token endpoint and complete the flow

			createAuthorizationCodeRequest();

			requestAuthorizationCode();
			String firstAccessToken = env.getString("token_endpoint_response", "access_token");
			env.putString("first_access_token", firstAccessToken);

			if(sendRefreshTokenRequestAndCheckIdTokenClaims()) {
				return;
			}

			requestProtectedResource();

			performProtectedResourceRequestWithFirstClientKeysExpectingError();

			// try client 2's refresh_token with client 1
			eventLog.startBlock("Attempting to use refresh_token issued to client 2 with client 1");
			refreshTokenRequestExpectingError();
			eventLog.endBlock();
			fireTestFinished();
		}
	}
}