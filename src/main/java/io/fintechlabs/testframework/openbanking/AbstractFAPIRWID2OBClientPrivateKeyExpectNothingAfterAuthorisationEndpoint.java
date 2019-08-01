package io.fintechlabs.testframework.openbanking;

public abstract class AbstractFAPIRWID2OBClientPrivateKeyExpectNothingAfterAuthorisationEndpoint extends AbstractFAPIRWID2OBClientTest {

	@Override
	protected Object authorizationEndpoint(String requestId){

		Object returnValue = super.authorizationEndpoint(requestId);

		getTestExecutionManager().runInBackground(() -> {
			Thread.sleep(5 * 1000);
			if (getStatus().equals(Status.WAITING)) {
				setStatus(Status.RUNNING);
				//As the client hasn't called the token endpoint after 5 seconds, assume it has correctly detected the error and aborted.
				fireTestFinished();
			}

			return "done";

		});

		return returnValue;
	}

	protected abstract Object authorizationCodeGrantType(String requestId);

}
