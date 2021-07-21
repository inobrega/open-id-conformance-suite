package net.openid.conformance.openbanking_brasil.testmodules.support;

import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.testmodule.Environment;

public class RequestBadPermission extends AbstractCondition {

	@Override
	@PostEnvironment(strings = "consent_permissions")
	public Environment evaluate(Environment env) {
		String[] permissions = {"BAD_PERMISSION"};
		env.putString("consent_permissions", String.join(" ", permissions));
		return env;
	}

}