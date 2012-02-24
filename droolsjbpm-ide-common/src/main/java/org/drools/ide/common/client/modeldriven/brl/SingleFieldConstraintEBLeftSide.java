/*
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

public class SingleFieldConstraintEBLeftSide extends SingleFieldConstraint {

    public SingleFieldConstraintEBLeftSide() {
        super();
    }

    public SingleFieldConstraintEBLeftSide(String factType,
                                           String fieldName,
                                           String fieldType,
                                           FieldConstraint parent) {
        super( factType,
               fieldName,
               fieldType,
               parent );
    }

    public SingleFieldConstraintEBLeftSide(String field) {
        super( field );
    }

    private ExpressionFormLine expLeftSide = new ExpressionFormLine();

    /**
     * Returns true of there is a field binding.
     */
    public boolean isBound() {
        return expLeftSide != null && expLeftSide.isBound();
    }

    public ExpressionFormLine getExpressionLeftSide() {
        return expLeftSide;
    }

    public void setExpressionLeftSide(ExpressionFormLine expression) {
        this.expLeftSide = expression;
    }

    @Override
    public String getFieldBinding() {
        return getExpressionLeftSide().getBinding();
    }

    @Override
    public void setFieldBinding(String fieldBinding) {
        getExpressionLeftSide().setBinding( fieldBinding );
    }

    @Override
    public String getFieldType() {
        return getExpressionLeftSide().getClassType();
    }

    @Override
    public String getFieldName() {
        return getExpressionLeftSide().getFieldName();
    }

    /**
     * This adds a new connective.
     */
    @Override
    public void addNewConnective() {

        String fieldName = getExpressionLeftSide().getFieldName();
        String fieldType = getExpressionLeftSide().getGenericType();
        String factType = getExpressionLeftSide().getPreviousClassType();
        if ( factType == null ) {
            factType = getExpressionLeftSide().getClassType();
        }

        if ( this.connectives == null ) {
            this.connectives = new ConnectiveConstraint[]{new ConnectiveConstraint( factType,
                                                                                    fieldName,
                                                                                    fieldType )};
        } else {
            final ConnectiveConstraint[] newList = new ConnectiveConstraint[this.connectives.length + 1];
            for ( int i = 0; i < this.connectives.length; i++ ) {
                newList[i] = this.connectives[i];
            }
            newList[this.connectives.length] = new ConnectiveConstraint( factType,
                                                                         fieldName,
                                                                         fieldType );
            this.connectives = newList;
        }
    }

}
