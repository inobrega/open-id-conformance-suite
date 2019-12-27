package net.openid.conformance.openid;

import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.client.CallUserInfoEndpointWithBearerToken;
import net.openid.conformance.condition.client.EnsureMemberValuesInClaimNameReferenceToMemberNamesInClaimSources;
import net.openid.conformance.condition.client.EnsureUserInfoBirthDateValid;
import net.openid.conformance.condition.client.EnsureUserInfoContainsSub;
import net.openid.conformance.condition.client.ExtractUserInfoFromUserInfoEndpointResponse;
import net.openid.conformance.condition.client.ValidateUserInfoStandardClaims;
import net.openid.conformance.condition.client.VerifyUserInfoAndIdTokenInAuthorizationEndpointSameSub;
import net.openid.conformance.condition.client.VerifyUserInfoAndIdTokenInTokenEndpointSameSub;

public abstract class AbstractOIDCCUserInfoTest extends AbstractOIDCCServerTest {

	@Override
	protected void onPostAuthorizationFlowComplete() {
		callUserInfoEndpoint();
		callAndStopOnFailure(ExtractUserInfoFromUserInfoEndpointResponse.class);
		validateUserInfoResponse();
		fireTestFinished();
	}

	protected void callUserInfoEndpoint() {
		callAndStopOnFailure(CallUserInfoEndpointWithBearerToken.class, "OIDCC-5.3.1");
	}

	protected void validateUserInfoResponse() {
		callAndContinueOnFailure(ValidateUserInfoStandardClaims.class, ConditionResult.FAILURE, "OIDCC-5.1");
		callAndContinueOnFailure(EnsureUserInfoContainsSub.class, ConditionResult.FAILURE, "OIDCC-5.3.2");
		callAndContinueOnFailure(EnsureUserInfoBirthDateValid.class, ConditionResult.FAILURE, "OIDCC-5.1");
		callAndContinueOnFailure(EnsureMemberValuesInClaimNameReferenceToMemberNamesInClaimSources.class, ConditionResult.FAILURE, "OIDCC-5.6.2");

		if (responseType.includesIdToken()) {
			callAndContinueOnFailure(VerifyUserInfoAndIdTokenInAuthorizationEndpointSameSub.class, ConditionResult.FAILURE, "OIDCC-5.3.2");

			if (responseType.includesCode()) {
				callAndContinueOnFailure(VerifyUserInfoAndIdTokenInTokenEndpointSameSub.class, ConditionResult.FAILURE,  "OIDCC-5.3.2");
			}
		}

	}

}