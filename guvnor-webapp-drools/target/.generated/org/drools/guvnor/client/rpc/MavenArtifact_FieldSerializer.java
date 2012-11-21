package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class MavenArtifact_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getArtifact(org.drools.guvnor.client.rpc.MavenArtifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.MavenArtifact::artifact;
  }-*/;
  
  private static native void setArtifact(org.drools.guvnor.client.rpc.MavenArtifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.MavenArtifact::artifact = value;
  }-*/;
  
  private static native java.util.Collection getChild(org.drools.guvnor.client.rpc.MavenArtifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.MavenArtifact::child;
  }-*/;
  
  private static native void setChild(org.drools.guvnor.client.rpc.MavenArtifact instance, java.util.Collection value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.MavenArtifact::child = value;
  }-*/;
  
  private static native java.lang.String getClassifier(org.drools.guvnor.client.rpc.MavenArtifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.MavenArtifact::classifier;
  }-*/;
  
  private static native void setClassifier(org.drools.guvnor.client.rpc.MavenArtifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.MavenArtifact::classifier = value;
  }-*/;
  
  private static native java.lang.String getGroup(org.drools.guvnor.client.rpc.MavenArtifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.MavenArtifact::group;
  }-*/;
  
  private static native void setGroup(org.drools.guvnor.client.rpc.MavenArtifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.MavenArtifact::group = value;
  }-*/;
  
  private static native boolean getNecessaryOnRuntime(org.drools.guvnor.client.rpc.MavenArtifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.MavenArtifact::necessaryOnRuntime;
  }-*/;
  
  private static native void setNecessaryOnRuntime(org.drools.guvnor.client.rpc.MavenArtifact instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.MavenArtifact::necessaryOnRuntime = value;
  }-*/;
  
  private static native java.lang.String getScope(org.drools.guvnor.client.rpc.MavenArtifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.MavenArtifact::scope;
  }-*/;
  
  private static native void setScope(org.drools.guvnor.client.rpc.MavenArtifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.MavenArtifact::scope = value;
  }-*/;
  
  private static native java.lang.String getType(org.drools.guvnor.client.rpc.MavenArtifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.MavenArtifact::type;
  }-*/;
  
  private static native void setType(org.drools.guvnor.client.rpc.MavenArtifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.MavenArtifact::type = value;
  }-*/;
  
  private static native java.lang.String getVersion(org.drools.guvnor.client.rpc.MavenArtifact instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.MavenArtifact::version;
  }-*/;
  
  private static native void setVersion(org.drools.guvnor.client.rpc.MavenArtifact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.MavenArtifact::version = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.MavenArtifact instance) throws SerializationException {
    setArtifact(instance, streamReader.readString());
    setChild(instance, (java.util.Collection) streamReader.readObject());
    setClassifier(instance, streamReader.readString());
    setGroup(instance, streamReader.readString());
    setNecessaryOnRuntime(instance, streamReader.readBoolean());
    setScope(instance, streamReader.readString());
    setType(instance, streamReader.readString());
    setVersion(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.rpc.MavenArtifact instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.MavenArtifact();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.MavenArtifact instance) throws SerializationException {
    streamWriter.writeString(getArtifact(instance));
    streamWriter.writeObject(getChild(instance));
    streamWriter.writeString(getClassifier(instance));
    streamWriter.writeString(getGroup(instance));
    streamWriter.writeBoolean(getNecessaryOnRuntime(instance));
    streamWriter.writeString(getScope(instance));
    streamWriter.writeString(getType(instance));
    streamWriter.writeString(getVersion(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.MavenArtifact_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.MavenArtifact_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.MavenArtifact)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.MavenArtifact_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.MavenArtifact)object);
  }
  
}
