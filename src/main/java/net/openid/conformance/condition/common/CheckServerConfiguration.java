package net.openid.conformance.condition.common;

import com.google.common.collect.ImmutableList;
import net.openid.conformance.testmodule.Environment;

import java.util.List;

public class CheckServerConfiguration extends AbstractCheckServerConfiguration {

	@Override
	protected List<String> getExpectedListEndpoint() {
		return ImmutableList.of("authorization_endpoint",
//			"token_endpoint",
			"issuer");
	}

}
