package net.openid.conformance.sequence.as;

import net.openid.conformance.condition.as.AddJARMResponseModeToServerConfiguration;
import net.openid.conformance.condition.as.AddResponseTypeCodeIdTokenToServerConfiguration;
import net.openid.conformance.condition.as.AddResponseTypeCodeToServerConfiguration;
import net.openid.conformance.sequence.AbstractConditionSequence;

public class AddPlainFAPIToServerConfiguration extends AbstractConditionSequence {

	@Override
	public void evaluate() {
		callAndStopOnFailure(AddResponseTypeCodeIdTokenToServerConfiguration.class, "FAPI1-ADVANCED-5.2.2-2");
	}
}
