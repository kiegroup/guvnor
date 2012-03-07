package org.drools.ide.common.server.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.ide.common.client.modeldriven.testing.Fact;
import org.drools.ide.common.client.modeldriven.testing.FactAssignmentField;
import org.drools.ide.common.client.modeldriven.testing.Field;
import org.drools.ide.common.client.modeldriven.testing.FieldData;

import java.security.InvalidParameterException;

public class FieldConverter implements Converter {


    private final XStream xt;

    public FieldConverter(XStream xt) {
        this.xt = xt;
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        context.convertAnother(source, getDefaultConverter());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        reader.moveDown();
        String name = reader.getValue();
        reader.moveUp();

        reader.moveDown();

        if (reader.getNodeName().equals("value")) {
            FieldData fieldData = new FieldData();

            fieldData.setName(name);

            fieldData.setValue(reader.getValue());
            reader.moveUp();

            if (reader.hasMoreChildren()) {
                reader.moveDown();
                fieldData.setNature(Integer.parseInt(reader.getValue()));
                reader.moveUp();
            }

            return fieldData;

        } else if (reader.getNodeName().equals("fact")) {

            FactAssignmentField factAssignmentField = new FactAssignmentField();
            factAssignmentField.setName(name);

            factAssignmentField.setFact((Fact) context.convertAnother(factAssignmentField, Fact.class));
            reader.moveUp();

            return factAssignmentField;
        }

        throw new InvalidParameterException("Unknown Field instance.");
    }

    @Override
    public boolean canConvert(Class type) {
        return Field.class.isAssignableFrom(type);
    }


    private ReflectionConverter getDefaultConverter() {
        return new ReflectionConverter(xt.getMapper(), xt.getReflectionProvider());
    }

}
