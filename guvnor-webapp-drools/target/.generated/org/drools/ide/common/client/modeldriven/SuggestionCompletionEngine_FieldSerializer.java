package org.drools.ide.common.client.modeldriven;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SuggestionCompletionEngine_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.Map getAccessorsAndMutators(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::accessorsAndMutators;
  }-*/;
  
  private static native void setAccessorsAndMutators(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::accessorsAndMutators = value;
  }-*/;
  
  private static native java.util.Map getAnnotationsForTypes(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::annotationsForTypes;
  }-*/;
  
  private static native void setAnnotationsForTypes(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::annotationsForTypes = value;
  }-*/;
  
  private static native java.util.Map getDataEnumLists(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::dataEnumLists;
  }-*/;
  
  private static native void setDataEnumLists(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::dataEnumLists = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.FactTypeFilter getFactFilter(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::factFilter;
  }-*/;
  
  private static native void setFactFilter(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, org.drools.ide.common.client.modeldriven.FactTypeFilter value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::factFilter = value;
  }-*/;
  
  private static native java.util.Map getFieldParametersType(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::fieldParametersType;
  }-*/;
  
  private static native void setFieldParametersType(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::fieldParametersType = value;
  }-*/;
  
  private static native java.util.Map getFilterModelFields(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::filterModelFields;
  }-*/;
  
  private static native void setFilterModelFields(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::filterModelFields = value;
  }-*/;
  
  private static native boolean getFilteringFacts(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::filteringFacts;
  }-*/;
  
  private static native void setFilteringFacts(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::filteringFacts = value;
  }-*/;
  
  private static native java.lang.String[] getGlobalCollections(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::globalCollections;
  }-*/;
  
  private static native void setGlobalCollections(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, java.lang.String[] value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::globalCollections = value;
  }-*/;
  
  private static native java.util.Map getGlobalTypes(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::globalTypes;
  }-*/;
  
  private static native void setGlobalTypes(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::globalTypes = value;
  }-*/;
  
  private static native java.util.Map getMethodInfos(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::methodInfos;
  }-*/;
  
  private static native void setMethodInfos(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::methodInfos = value;
  }-*/;
  
  private static native java.util.Map getModelFields(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::modelFields;
  }-*/;
  
  private static native void setModelFields(org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine::modelFields = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) throws SerializationException {
    setAccessorsAndMutators(instance, (java.util.Map) streamReader.readObject());
    instance.actionDSLSentences = (org.drools.ide.common.client.modeldriven.brl.DSLSentence[]) streamReader.readObject();
    setAnnotationsForTypes(instance, (java.util.Map) streamReader.readObject());
    instance.anyScopeDSLItems = (org.drools.ide.common.client.modeldriven.brl.DSLSentence[]) streamReader.readObject();
    instance.conditionDSLSentences = (org.drools.ide.common.client.modeldriven.brl.DSLSentence[]) streamReader.readObject();
    setDataEnumLists(instance, (java.util.Map) streamReader.readObject());
    setFactFilter(instance, (org.drools.ide.common.client.modeldriven.FactTypeFilter) streamReader.readObject());
    setFieldParametersType(instance, (java.util.Map) streamReader.readObject());
    setFilterModelFields(instance, (java.util.Map) streamReader.readObject());
    setFilteringFacts(instance, streamReader.readBoolean());
    setGlobalCollections(instance, (java.lang.String[]) streamReader.readObject());
    setGlobalTypes(instance, (java.util.Map) streamReader.readObject());
    instance.keywordDSLItems = (org.drools.ide.common.client.modeldriven.brl.DSLSentence[]) streamReader.readObject();
    setMethodInfos(instance, (java.util.Map) streamReader.readObject());
    setModelFields(instance, (java.util.Map) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine instance) throws SerializationException {
    streamWriter.writeObject(getAccessorsAndMutators(instance));
    streamWriter.writeObject(instance.actionDSLSentences);
    streamWriter.writeObject(getAnnotationsForTypes(instance));
    streamWriter.writeObject(instance.anyScopeDSLItems);
    streamWriter.writeObject(instance.conditionDSLSentences);
    streamWriter.writeObject(getDataEnumLists(instance));
    streamWriter.writeObject(getFactFilter(instance));
    streamWriter.writeObject(getFieldParametersType(instance));
    streamWriter.writeObject(getFilterModelFields(instance));
    streamWriter.writeBoolean(getFilteringFacts(instance));
    streamWriter.writeObject(getGlobalCollections(instance));
    streamWriter.writeObject(getGlobalTypes(instance));
    streamWriter.writeObject(instance.keywordDSLItems);
    streamWriter.writeObject(getMethodInfos(instance));
    streamWriter.writeObject(getModelFields(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine)object);
  }
  
}
