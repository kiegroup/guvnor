package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ExpressionField_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ExpressionField instance) throws SerializationException {
    
    org.drools.ide.common.client.modeldriven.brl.ExpressionPart_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native org.drools.ide.common.client.modeldriven.brl.ExpressionField instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @org.drools.ide.common.client.modeldriven.brl.ExpressionField::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ExpressionField instance) throws SerializationException {
    
    org.drools.ide.common.client.modeldriven.brl.ExpressionPart_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ExpressionField_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionField_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ExpressionField)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionField_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ExpressionField)object);
  }
  
}
