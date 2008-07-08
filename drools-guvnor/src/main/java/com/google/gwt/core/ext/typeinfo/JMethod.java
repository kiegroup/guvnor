
package com.google.gwt.core.ext.typeinfo;

public class JMethod {

  private JType returnType;

  public JMethod(JClassType enclosingType, String name, int declStart,
      int declEnd, int bodyStart, int bodyEnd) {

  }

  public JClassType getEnclosingType() {
    return null;
  }

  public String getReadableDeclaration() {
    return null;
  }

  public String getReadableDeclaration(boolean noAccess, boolean noNative,
      boolean noStatic, boolean noFinal, boolean noAbstract) {
    return null;
  }

  public JType getReturnType() {
    return null;
  }

  public boolean isAbstract() {
    return false;
  }

  public JConstructor isConstructor() {
    return null;
  }

  public boolean isFinal() {
    return false;
  }

  public JMethod isMethod() {
    return this;
  }

  public boolean isNative() {
    return false;
  }

  public boolean isStatic() {
    return false;
  }

  public void setReturnType(JType type) {
  }

  public String toString() {
    return getReadableDeclaration();
  }

  String getReadableDeclaration(int modifierBits) {
    return null;
  }
}
