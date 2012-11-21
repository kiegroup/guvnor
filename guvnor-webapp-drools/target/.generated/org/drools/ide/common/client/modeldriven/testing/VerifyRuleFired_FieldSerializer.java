package org.drools.ide.common.client.modeldriven.testing;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class VerifyRuleFired_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.Integer getActualResult(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::actualResult;
  }-*/;
  
  private static native void setActualResult(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance, java.lang.Integer value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::actualResult = value;
  }-*/;
  
  private static native java.lang.Integer getExpectedCount(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::expectedCount;
  }-*/;
  
  private static native void setExpectedCount(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance, java.lang.Integer value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::expectedCount = value;
  }-*/;
  
  private static native java.lang.Boolean getExpectedFire(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::expectedFire;
  }-*/;
  
  private static native void setExpectedFire(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::expectedFire = value;
  }-*/;
  
  private static native java.lang.String getExplanation(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::explanation;
  }-*/;
  
  private static native void setExplanation(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::explanation = value;
  }-*/;
  
  private static native java.lang.String getRuleName(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::ruleName;
  }-*/;
  
  private static native void setRuleName(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::ruleName = value;
  }-*/;
  
  private static native java.lang.Boolean getSuccessResult(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::successResult;
  }-*/;
  
  private static native void setSuccessResult(org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired::successResult = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance) throws SerializationException {
    setActualResult(instance, (java.lang.Integer) streamReader.readObject());
    setExpectedCount(instance, (java.lang.Integer) streamReader.readObject());
    setExpectedFire(instance, (java.lang.Boolean) streamReader.readObject());
    setExplanation(instance, streamReader.readString());
    setRuleName(instance, streamReader.readString());
    setSuccessResult(instance, (java.lang.Boolean) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired instance) throws SerializationException {
    streamWriter.writeObject(getActualResult(instance));
    streamWriter.writeObject(getExpectedCount(instance));
    streamWriter.writeObject(getExpectedFire(instance));
    streamWriter.writeString(getExplanation(instance));
    streamWriter.writeString(getRuleName(instance));
    streamWriter.writeObject(getSuccessResult(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired)object);
  }
  
}
