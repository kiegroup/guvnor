package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ExpressionPart_FieldSerializer {
  private static native java.lang.String getClassType(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::classType;
  }-*/;
  
  private static native void setClassType(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::classType = value;
  }-*/;
  
  private static native java.lang.String getGenericType(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::genericType;
  }-*/;
  
  private static native void setGenericType(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::genericType = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::name;
  }-*/;
  
  private static native void setName(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::name = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.brl.ExpressionPart getNext(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::next;
  }-*/;
  
  private static native void setNext(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance, org.drools.ide.common.client.modeldriven.brl.ExpressionPart value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::next = value;
  }-*/;
  
  private static native java.lang.String getParametricType(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::parametricType;
  }-*/;
  
  private static native void setParametricType(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::parametricType = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.brl.ExpressionPart getPrev(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::prev;
  }-*/;
  
  private static native void setPrev(org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance, org.drools.ide.common.client.modeldriven.brl.ExpressionPart value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ExpressionPart::prev = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance) throws SerializationException {
    setClassType(instance, streamReader.readString());
    setGenericType(instance, streamReader.readString());
    setName(instance, streamReader.readString());
    setNext(instance, (org.drools.ide.common.client.modeldriven.brl.ExpressionPart) streamReader.readObject());
    setParametricType(instance, streamReader.readString());
    setPrev(instance, (org.drools.ide.common.client.modeldriven.brl.ExpressionPart) streamReader.readObject());
    
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ExpressionPart instance) throws SerializationException {
    streamWriter.writeString(getClassType(instance));
    streamWriter.writeString(getGenericType(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getNext(instance));
    streamWriter.writeString(getParametricType(instance));
    streamWriter.writeObject(getPrev(instance));
    
  }
  
}
