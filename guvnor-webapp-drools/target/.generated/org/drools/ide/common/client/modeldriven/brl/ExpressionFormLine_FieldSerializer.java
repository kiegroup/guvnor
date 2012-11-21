package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ExpressionFormLine_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getBinding(org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine::binding;
  }-*/;
  
  private static native void setBinding(org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine::binding = value;
  }-*/;
  
  private static native java.util.LinkedList getParts(org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine::parts;
  }-*/;
  
  private static native void setParts(org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine instance, java.util.LinkedList value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine::parts = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine instance) throws SerializationException {
    setBinding(instance, streamReader.readString());
    setParts(instance, (java.util.LinkedList) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine instance) throws SerializationException {
    streamWriter.writeString(getBinding(instance));
    streamWriter.writeObject(getParts(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine)object);
  }
  
}
