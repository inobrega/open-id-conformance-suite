package net.openid.conformance.sequence.client;

import net.openid.conformance.condition.client.AddClientIdToPAREndpointRequest;
import net.openid.conformance.condition.client.BuildMTLSRequestObjectPostToPAREndpoint;
import net.openid.conformance.sequence.AbstractConditionSequence;

public class AddMTLSClientAuthenticationToPAREndpointRequest extends AbstractConditionSequence {

	@Override
	public void evaluate() {
		callAndStopOnFailure(BuildMTLSRequestObjectPostToPAREndpoint.class);
		callAndStopOnFailure(AddClientIdToPAREndpointRequest.class);
	}

}