package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DependenciesPageRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getDependencyPath(org.drools.guvnor.client.rpc.DependenciesPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.DependenciesPageRow::dependencyPath;
  }-*/;
  
  private static native void setDependencyPath(org.drools.guvnor.client.rpc.DependenciesPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.DependenciesPageRow::dependencyPath = value;
  }-*/;
  
  private static native java.lang.String getDependencyVersion(org.drools.guvnor.client.rpc.DependenciesPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.DependenciesPageRow::dependencyVersion;
  }-*/;
  
  private static native void setDependencyVersion(org.drools.guvnor.client.rpc.DependenciesPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.DependenciesPageRow::dependencyVersion = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.DependenciesPageRow instance) throws SerializationException {
    setDependencyPath(instance, streamReader.readString());
    setDependencyVersion(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.DependenciesPageRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.DependenciesPageRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.DependenciesPageRow instance) throws SerializationException {
    streamWriter.writeString(getDependencyPath(instance));
    streamWriter.writeString(getDependencyVersion(instance));
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.DependenciesPageRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.DependenciesPageRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.DependenciesPageRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.DependenciesPageRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.DependenciesPageRow)object);
  }
  
}
