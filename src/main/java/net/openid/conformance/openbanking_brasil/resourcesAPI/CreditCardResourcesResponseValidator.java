package net.openid.conformance.openbanking_brasil.resourcesAPI;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.condition.client.AbstractJsonAssertingCondition;
import net.openid.conformance.util.field.*;
import net.openid.conformance.logging.ApiName;
import net.openid.conformance.testmodule.Environment;
import net.openid.conformance.util.field.ArrayField;
import net.openid.conformance.util.field.StringField;

import java.util.Set;

/**
 * This is validator for API - Resources "Obtém a lista de recursos consentidos pelo cliente."
 * See <a href="https://openbanking-brasil.github.io/areadesenvolvedor/#obtem-a-lista-de-recursos-consentidos-pelo-cliente">
 * Obtém a lista de recursos consentidos pelo cliente.</a>
 **/
@ApiName("Resources")
public class CreditCardResourcesResponseValidator extends AbstractJsonAssertingCondition {

	@Override
	@PreEnvironment(strings = "resource_endpoint_response")
	public Environment evaluate(Environment environment) {
		JsonObject body = bodyFrom(environment);
		assertHasField(body, ROOT_PATH);
		assertField(body,
			new ArrayField.Builder("data")
				.setMinItems(1)
				.build());

		assertJsonArrays(body, ROOT_PATH, this::assertInnerFields);
		return environment;
	}

	private void assertInnerFields(JsonObject body) {
		Set<String> enumType = Sets.newHashSet("CREDIT_CARD_ACCOUNT");

		Set<String> enumStatus = Sets.newHashSet("AVAILABLE", "UNAVAILABLE", "TEMPORARILY_UNAVAILABLE", "PENDING_AUTHORISATION");
		assertField(body,
			new StringField
				.Builder("resourceId")
				.setPattern("^[a-zA-Z0-9][a-zA-Z0-9\\-]{0,99}$")
				.setMaxLength(100)
				.setMinLength(1)
				.build());
		
		assertField(body,
			new StringField
				.Builder("type")
				.setPattern("CREDIT_CARD_ACCOUNT")
				.build());

		assertField(body,
			new StringField
				.Builder("status")
				.setEnums(enumStatus)
				.build());

	}
}