package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class GuidedDecisionTable52_TableFormat_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat instance) throws SerializationException {
    // Enum deserialization is handled via the instantiate method
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat instantiate(SerializationStreamReader streamReader) throws SerializationException {
    int ordinal = streamReader.readInt();
    org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat[] values = org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat.values();
    assert (ordinal >= 0 && ordinal < values.length);
    return values[ordinal];
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat instance) throws SerializationException {
    assert (instance != null);
    streamWriter.writeInt(instance.ordinal());
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52_TableFormat_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52_TableFormat_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52_TableFormat_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat)object);
  }
  
}
