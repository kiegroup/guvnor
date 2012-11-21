package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionFieldList_FieldSerializer {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ActionFieldList instance) throws SerializationException {
    instance.fieldValues = (org.drools.ide.common.client.modeldriven.brl.ActionFieldValue[]) streamReader.readObject();
    
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ActionFieldList instance) throws SerializationException {
    streamWriter.writeObject(instance.fieldValues);
    
  }
  
}
