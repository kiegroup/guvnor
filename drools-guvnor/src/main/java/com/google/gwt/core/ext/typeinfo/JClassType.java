package com.google.gwt.core.ext.typeinfo;

import com.google.gwt.core.ext.UnableToCompleteException;

import java.util.HashSet;
import java.util.Set;

/**
 * Type representing a Java class or interface type.
 */
public class JClassType extends JType implements HasMetaData {

    public JClassType(TypeOracle oracle, CompilationUnitProvider cup,
                      JPackage declaringPackage, JClassType enclosingType, boolean isLocalType,
                      String name, int declStart, int declEnd, int bodyStart, int bodyEnd,
                      boolean isInterface) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public void addImplementedInterface(JClassType intf) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public void addMetaData(String tagName, String[] values) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public void addModifierBits(int bits) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JConstructor findConstructor(JType[] paramTypes) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JField findField(String name) {throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JMethod findMethod(String name, JType[] paramTypes) {
       throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JClassType findNestedType(String typeName) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public int getBodyEnd() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public int getBodyStart() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public CompilationUnitProvider getCompilationUnit() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JConstructor getConstructor(JType[] paramTypes)
            throws NotFoundException {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JConstructor[] getConstructors() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JClassType getEnclosingType() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JField getField(String name) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JField[] getFields() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JClassType[] getImplementedInterfaces() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public String getJNISignature() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public String[][] getMetaData(String tagName) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public String[] getMetaDataTags() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JMethod getMethod(String name, JType[] paramTypes)
            throws NotFoundException {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JMethod[] getMethods() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public String getName() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JClassType getNestedType(String typeName) throws NotFoundException {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JClassType[] getNestedTypes() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public TypeOracle getOracle() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JMethod[] getOverloads(String name) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JMethod[] getOverridableMethods() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JPackage getPackage() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public String getQualifiedSourceName() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public String getSimpleSourceName() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JClassType[] getSubtypes() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JClassType getSuperclass() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public String getTypeHash() throws UnableToCompleteException {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public boolean isAbstract() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JArrayType isArray() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public boolean isAssignableFrom(JClassType possibleSubtype) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public boolean isAssignableTo(JClassType possibleSupertype) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JClassType isClass() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public boolean isDefaultInstantiable() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JClassType isInterface() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public boolean isLocalType() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public boolean isMemberType() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JParameterizedType isParameterized() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public JPrimitiveType isPrimitive() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public boolean isPrivate() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public boolean isProtected() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public boolean isPublic() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public boolean isStatic() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public void setSuperclass(JClassType type) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    public String toString() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    protected int getModifierBits() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    void addConstructor(JConstructor ctor) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    void addField(JField field) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    void addMethod(JMethod method) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    void addNestedType(JClassType type) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    JClassType findNestedTypeImpl(String[] typeName, int index) {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    void notifySuperTypes() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }

    void removeFromSupertypes() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }


    public String getParameterizedQualifiedSourceName() {
        throw new RuntimeException("not to be used for JavaScript compilation");
    }
}
