package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FromAccumulateCompositeFactPattern_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getActionCode(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::actionCode;
  }-*/;
  
  private static native void setActionCode(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::actionCode = value;
  }-*/;
  
  private static native java.lang.String getFunction(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::function;
  }-*/;
  
  private static native void setFunction(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::function = value;
  }-*/;
  
  private static native java.lang.String getInitCode(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::initCode;
  }-*/;
  
  private static native void setInitCode(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::initCode = value;
  }-*/;
  
  private static native java.lang.String getResultCode(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::resultCode;
  }-*/;
  
  private static native void setResultCode(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::resultCode = value;
  }-*/;
  
  private static native java.lang.String getReverseCode(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::reverseCode;
  }-*/;
  
  private static native void setReverseCode(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::reverseCode = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.brl.IPattern getSourcePattern(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::sourcePattern;
  }-*/;
  
  private static native void setSourcePattern(org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance, org.drools.ide.common.client.modeldriven.brl.IPattern value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern::sourcePattern = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance) throws SerializationException {
    setActionCode(instance, streamReader.readString());
    setFunction(instance, streamReader.readString());
    setInitCode(instance, streamReader.readString());
    setResultCode(instance, streamReader.readString());
    setReverseCode(instance, streamReader.readString());
    setSourcePattern(instance, (org.drools.ide.common.client.modeldriven.brl.IPattern) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern instance) throws SerializationException {
    streamWriter.writeString(getActionCode(instance));
    streamWriter.writeString(getFunction(instance));
    streamWriter.writeString(getInitCode(instance));
    streamWriter.writeString(getResultCode(instance));
    streamWriter.writeString(getReverseCode(instance));
    streamWriter.writeObject(getSourcePattern(instance));
    
    org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern)object);
  }
  
}
