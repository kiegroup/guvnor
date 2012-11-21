package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class StandaloneEditorInvocationParameters_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.guvnor.client.rpc.Asset[] getActiveTemporalWorkingSets(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::activeTemporalWorkingSets;
  }-*/;
  
  private static native void setActiveTemporalWorkingSets(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance, org.drools.guvnor.client.rpc.Asset[] value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::activeTemporalWorkingSets = value;
  }-*/;
  
  private static native org.drools.guvnor.client.rpc.Asset[] getActiveWorkingSets(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::activeWorkingSets;
  }-*/;
  
  private static native void setActiveWorkingSets(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance, org.drools.guvnor.client.rpc.Asset[] value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::activeWorkingSets = value;
  }-*/;
  
  private static native org.drools.guvnor.client.rpc.Asset[] getAssetsToBeEdited(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::assetsToBeEdited;
  }-*/;
  
  private static native void setAssetsToBeEdited(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance, org.drools.guvnor.client.rpc.Asset[] value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::assetsToBeEdited = value;
  }-*/;
  
  private static native java.lang.String getClientName(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::clientName;
  }-*/;
  
  private static native void setClientName(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::clientName = value;
  }-*/;
  
  private static native boolean getHideAttributes(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::hideAttributes;
  }-*/;
  
  private static native void setHideAttributes(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::hideAttributes = value;
  }-*/;
  
  private static native boolean getHideLHS(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::hideLHS;
  }-*/;
  
  private static native void setHideLHS(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::hideLHS = value;
  }-*/;
  
  private static native boolean getHideRHS(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::hideRHS;
  }-*/;
  
  private static native void setHideRHS(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::hideRHS = value;
  }-*/;
  
  private static native boolean getTemporalAssets(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::temporalAssets;
  }-*/;
  
  private static native void setTemporalAssets(org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters::temporalAssets = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance) throws SerializationException {
    setActiveTemporalWorkingSets(instance, (org.drools.guvnor.client.rpc.Asset[]) streamReader.readObject());
    setActiveWorkingSets(instance, (org.drools.guvnor.client.rpc.Asset[]) streamReader.readObject());
    setAssetsToBeEdited(instance, (org.drools.guvnor.client.rpc.Asset[]) streamReader.readObject());
    setClientName(instance, streamReader.readString());
    setHideAttributes(instance, streamReader.readBoolean());
    setHideLHS(instance, streamReader.readBoolean());
    setHideRHS(instance, streamReader.readBoolean());
    setTemporalAssets(instance, streamReader.readBoolean());
    
  }
  
  public static org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters instance) throws SerializationException {
    streamWriter.writeObject(getActiveTemporalWorkingSets(instance));
    streamWriter.writeObject(getActiveWorkingSets(instance));
    streamWriter.writeObject(getAssetsToBeEdited(instance));
    streamWriter.writeString(getClientName(instance));
    streamWriter.writeBoolean(getHideAttributes(instance));
    streamWriter.writeBoolean(getHideLHS(instance));
    streamWriter.writeBoolean(getHideRHS(instance));
    streamWriter.writeBoolean(getTemporalAssets(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.StandaloneEditorInvocationParameters)object);
  }
  
}
