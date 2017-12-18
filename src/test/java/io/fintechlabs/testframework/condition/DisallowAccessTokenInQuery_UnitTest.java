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

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.badRequest;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.fintechlabs.testframework.condition.Condition.ConditionResult;
import io.fintechlabs.testframework.logging.TestInstanceEventLog;
import io.fintechlabs.testframework.testmodule.Environment;
import io.specto.hoverfly.junit.rule.HoverflyRule;

@RunWith(MockitoJUnitRunner.class)
public class DisallowAccessTokenInQuery_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	// Examples from RFC 6749

	private static JsonObject bearerToken = new JsonParser().parse("{"
			+ "\"value\":\"mF_9.B5f-4.1JqM\","
			+ "\"type\":\"Bearer\""
			+ "}").getAsJsonObject();

	@ClassRule
	public static HoverflyRule hoverfly = HoverflyRule.inSimulationMode(dsl(
		service("good.example.com")
			.get("/resource")
			.queryParam("access_token", any())
			.willReturn(badRequest().body("Bad Request")),
		service("bad.example.com")
			.get("/resource")
			.queryParam("access_token", "mF_9.B5f-4.1JqM")
			.willReturn(success("OK", "text/plain"))
	));

	private DisallowAccessTokenInQuery cond;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		hoverfly.resetJournal();

		cond = new DisallowAccessTokenInQuery("UNIT-TEST", eventLog, ConditionResult.INFO);

		env.put("config", new JsonObject());
	}

	/**
	 * Test method for {@link io.fintechlabs.testframework.condition.CallResourceEndpointWithBearerToken#evaluate(io.fintechlabs.testframework.testmodule.Environment)}.
	 */
	@Test
	public void testEvaluate_noError() {

		env.put("access_token", bearerToken);
		env.get("config").addProperty("resourceUrl", "http://good.example.com/resource");

		cond.evaluate(env);

		hoverfly.verify(service("good.example.com")
				.get("/resource")
				.queryParam("access_token", "mF_9.B5f-4.1JqM"));

		verify(env, atLeastOnce()).getString("access_token", "value");
		verify(env, atLeastOnce()).getString("config", "resourceUrl");
	}

	/**
	 * Test method for {@link io.fintechlabs.testframework.condition.DisallowAccessTokenInQuery#evaluate(io.fintechlabs.testframework.testmodule.Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_disallowedQueryAccepted() {

		env.put("access_token", bearerToken);
		env.get("config").addProperty("resourceUrl", "http://bad.example.com/resource");

		cond.evaluate(env);

	}

	/**
	 * Test method for {@link io.fintechlabs.testframework.condition.DisallowAccessTokenInQuery#evaluate(io.fintechlabs.testframework.testmodule.Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_badServer() {

		env.put("access_token", bearerToken);
		env.get("config").addProperty("resourceUrl", "http://invalid.org/resource");

		cond.evaluate(env);

	}

	/**
	 * Test method for {@link io.fintechlabs.testframework.condition.DisallowAccessTokenInQuery#evaluate(io.fintechlabs.testframework.testmodule.Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_missingToken() {

		env.get("config").addProperty("resourceUrl", "http://good.example.com/resource");

		cond.evaluate(env);

	}

	/**
	 * Test method for {@link io.fintechlabs.testframework.condition.DisallowAccessTokenInQuery#evaluate(io.fintechlabs.testframework.testmodule.Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_missingUrl() {

		env.put("access_token", bearerToken);

		cond.evaluate(env);

	}

}
