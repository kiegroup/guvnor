package org.drools.ide.common.client.modeldriven.brl;

import org.drools.ide.common.client.modeldriven.MethodInfo;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

public class ExpressionPartHelper {
	public static ExpressionPart getExpressionPartForMethod(SuggestionCompletionEngine sce, String factName, String methodName) {
		MethodInfo mi = sce.getMethodinfo(factName, methodName);
		if (SuggestionCompletionEngine.TYPE_COLLECTION.equals(mi.getGenericType())) {
			return new ExpressionCollection(methodName, mi.getReturnClassType(),
					mi.getGenericType(), mi.getParametricReturnType());
		} 
		return new ExpressionMethod(mi.getName(), mi.getReturnClassType(), mi.getGenericType());
	}
	
	public static ExpressionPart getExpressionPartForField(SuggestionCompletionEngine sce, String factName, String fieldName) {
		String fieldClassName = sce.getFieldClassName(factName, fieldName);
		String fieldGenericType = sce.getFieldType(factName, fieldName);
		if (SuggestionCompletionEngine.TYPE_COLLECTION.equals(fieldGenericType)) {
			String fieldParametricType = sce.getParametricFieldType(factName, fieldName);
			return new ExpressionCollection(fieldName, fieldClassName, fieldGenericType,
					fieldParametricType);
		} 
		return new ExpressionField(fieldName, fieldClassName, fieldGenericType);
	}
	
	public static ExpressionPart getExpressionPartForGlobalVariable(SuggestionCompletionEngine sce, String varName) {
		String globalVarType = sce.getGlobalVariable(varName);
		return new ExpressionGlobalVariable(varName, globalVarType, globalVarType);
	}
}
