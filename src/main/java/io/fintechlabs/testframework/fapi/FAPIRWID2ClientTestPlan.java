package io.fintechlabs.testframework.fapi;

import io.fintechlabs.testframework.openbanking.FAPIRWID2OBClientTestInvalidAlternateAlg;
import io.fintechlabs.testframework.openbanking.FAPIRWID2OBClientTestInvalidNullAlg;
import io.fintechlabs.testframework.openbanking.FAPIRWID2OBClientTestInvalidOpenBankingIntentId;
import io.fintechlabs.testframework.openbanking.FAPIRWID2OBClientTestNoAtHash;
import io.fintechlabs.testframework.plan.PublishTestPlan;
import io.fintechlabs.testframework.plan.TestPlan;

@PublishTestPlan (
	testPlanName = "fapi-rw-id2-client-test-plan",
	displayName = "FAPI-RW-ID2: Relying Party (client test)",
	profile = "FAPI-RW-ID2-Relying-Party-Client-Test",
	testModules = {
		FAPIRWID2ClientTest.class,
		FAPIRWID2ClientTestInvalidSHash.class,
		FAPIRWID2ClientTestInvalidCHash.class,
		FAPIRWID2ClientTestInvalidNonce.class,
		FAPIRWID2ClientTestInvalidIss.class,
		FAPIRWID2ClientTestInvalidAud.class,
		FAPIRWID2ClientTestInvalidSecondaryAud.class,
		FAPIRWID2ClientTestInvalidSignature.class,
		FAPIRWID2ClientTestWithMTLSHolderOfKeyInvalidNullAlg.class,
		FAPIRWID2ClientTestWithMTLSHolderOfKeyInvalidAlternateAlg.class,
		FAPIRWID2ClientTestInvalidExpiredExp.class,
		FAPIRWID2ClientTestInvalidMissingExp.class,
		FAPIRWID2ClientTestIatIsWeekInPast.class,
		FAPIRWID2ClientTestInvalidMissingAud.class,
		FAPIRWID2ClientTestInvalidMissingIss.class,
		FAPIRWID2ClientTestInvalidMissingNonce.class,
		FAPIRWID2ClientTestInvalidMissingSHash.class,
		FAPIRWID2ClientTestValidAudAsArray.class,
		FAPIRWID2ClientTestWithPrivateKeyJWTAndMTLSHolderOfKeyInvalidNullAlg.class,
		FAPIRWID2ClientTestWithPrivateKeyJWTAndMTLSHolderOfKeyInvalidAlternateAlg.class,
		FAPIRWID2OBClientTestNoAtHash.class,
		FAPIRWID2OBClientTestInvalidOpenBankingIntentId.class,
		FAPIRWID2OBClientTestInvalidNullAlg.class,
		FAPIRWID2OBClientTestInvalidAlternateAlg.class
	},
	variants = {
		FAPIRWID2ClientTest.variant_mtls,
		FAPIRWID2ClientTest.variant_privatekeyjwt,
		FAPIRWID2ClientTest.variant_openbankinguk_mtls,
		FAPIRWID2ClientTest.variant_openbankinguk_privatekeyjwt
	}
)
public class FAPIRWID2ClientTestPlan implements TestPlan {

}
