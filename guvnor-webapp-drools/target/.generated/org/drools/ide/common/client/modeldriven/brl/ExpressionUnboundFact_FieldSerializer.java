package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ExpressionUnboundFact_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.client.modeldriven.brl.FactPattern getFact(org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact::fact;
  }-*/;
  
  private static native void setFact(org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact instance, org.drools.ide.common.client.modeldriven.brl.FactPattern value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact::fact = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact instance) throws SerializationException {
    setFact(instance, (org.drools.ide.common.client.modeldriven.brl.FactPattern) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.brl.ExpressionPart_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact instance) throws SerializationException {
    streamWriter.writeObject(getFact(instance));
    
    org.drools.ide.common.client.modeldriven.brl.ExpressionPart_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact)object);
  }
  
}
