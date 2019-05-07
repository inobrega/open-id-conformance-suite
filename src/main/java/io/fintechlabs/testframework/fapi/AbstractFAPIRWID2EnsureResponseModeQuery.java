package io.fintechlabs.testframework.fapi;


import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.fintechlabs.testframework.condition.Condition;
import io.fintechlabs.testframework.condition.client.CheckAuthorizationResponseWhenResponseModeQuery;
import io.fintechlabs.testframework.condition.client.EnsureInvalidRequestError;
import io.fintechlabs.testframework.condition.client.ExpectResponseModeQueryErrorPage;
import io.fintechlabs.testframework.condition.client.ExtractImplicitHashToCallbackResponse;
import io.fintechlabs.testframework.condition.client.RejectAuthCodeInUrlQuery;
import io.fintechlabs.testframework.condition.client.SetAuthorizationEndpointRequestResponseModeToQuery;
import io.fintechlabs.testframework.condition.client.ValidateErrorResponseFromAuthorizationEndpoint;
import io.fintechlabs.testframework.condition.common.CreateRandomImplicitSubmitUrl;
import io.fintechlabs.testframework.testmodule.UserFacing;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AbstractFAPIRWID2EnsureResponseModeQuery extends AbstractFAPIRWID2ServerTestModule {

	@Override
	protected void performAuthorizationFlow() {

		createAuthorizationRequest();

		createAuthorizationRedirect();

		String redirectTo = env.getString("redirect_to_authorization_endpoint");

		eventLog.log(getName(), args("msg", "Redirecting to authorization endpoint",
			"redirect_to", redirectTo,
			"http", "redirect"));

		setStatus(Status.WAITING);

		callAndStopOnFailure(ExpectResponseModeQueryErrorPage.class, "OAuth2-RT-5");

		waitForPlaceholders();

		browser.goToUrl(redirectTo, env.getString("response_mode_error"));
	}

	@Override
	protected void createAuthorizationRequest() {

		super.createAuthorizationRequest();

		callAndStopOnFailure(SetAuthorizationEndpointRequestResponseModeToQuery.class);
	}

	@Override
	protected void processCallback() {

		// FAPI-RW always requires the hybrid flow, use the hash as the response
		env.mapKey("authorization_endpoint_response", "callback_params");

		callAndContinueOnFailure(RejectAuthCodeInUrlQuery.class, Condition.ConditionResult.FAILURE, "OIDCC-3.3.2.5");

		// This call may map authorization_endpoint_response onto callback_query_params if appropriate
		callAndContinueOnFailure(CheckAuthorizationResponseWhenResponseModeQuery.class, Condition.ConditionResult.FAILURE, "OAuth2-RT-5");

		JsonObject authorizationEndpointResponse = env.getObject("authorization_endpoint_response");

		if (authorizationEndpointResponse.has("error")) {

			callAndContinueOnFailure(ValidateErrorResponseFromAuthorizationEndpoint.class, Condition.ConditionResult.FAILURE, "OIDCC-3.1.2.6");

			callAndContinueOnFailure(EnsureInvalidRequestError.class, Condition.ConditionResult.FAILURE, "OIDCC-3.3.2.6");

		}

		fireTestFinished();
	}
}