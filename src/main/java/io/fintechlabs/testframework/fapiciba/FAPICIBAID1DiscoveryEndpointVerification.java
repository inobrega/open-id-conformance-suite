package io.fintechlabs.testframework.fapiciba;

import io.fintechlabs.testframework.condition.Condition;
import io.fintechlabs.testframework.condition.client.CheckBackchannelTokenDeliveryPingModeSupported;
import io.fintechlabs.testframework.condition.client.CheckBackchannelTokenDeliveryPollModeSupported;
import io.fintechlabs.testframework.condition.client.CheckBackchannelUserCodeParameterSupported;
import io.fintechlabs.testframework.condition.client.CheckDiscBackchannelAuthorizationEndpoint;
import io.fintechlabs.testframework.condition.client.CheckDiscEndpointBackchannelAuthenticationRequestSigningAlgValuesSupported;
import io.fintechlabs.testframework.condition.client.FAPICIBACheckDiscEndpointGrantTypesSupported;
import io.fintechlabs.testframework.fapi.AbstractFAPIDiscoveryEndpointVerification;
import io.fintechlabs.testframework.sequence.AbstractConditionSequence;
import io.fintechlabs.testframework.sequence.ConditionSequence;
import io.fintechlabs.testframework.testmodule.PublishTestModule;
import io.fintechlabs.testframework.testmodule.Variant;

@PublishTestModule(
	testName = "fapi-ciba-id1-discovery-end-point-verification",
	displayName = "FAPI-CIBA-ID1: Discovery Endpoint Verification",
	summary = "This test ensures that the server's discovery document (including token_delivery_modes, response_types, grant_types etc) contains correct values.",
	profile = "FAPI-CIBA-ID1",
	configurationFields = {
		"server.discoveryUrl",
	}
)
public class FAPICIBAID1DiscoveryEndpointVerification extends AbstractFAPIDiscoveryEndpointVerification {
	private Class<? extends ConditionSequence> variantModeChecks;

	public static class PollChecks extends AbstractConditionSequence
	{
		@Override
		public void evaluate() {
			callAndContinueOnFailure(CheckBackchannelTokenDeliveryPollModeSupported.class, Condition.ConditionResult.FAILURE, "FAPI-RW-5.2.2-6");

		}
	}
	public static class PingChecks extends AbstractConditionSequence
	{
		@Override
		public void evaluate() {
			callAndContinueOnFailure(CheckBackchannelTokenDeliveryPingModeSupported.class, Condition.ConditionResult.FAILURE, "FAPI-RW-5.2.2-6");

		}
	}

	@Variant(name = FAPICIBAID1.variant_ping_mtls)
	public void setupPingMTLS() {
		super.setupMTLS();
		variantModeChecks = PingChecks.class;
	}

	@Variant(name = FAPICIBAID1.variant_ping_privatekeyjwt)
	public void setupPingPrivateKeyJwt() {
		super.setupPrivateKeyJwt();
		variantModeChecks = PingChecks.class;
	}

	@Variant(name = FAPICIBAID1.variant_poll_mtls)
	public void setupPollMTLS() {
		super.setupMTLS();
		variantModeChecks = PollChecks.class;
	}

	@Variant(name = FAPICIBAID1.variant_poll_privatekeyjwt)
	public void setupPollPrivateKeyJwt() {
		super.setupPrivateKeyJwt();
		variantModeChecks = PollChecks.class;
	}

	@Variant(name = FAPICIBAID1.variant_openbankinguk_poll_mtls)
	public void setupOpenBankingUkPollMTLS() {
		super.setupMTLS();
		variantModeChecks = PollChecks.class;
	}

	@Variant(name = FAPICIBAID1.variant_openbankinguk_poll_privatekeyjwt)
	public void setupOpenBankingUkPollPrivateKeyJwt() {
		super.setupPrivateKeyJwt();
		variantModeChecks = PollChecks.class;
	}

	@Variant(name = FAPICIBAID1.variant_openbankinguk_ping_mtls)
	public void setupOpenBankingUkPingMTLS() {
		super.setupMTLS();
		variantModeChecks = PingChecks.class;
	}

	@Variant(name = FAPICIBAID1.variant_openbankinguk_ping_privatekeyjwt)
	public void setupOpenBankingUkPingPrivateKeyJwt() {
		super.setupPrivateKeyJwt();
		variantModeChecks = PingChecks.class;
	}

	@Override
	protected void performEndpointVerification() {
		super.performEndpointVerification();

		callAndContinueOnFailure(CheckDiscBackchannelAuthorizationEndpoint.class, Condition.ConditionResult.FAILURE, "CIBA-4");
		callAndContinueOnFailure(CheckDiscEndpointBackchannelAuthenticationRequestSigningAlgValuesSupported.class, Condition.ConditionResult.WARNING, "CIBA-4");
		callAndContinueOnFailure(CheckBackchannelUserCodeParameterSupported.class, Condition.ConditionResult.WARNING, "CIBA-4");
		callAndContinueOnFailure(FAPICIBACheckDiscEndpointGrantTypesSupported.class, Condition.ConditionResult.FAILURE, "CIBA-4");

		performProfileSpecificChecks();

		call(sequence(variantModeChecks));
	}

	public void performProfileSpecificChecks() {
	}
}