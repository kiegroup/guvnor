package org.drools.guvnor.client.asseteditor.drools.factmodel;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FactMetaModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getAnnotations(org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel::annotations;
  }-*/;
  
  private static native void setAnnotations(org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel::annotations = value;
  }-*/;
  
  private static native java.util.List getFields(org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel::fields;
  }-*/;
  
  private static native void setFields(org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel::fields = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel::name = value;
  }-*/;
  
  private static native java.lang.String getSuperType(org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel::superType;
  }-*/;
  
  private static native void setSuperType(org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel::superType = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instance) throws SerializationException {
    setAnnotations(instance, (java.util.List) streamReader.readObject());
    setFields(instance, (java.util.List) streamReader.readObject());
    setName(instance, streamReader.readString());
    setSuperType(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel instance) throws SerializationException {
    streamWriter.writeObject(getAnnotations(instance));
    streamWriter.writeObject(getFields(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getSuperType(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel)object);
  }
  
}
