package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class NewGuidedDecisionTableAssetConfiguration_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat getTableFormat(org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration::tableFormat;
  }-*/;
  
  private static native void setTableFormat(org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration instance, org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration::tableFormat = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration instance) throws SerializationException {
    setTableFormat(instance, (org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat) streamReader.readObject());
    
    org.drools.guvnor.client.rpc.NewAssetConfiguration_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration instance) throws SerializationException {
    streamWriter.writeObject(getTableFormat(instance));
    
    org.drools.guvnor.client.rpc.NewAssetConfiguration_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration)object);
  }
  
}
