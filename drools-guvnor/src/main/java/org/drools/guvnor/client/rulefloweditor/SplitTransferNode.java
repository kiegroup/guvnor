package org.drools.guvnor.client.rulefloweditor;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.rulefloweditor.SplitNode.ConnectionRef;
import org.drools.guvnor.client.rulefloweditor.SplitNode.Constraint;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SplitTransferNode extends TransferNode
    implements
    IsSerializable {

    private Map<ConnectionRef, Constraint> constraints = new HashMap<ConnectionRef, Constraint>();
    private Type                           splitType;

    public enum Type
            implements IsSerializable {
        UNDEFINED(
                0), AND(
                1), XOR(
                2), OR(
                3);

        private final int value;

        Type(int i) {
            value = i;
        }

        int getValue() {
            return value;
        }

        public static Type getType(int i) {
            switch ( i ) {
                case 0 :
                    return Type.UNDEFINED;
                case 1 :
                    return Type.AND;
                case 2 :
                    return Type.XOR;
                case 3 :
                default :
                    return Type.OR;
            }
        }
    }

    public void setConstraints(Map<ConnectionRef, Constraint> constraints) {
        this.constraints = constraints;
    }

    public Map<ConnectionRef, Constraint> getConstraints() {
        return constraints;
    }

    public void setSplitType(Type splitType) {
        this.splitType = splitType;
    }

    public Type getSplitType() {
        return splitType;
    }
}
