package org.drools.ide.common.client.modeldriven.testing;


public class CallMethod implements Fixture {

	/*
	 * the function name was not yet choose
	 */

	public static final int TYPE_UNDEFINED = 0;

	/**
	 * The function has been choosen
	 */
	public static final int TYPE_DEFINED = 1;
	/*
	 * shows the state of the method call TYPE_UNDEFINED => the user has not
	 * choosen a method or TYPE_DEFINED => The user has choosen a function
	 */
	public int state;

	public String methodName;

	public String variable;
	public CallFieldValue[] callFieldValues = new CallFieldValue[0];

	public CallMethod() {
	}

	public CallMethod(String variable) {
		super();
		this.variable = variable;
	}

	public void removeField(final int idx) {

		final CallFieldValue[] newList = new CallFieldValue[this.callFieldValues.length - 1];
		int newIdx = 0;
		for (int i = 0; i < this.callFieldValues.length; i++) {

			if (i != idx) {
				newList[newIdx] = this.callFieldValues[i];
				newIdx++;
			}

		}
		this.callFieldValues = newList;
	}

	public void addFieldValue(final CallFieldValue val) {
		if (this.callFieldValues == null) {
			this.callFieldValues = new CallFieldValue[1];
			this.callFieldValues[0] = val;
		} else {
			final CallFieldValue[] newList = new CallFieldValue[this.callFieldValues.length + 1];
			for (int i = 0; i < this.callFieldValues.length; i++) {
				newList[i] = this.callFieldValues[i];
			}
			newList[this.callFieldValues.length] = val;
			this.callFieldValues = newList;
		}
	}
}
