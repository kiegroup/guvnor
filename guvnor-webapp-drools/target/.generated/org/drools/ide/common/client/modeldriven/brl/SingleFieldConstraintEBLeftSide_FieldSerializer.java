package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SingleFieldConstraintEBLeftSide_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine getExpLeftSide(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide::expLeftSide;
  }-*/;
  
  private static native void setExpLeftSide(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide instance, org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide::expLeftSide = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide instance) throws SerializationException {
    setExpLeftSide(instance, (org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide instance) throws SerializationException {
    streamWriter.writeObject(getExpLeftSide(instance));
    
    org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide)object);
  }
  
}
