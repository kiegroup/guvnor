package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FromCollectCompositeFactPattern_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.client.modeldriven.brl.IPattern getRightPattern(org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern::rightPattern;
  }-*/;
  
  private static native void setRightPattern(org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern instance, org.drools.ide.common.client.modeldriven.brl.IPattern value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern::rightPattern = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern instance) throws SerializationException {
    setRightPattern(instance, (org.drools.ide.common.client.modeldriven.brl.IPattern) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern instance) throws SerializationException {
    streamWriter.writeObject(getRightPattern(instance));
    
    org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern)object);
  }
  
}
