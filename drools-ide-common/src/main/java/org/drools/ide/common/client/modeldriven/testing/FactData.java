package org.drools.ide.common.client.modeldriven.testing;

import java.util.ArrayList;
import java.util.List;

public class FactData
    implements
    Fixture {
    private static final long serialVersionUID = 1692174722646380925L;

    /**
     * The type (class)
     */
    public String             type;

    /**
     * The name of the "variable"
     */
    public String             name;

    public List<FieldData>    fieldData        = new ArrayList<FieldData>();

    /**
     * If its a modify, obviously we are modifying existing data in working memory.
     */
    public boolean            isModify;

    public FactData() {
    }

    public FactData(String type,
                    String name,
                    List<FieldData> fieldData,
                    boolean modify) {
        this( type,
              name,
              modify );
        this.fieldData = fieldData;

    }

    public FactData(String type,
                    String name,
                    boolean modify) {

        this.type = type;
        this.name = name;
        this.isModify = modify;
    }
}
