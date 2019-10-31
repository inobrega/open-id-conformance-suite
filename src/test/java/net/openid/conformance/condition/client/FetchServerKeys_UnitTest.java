package net.openid.conformance.condition.client;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import io.specto.hoverfly.junit.rule.HoverflyRule;

@RunWith(MockitoJUnitRunner.class)
public class FetchServerKeys_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private static String jwksStr = "{"
		+ "\"keys\":["
		+ "{"
		+ "\"kty\":\"oct\","
		+ "\"alg\":\"HS256\","
		+ "\"k\":\"UzUgc1C/vF44Uf9jZuswyJrivNwGas6uVYhVEi7GKUQ\""
		+ "},"
		+ "{"
		+ "\"kty\":\"oct\","
		+ "\"alg\":\"HS256\","
		+ "\"k\":\"LAEuRo0oklLIyw/md746i3ZhbCPd4UoZ7+J421/avfM\""
		+ "}"
		+ "]}";

	@ClassRule
	public static HoverflyRule hoverfly = HoverflyRule.inSimulationMode(dsl(
		service("good.example.com")
			.get("/jwks.json")
			.willReturn(success(jwksStr, "application/json")),
		service("bad.example.com")
			.get("/jwks.json")
			.willReturn(success("This is not JSON!", "text/plain"))));

	private FetchServerKeys cond;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		hoverfly.resetJournal();

		cond = new FetchServerKeys();

		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);
	}

	/**
	 * Test method for {@link FetchServerKeys#evaluate(Environment)}.
	 */
	@Test
	public void testEvaluate_static() {

		JsonObject jwks = new JsonParser().parse(jwksStr).getAsJsonObject();

		JsonObject server = new JsonObject();
		server.add("jwks", jwks);
		server.addProperty("jwks_uri", "https://good.example.com/jwks.json");
		env.putObject("server", server);

		cond.execute(env);

		hoverfly.verifyZeroRequestTo(service("good.example.com"));

		assertThat(env.getObject("server_jwks")).isEqualTo(jwks);
	}

	/**
	 * Test method for {@link FetchServerKeys#evaluate(Environment)}.
	 */
	@Test
	public void testEvaluate_dynamic() {

		JsonObject jwks = new JsonParser().parse(jwksStr).getAsJsonObject();

		JsonObject server = new JsonObject();
		server.addProperty("jwks_uri", "https://good.example.com/jwks.json");
		env.putObject("server", server);

		cond.execute(env);

		hoverfly.verify(service("good.example.com").get("/jwks.json"));

		assertThat(env.getObject("server_jwks")).isEqualTo(jwks);
	}

	/**
	 * Test method for {@link FetchServerKeys#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_missingUri() {

		env.putObject("server", new JsonObject());

		cond.execute(env);
	}

	/**
	 * Test method for {@link FetchServerKeys#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_missingConfig() {

		cond.execute(env);
	}

	/**
	 * Test method for {@link FetchServerKeys#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_noServer() {

		JsonObject server = new JsonObject();
		server.addProperty("jwks_uri", "https://nonexisting.example.com/jwks.json");
		env.putObject("server", server);

		cond.execute(env);
	}

	/**
	 * Test method for {@link FetchServerKeys#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_badResponse() {

		JsonObject server = new JsonObject();
		server.addProperty("jwks_uri", "https://bad.example.com/jwks.json");
		env.putObject("server", server);

		cond.execute(env);
	}
}