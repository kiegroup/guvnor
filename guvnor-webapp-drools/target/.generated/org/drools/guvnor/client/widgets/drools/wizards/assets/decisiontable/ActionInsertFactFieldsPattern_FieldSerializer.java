package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionInsertFactFieldsPattern_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getIsInsertedLogically(org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern instance) /*-{
    return instance.@org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern::isInsertedLogically;
  }-*/;
  
  private static native void setIsInsertedLogically(org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern::isInsertedLogically = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern instance) throws SerializationException {
    setIsInsertedLogically(instance, streamReader.readBoolean());
    
    org.drools.ide.common.client.modeldriven.dt52.Pattern52_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern instance) throws SerializationException {
    streamWriter.writeBoolean(getIsInsertedLogically(instance));
    
    org.drools.ide.common.client.modeldriven.dt52.Pattern52_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern_FieldSerializer.serialize(writer, (org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.ActionInsertFactFieldsPattern)object);
  }
  
}
