
package com.google.gwt.core.ext.typeinfo;

import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * Type representing a Java class or interface type.
 */
public class JClassType extends JType implements HasMetaData {                                                  

    public void addImplementedInterface(JClassType intf) {
    }

    public void addMetaData(String tagName, String[] values) {
    }

    public void addModifierBits(int bits) {
    }

    public JConstructor findConstructor(JType[] paramTypes) {
        return null;
    }

    public JField findField(String name) {
        return null;
    }

    public JMethod findMethod(String name, JType[] paramTypes) {
        return null;
    }

    public JClassType findNestedType(String typeName) {
        return null;
    }

    public int getBodyEnd() {
        return 0;
    }

    public int getBodyStart() {
        return 0;
    }

    public JClassType getEnclosingType() {
        return null;
    }

    public JField getField(String name) {
        return null;
    }

    public JField[] getFields() {
        return null;
    }

    public JClassType[] getImplementedInterfaces() {
        return null;
    }

    public String getJNISignature() {
        return null;
    }

    public String[][] getMetaData(String tagName) {
        return null;
    }

    public String[] getMetaDataTags() {
        return null;
    }

    public JMethod getMethod(String name, JType[] paramTypes)
             {
        return null;
    }

    public JMethod[] getMethods() {
        return null;
    }

    public String getJavahSignatureName() {
        return null;
    }

    public String getJsniSignatureName() {
        return null;
    }

    public String getName() {
        return null;
    }

    public JClassType getNestedType(String typeName)  {
        return null;
    }

    public JClassType[] getNestedTypes() {
        return null;
    }

    public TypeOracle getOracle() {
        return null;
    }

    public JMethod[] getOverloads(String name) {
        return null;
    }

    public JMethod[] getOverridableMethods() {
        return null;
    }

    public JPackage getPackage() {
        return null;
    }

    public String getQualifiedSourceName() {
        return null;
    }

    public String getSimpleSourceName() {
        return null;
    }

    public JClassType[] getSubtypes() {
        return null;
    }

    public JClassType getSuperclass() {
        return null;
    }

    public String getTypeHash() throws UnableToCompleteException {
        return null;
    }

    public boolean isAbstract() {
        return false;
    }


    public boolean isAssignableFrom(JClassType possibleSubtype) {
        return false;
    }

    public boolean isAssignableTo(JClassType possibleSupertype) {
        return false;
    }

    public JClassType isClass() {
        return null;
    }

    public boolean isDefaultInstantiable() {
        return false;
    }

    public JClassType isInterface() {
        return null;
    }

    public boolean isLocalType() {
        return false;
    }

    public boolean isMemberType() {
        return false;
    }

    public JParameterizedType isParameterized() {
        // intentional null
        return null;
    }

    public JPrimitiveType isPrimitive() {
        // intentional null
        return null;
    }

    public boolean isPrivate() {
        return false;
    }

    public boolean isProtected() {
        return false;
    }

    public boolean isPublic() {
        return false;
    }

    public boolean isStatic() {
        return false;
    }

    public void setSuperclass(JClassType type) {
    }

    public String toString() {
        return null;
    }

    protected int getModifierBits() {
        return 0;
    }

    void addConstructor(JConstructor ctor) {
    }

    void addField(JField field) {
    }

    void addMethod(JMethod method) {
    }

    void addNestedType(JClassType type) {

    }

    JClassType findNestedTypeImpl(String[] typeName, int index) {
        return null;
    }

    void notifySuperTypes() {
    }

    void removeFromSupertypes() {
    }

    public String getParameterizedQualifiedSourceName() {
        return null;
    }
}
