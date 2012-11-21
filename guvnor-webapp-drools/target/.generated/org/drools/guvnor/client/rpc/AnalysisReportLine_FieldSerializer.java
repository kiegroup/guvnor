package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AnalysisReportLine_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.AnalysisReportLine instance) throws SerializationException {
    instance.causes = (org.drools.guvnor.client.rpc.Cause[]) streamReader.readObject();
    instance.description = streamReader.readString();
    instance.impactedRules = (java.util.Map) streamReader.readObject();
    instance.patternOrderNumber = (java.lang.Integer) streamReader.readObject();
    instance.reason = streamReader.readString();
    
  }
  
  public static org.drools.guvnor.client.rpc.AnalysisReportLine instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.AnalysisReportLine();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.AnalysisReportLine instance) throws SerializationException {
    streamWriter.writeObject(instance.causes);
    streamWriter.writeString(instance.description);
    streamWriter.writeObject(instance.impactedRules);
    streamWriter.writeObject(instance.patternOrderNumber);
    streamWriter.writeString(instance.reason);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.AnalysisReportLine_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AnalysisReportLine_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.AnalysisReportLine)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AnalysisReportLine_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.AnalysisReportLine)object);
  }
  
}
