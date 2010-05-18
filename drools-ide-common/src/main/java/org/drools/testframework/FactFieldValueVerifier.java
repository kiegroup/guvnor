package org.drools.testframework;

import static org.mvel2.MVEL.eval;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;

/**
 * 
 * @author rikkola
 *
 */
public class FactFieldValueVerifier {

    private final Map<String, Object> populatedData;
    private final String              factName;
    private final Object              factObject;

    private VerifyField               currentField;

    public FactFieldValueVerifier(Map<String, Object> populatedData,
                             String factName,
                             Object factObject) {
        this.populatedData = populatedData;
        this.factName = factName;
        this.factObject = factObject;
    }

    public void checkFields(List<VerifyField> fieldValues) {
        Iterator<VerifyField> fields = fieldValues.iterator();
        while ( fields.hasNext() ) {
            this.currentField = fields.next();

            if ( currentField.expected != null ) {
                ResultVerifier resultVerifier = new ResultVerifier( factObject );

                resultVerifier.setExpected( getExpectedResult() );

                currentField.successResult = resultVerifier.isSuccess( currentField );

                if ( !currentField.successResult ) {
                    currentField.actualResult = resultVerifier.getActual( currentField );

                    currentField.explanation = getFailingExplanation();
                } else {
                    currentField.explanation = getSuccesfulExplanation();
                }
            }

        }

    }

    private Object getExpectedResult() {
        Object expectedResult = currentField.expected.trim();
        if ( currentField.expected.startsWith( "=" ) ) {
            expectedResult = eval( currentField.expected.substring( 1 ),
                                   this.populatedData );
        }
        return expectedResult;
    }

    private String getSuccesfulExplanation() {
        if ( currentField.operator.equals( "==" ) ) {
            return "[" + factName + "] field [" + currentField.fieldName + "] was [" + currentField.expected + "].";
        } else if ( currentField.operator.equals( "!=" ) ) {
            return "[" + factName + "] field [" + currentField.fieldName + "] was not [" + currentField.expected + "].";
        }

        return "";
    }

    private String getFailingExplanation() {
        if ( currentField.operator.equals( "==" ) ) {
            return "[" + factName + "] field [" + currentField.fieldName + "] was [" + currentField.actualResult + "] expected [" + currentField.expected + "].";
        } else {
            return "[" + factName + "] field [" + currentField.fieldName + "] was not expected to be [" + currentField.actualResult + "].";
        }
    }
}
class ResultVerifier {

    private final Map<String, Object> variables     = new HashMap<String, Object>();
    private ParserContext             parserContext = new ParserContext();

    protected ResultVerifier(Object factObject) {
        addVariable( "__fact__",
                     factObject );
    }

    protected void setExpected(Object expected) {
        addVariable( "__expected__",
                     expected );
    }

    private void addVariable(String name,
                             Object object) {
        variables.put( name,
                       object );

        parserContext.addInput( name,
                                object.getClass() );
    }

    protected Boolean isSuccess(VerifyField currentField) {
        CompiledExpression expression = new ExpressionCompiler( "__fact__." + currentField.fieldName + " " + currentField.operator + " __expected__" ).compile( parserContext );

        return (Boolean) MVEL.executeExpression( expression,
                                                 variables );
    }

    protected String getActual(VerifyField currentField) {
        Object actualValue = MVEL.executeExpression( new ExpressionCompiler( "__fact__." + currentField.fieldName ).compile( parserContext ),
                                                     variables );

        return (actualValue != null) ? actualValue.toString() : "";

    }
}
