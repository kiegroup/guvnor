package org.drools.ide.common.server.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.ide.common.client.modeldriven.testing.FactAssignmentField;
import org.drools.ide.common.client.modeldriven.testing.Field;
import org.drools.ide.common.client.modeldriven.testing.FieldData;

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
        Class<?> resultType = getResultType(reader);
        Object result = xt.getReflectionProvider().newInstance(resultType);
        return context.convertAnother(result, resultType, getDefaultConverter());
    }

    @Override
    public boolean canConvert(Class type) {
        return Field.class.isAssignableFrom(type);
    }

    private ReflectionConverter getDefaultConverter() {
        return new ReflectionConverter(xt.getMapper(), xt.getReflectionProvider());
    }

    private Class<?> getResultType(HierarchicalStreamReader reader) {
        if (containsValueNode(reader)) {
            return FieldData.class;
        } else {
            return FactAssignmentField.class;
        }
    }

    public boolean containsValueNode(HierarchicalStreamReader reader) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            if (reader.getNodeName().equals("value")) {
                reader.moveUp();
                return true;
            }

            reader.moveUp();
        }
        return false;
    }
}
