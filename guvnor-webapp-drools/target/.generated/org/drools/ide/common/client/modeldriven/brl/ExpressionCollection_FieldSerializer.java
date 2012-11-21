package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ExpressionCollection_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ExpressionCollection instance) throws SerializationException {
    
    org.drools.ide.common.client.modeldriven.brl.ExpressionPart_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native org.drools.ide.common.client.modeldriven.brl.ExpressionCollection instantiate(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @org.drools.ide.common.client.modeldriven.brl.ExpressionCollection::new()();
  }-*/;
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ExpressionCollection instance) throws SerializationException {
    
    org.drools.ide.common.client.modeldriven.brl.ExpressionPart_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ExpressionCollection_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionCollection_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ExpressionCollection)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ExpressionCollection_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ExpressionCollection)object);
  }
  
}
