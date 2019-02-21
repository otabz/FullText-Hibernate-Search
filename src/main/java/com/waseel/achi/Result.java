package com.waseel.achi;

public class Result {

	public static enum Outcome {

		SUCCESS("success"), FAILURE("failure");

		private final String value;

		private Outcome(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}

	protected String outcome;
	protected String message;

	public Result(Outcome outcome, String message) {
		this.outcome = outcome.value();
		this.message = message;
	}

	public String getOutcome() {
		return outcome;
	}

	public String getMessage() {
		return message;
	}

	public static class NoResult extends Result {

		public NoResult(String message) {
			super(Result.Outcome.FAILURE, message);
		}

		public NoResult() {
			this("Oops! Something crashed. Please, try again.");
		}
	}
}
