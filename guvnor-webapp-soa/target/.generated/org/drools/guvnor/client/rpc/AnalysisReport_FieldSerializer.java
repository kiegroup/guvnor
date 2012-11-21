package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AnalysisReport_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.AnalysisReport instance) throws SerializationException {
    instance.errors = (org.drools.guvnor.client.rpc.AnalysisReportLine[]) streamReader.readObject();
    instance.factUsages = (org.drools.guvnor.client.rpc.AnalysisFactUsage[]) streamReader.readObject();
    instance.notes = (org.drools.guvnor.client.rpc.AnalysisReportLine[]) streamReader.readObject();
    instance.warnings = (org.drools.guvnor.client.rpc.AnalysisReportLine[]) streamReader.readObject();
    
  }
  
  public static org.drools.guvnor.client.rpc.AnalysisReport instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.AnalysisReport();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.AnalysisReport instance) throws SerializationException {
    streamWriter.writeObject(instance.errors);
    streamWriter.writeObject(instance.factUsages);
    streamWriter.writeObject(instance.notes);
    streamWriter.writeObject(instance.warnings);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.AnalysisReport_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AnalysisReport_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.AnalysisReport)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AnalysisReport_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.AnalysisReport)object);
  }
  
}
