package org.drools.guvnor.client.ruleeditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

import org.drools.guvnor.client.factcontraints.Constraint;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ConstraintEditor extends Composite {
	private Constants constants =  GWT.create(Constants.class);
	private Constraint constraint;

	public ConstraintEditor(Constraint constraint) {
		this.constraint = constraint;
		
		Grid confGrid = new Grid(constraint.getArgumentKeys().size(), 2);
		
		ArrayList<String> list = new ArrayList<String>();
		Map<String, String> argI18N = new HashMap<String, String>();
		for (String arg : constraint.getArgumentKeys()) {
			String i18n = getI18NText(arg);
			list.add(i18n);
			argI18N.put(i18n, arg);
		}
		Collections.sort(list);
		
		int row = 0;
		for (String arg : list) {
			TextBox argTB = new TextBox();
			argTB.setText(getConstraint().getArgumentValue(arg).toString());
			argTB.setName(argI18N.get(arg));
			argTB.setTitle(arg);
			argTB.addChangeListener(new ChangeListener() {
				public void onChange(Widget sender) {
					TextBox argTB = (TextBox) sender;
					getConstraint().setArgumentValue(argTB.getName(), argTB.getText());
				}
			});
			
			confGrid.setWidget(row, 0, new Label(arg + ":"));
			confGrid.setWidget(row, 1, argTB);
			row++;
		}
		
		initWidget(confGrid);
	}
	
	private String getI18NText(String s) {
		try {
			return constants.getString("constraint." + getConstraintName() + "." + s);
		} catch (MissingResourceException e) {
			return s;
		}
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	
	public String getConstraintName() {
		return getConstraint().getConstraintName();
	}
 }
