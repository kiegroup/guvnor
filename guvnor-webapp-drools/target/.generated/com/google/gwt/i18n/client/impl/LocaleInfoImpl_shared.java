package com.google.gwt.i18n.client.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import java.util.HashMap;

public class LocaleInfoImpl_shared extends com.google.gwt.i18n.client.impl.LocaleInfoImpl {
  private static native String getLocaleNativeDisplayName(
      JavaScriptObject nativeDisplayNamesNative,String localeName) /*-{
    return nativeDisplayNamesNative[localeName];
  }-*/;
  
  HashMap<String,String> nativeDisplayNamesJava;
  private JavaScriptObject nativeDisplayNamesNative;
  
  @Override
  public String[] getAvailableLocaleNames() {
    return new String[] {
      "default",
      "es_ES",
      "fr_FR",
      "ja_JP",
      "pt_BR",
      "zh_CN",
    };
  }
  
  @Override
  public String getLocaleNativeDisplayName(String localeName) {
    if (GWT.isScript()) {
      if (nativeDisplayNamesNative == null) {
        nativeDisplayNamesNative = loadNativeDisplayNamesNative();
      }
      return getLocaleNativeDisplayName(nativeDisplayNamesNative, localeName);
    } else {
      if (nativeDisplayNamesJava == null) {
        nativeDisplayNamesJava = new HashMap<String, String>();
        nativeDisplayNamesJava.put("es_ES", "español de España");
        nativeDisplayNamesJava.put("fr_FR", "français - France");
        nativeDisplayNamesJava.put("ja_JP", "日本語 - 日本");
        nativeDisplayNamesJava.put("pt_BR", "português do Brasil");
        nativeDisplayNamesJava.put("zh_CN", "中文（简体） - 中国");
      }
      return nativeDisplayNamesJava.get(localeName);
    }
  }
  
  @Override
  public boolean hasAnyRTL() {
    return false;
  }
  
  private native JavaScriptObject loadNativeDisplayNamesNative() /*-{
    return {
      "es_ES": "español de España",
      "fr_FR": "français - France",
      "ja_JP": "日本語 - 日本",
      "pt_BR": "português do Brasil",
      "zh_CN": "中文（简体） - 中国"
    };
  }-*/;
}
