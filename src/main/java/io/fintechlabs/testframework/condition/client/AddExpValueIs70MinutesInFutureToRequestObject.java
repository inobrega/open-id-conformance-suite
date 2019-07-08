package io.fintechlabs.testframework.condition.client;

import com.google.gson.JsonObject;
import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.PostEnvironment;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.testmodule.Environment;

import java.time.Instant;

public class AddExpValueIs70MinutesInFutureToRequestObject extends AbstractCondition {

	@Override
	@PreEnvironment(required = "request_object_claims")
	@PostEnvironment(required = "request_object_claims")
	public Environment evaluate(Environment env) {

		JsonObject requestObjectClaims = env.getObject("request_object_claims");

		Instant exp = Instant.now().plusSeconds(70 * 60);

		requestObjectClaims.addProperty("exp", exp.getEpochSecond());

		env.putObject("request_object_claims", requestObjectClaims);

		logSuccess("Added invalid exp value to request object which is 70 minutes in the future", args("request_object_claims", requestObjectClaims, "iat_is_70_minutes_in_the_future", exp));

		return env;

	}
}