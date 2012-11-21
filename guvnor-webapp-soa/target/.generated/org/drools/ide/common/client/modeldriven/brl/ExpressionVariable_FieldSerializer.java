package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ExpressionVariable_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.client.modeldriven.brl.FactPattern getFact(org.drools.ide.common.client.modeldriven.brl.ExpressionVariable instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionVariable::fact;
  }-*/;
  
  private static native void setFact(org.drools.ide.common.client.modeldriven.brl.ExpressionVariable instance, org.drools.ide.common.client.modeldriven.brl.FactPattern value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionVariable::fact = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ExpressionVariable instance) throws SerializationException {
    setFact(instance, (org.drools.ide.common.client.modeldriven.brl.FactPattern) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.brl.ExpressionPart_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native org.drools.ide.common.client.modeldriven.brl.ExpressionVariable instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @org.drools.ide.common.client.modeldriven.brl.ExpressionVariable::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ExpressionVariable instance) throws SerializationException {
    streamWriter.writeObject(getFact(instance));
    
    org.drools.ide.common.client.modeldriven.brl.ExpressionPart_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ExpressionVariable_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionVariable_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ExpressionVariable)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionVariable_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ExpressionVariable)object);
  }
  
}
