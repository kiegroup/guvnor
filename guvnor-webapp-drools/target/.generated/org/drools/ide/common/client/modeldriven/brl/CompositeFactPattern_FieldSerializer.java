package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class CompositeFactPattern_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.client.modeldriven.brl.IFactPattern[] getPatterns(org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern::patterns;
  }-*/;
  
  private static native void setPatterns(org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern instance, org.drools.ide.common.client.modeldriven.brl.IFactPattern[] value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern::patterns = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern instance) throws SerializationException {
    setPatterns(instance, (org.drools.ide.common.client.modeldriven.brl.IFactPattern[]) streamReader.readObject());
    instance.type = streamReader.readString();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern instance) throws SerializationException {
    streamWriter.writeObject(getPatterns(instance));
    streamWriter.writeString(instance.type);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern)object);
  }
  
}
