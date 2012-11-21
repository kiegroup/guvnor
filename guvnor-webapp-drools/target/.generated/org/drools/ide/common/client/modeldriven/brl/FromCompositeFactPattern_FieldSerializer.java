package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FromCompositeFactPattern_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine getExpression(org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern::expression;
  }-*/;
  
  private static native void setExpression(org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern instance, org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern::expression = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern instance) throws SerializationException {
    setExpression(instance, (org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine) streamReader.readObject());
    instance.factPattern = (org.drools.ide.common.client.modeldriven.brl.FactPattern) streamReader.readObject();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern instance) throws SerializationException {
    streamWriter.writeObject(getExpression(instance));
    streamWriter.writeObject(instance.factPattern);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern)object);
  }
  
}
