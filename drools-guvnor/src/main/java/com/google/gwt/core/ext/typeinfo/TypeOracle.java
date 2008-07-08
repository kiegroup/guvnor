/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.core.ext.typeinfo;


public class TypeOracle {

  static String combine(String[] strings, int startIndex) {
     return null;
  }

  static String[] modifierBitsToNames(int bits) {
   return null;
  }

  public JClassType findType(String name) {
    return null;
  }

  public JClassType findType(String pkgName, String typeName) {
    return null;
  }

  public JClassType getJavaLangObject() {
    return null;
  }

  public JType getParameterizedType(JClassType rawType, JType[] typeArgs) {
    return null;
  }

  public long getReloadCount() {
    return 0;
  }

  public JClassType getType(String name) {
    return null;
  }

  public JClassType getType(String pkgName, String topLevelTypeSimpleName){
    return null;
  }

  public JClassType[] getTypes() {
    return null;
  }

  public void sort(JClassType[] types) {
  }

  public void sort(JField[] fields) {
  }

  public void sort(JMethod[] methods) {
  }

}
