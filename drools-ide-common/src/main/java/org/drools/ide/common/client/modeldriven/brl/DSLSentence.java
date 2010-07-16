/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ide.common.client.modeldriven.brl;


/**
 * This represents a DSL sentence.
 * @author Michael Neale
 */
public class DSLSentence
    implements
    IPattern,
    IAction {

    public String sentence;


    /**
     * This will strip off any residual "{" stuff...
     */
    public String toString() {
        final char[] chars = this.sentence.toCharArray();
        boolean inBracket = false;
        boolean inBracketAfterColon = false;

        String result = "";
        for ( int i = 0; i < chars.length; i++ ) {
            final char c = chars[i];
            if ( c != '{' && c != '}' && c != ':' && !inBracketAfterColon ) {
                result += c;
            } else if ( c == '{' ) {
                inBracket = true;
            } else if ( c == '}' ) {
                inBracket = false;
                inBracketAfterColon = false;
            } else if ( c == ':' && inBracket ) {
                inBracketAfterColon = true;
            } else if ( c == ':' && !inBracket ) {
                result += c;
            }
        }
        return result.replace( "\\n",
                               "\n" );
    }

    /**
     * This is used by the GUI when adding a sentence to LHS or RHS.
     * @return
     */
    public DSLSentence copy() {
        final DSLSentence newOne = new DSLSentence();
        newOne.sentence = this.sentence;
        return newOne;
    }
}
