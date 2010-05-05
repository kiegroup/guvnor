package org.drools.ide.common.server.rules;

public interface ClassToGenericClassConverter {

	/**
	 * @param inspector
	 * @param fields
	 * @param i
	 * @return
	 */
	String translateClassToGenericType(final Class<?> type);

}