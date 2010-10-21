/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ide.common.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionRetractFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.RuleAttribute;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

/**
 * This takes care of converting GuidedDT object to DRL (via the RuleModel).
 * @author Michael Neale
 *
 */
public class GuidedDTDRLPersistence {

	public static GuidedDTDRLPersistence getInstance() {
		return new GuidedDTDRLPersistence();
	}

	public String marshal(GuidedDecisionTable dt) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < dt.getData().length; i++) {
			String[] row = dt.getData()[i];
			String num = row[0];
			String desc = row[1];

			RuleModel rm = new RuleModel();
			rm.name = getName(dt.getTableName(), num);

			doMetadata(dt.getMetadataCols(), row, rm);
			doAttribs(dt.getMetadataCols().size(), dt.getAttributeCols(), row, rm);
			doConditions(dt.getMetadataCols().size() + dt.getAttributeCols().size(), dt.getConditionCols(), row, rm);
			doActions(dt.getMetadataCols().size() + dt.getAttributeCols().size() + dt.getConditionCols().size(), dt.getActionCols(), row, rm);

			if(dt.getParentName() != null){
				rm.parentName = dt.getParentName();
			}

			sb.append("#from row number: " + (i + 1) + "\n");
			String rule = BRDRLPersistence.getInstance().marshal(rm);
			sb.append(rule);
			sb.append("\n");
		}


		return sb.toString();

	}

	void doActions(int condAndAttrs, List<ActionCol> actionCols, String[] row, RuleModel rm) {
		List<LabelledAction> actions = new ArrayList<LabelledAction>();
		for (int i = 0; i < actionCols.size(); i++) {
			ActionCol c = actionCols.get(i);
			String cell = row[condAndAttrs + i + GuidedDecisionTable.INTERNAL_ELEMENTS];
            if (!validCell(cell)) {
                cell = c.getDefaultValue();
            }
			if (validCell(cell)) {
				if (c instanceof ActionInsertFactCol) {
					ActionInsertFactCol ac = (ActionInsertFactCol)c;
					LabelledAction a = findByLabelledAction(actions, ac.getBoundName());
					if (a == null) {
						a = new LabelledAction();
						a.boundName  = ac.getBoundName();
						ActionInsertFact ins = new ActionInsertFact(ac.getFactType());
						ins.setBoundName( ac.getBoundName() );
						a.action = ins;
						actions.add(a);
					}
					ActionInsertFact ins = (ActionInsertFact) a.action;
					ActionFieldValue val = new ActionFieldValue(ac.getFactField(), cell, ac.getType());
					ins.addFieldValue(val);
				} else if (c instanceof ActionRetractFactCol) {
					ActionRetractFactCol rf = (ActionRetractFactCol)c;
					LabelledAction a = findByLabelledAction(actions, rf.boundName);
					if (a == null) {
						a = new LabelledAction();
						a.action = new ActionRetractFact(rf.boundName);
						a.boundName = rf.boundName;
						actions.add(a);
					}
				} else if (c instanceof ActionSetFieldCol) {
					ActionSetFieldCol sf = (ActionSetFieldCol)c;
					LabelledAction a = findByLabelledAction(actions, sf.getBoundName());
					if (a == null) {
						a = new LabelledAction();
						a.boundName = sf.getBoundName();
						if (!sf.isUpdate()) {
							a.action = new ActionSetField(sf.getBoundName());
						} else {
							a.action = new ActionUpdateField(sf.getBoundName());
						}
						actions.add(a);
					} else if (sf.isUpdate() && !(a.action instanceof ActionUpdateField)) {
						//lets swap it out for an update as the user has asked for it.
						ActionSetField old = (ActionSetField) a.action;
						ActionUpdateField update = new ActionUpdateField(sf.getBoundName());
						update.fieldValues = old.fieldValues;
						a.action = update;
					}
					ActionSetField asf = (ActionSetField) a.action;
					ActionFieldValue val = new ActionFieldValue(sf.getFactField(), cell, sf.getType());
					asf.addFieldValue(val);
				}
			}
		}

		rm.rhs = new IAction[actions.size()];
		for (int i = 0; i < rm.rhs.length; i++) {
			rm.rhs[i] = actions.get(i).action;
		}
	}

	private LabelledAction findByLabelledAction(List<LabelledAction> actions, String boundName) {
		for (LabelledAction labelledAction : actions) {
			if (labelledAction.boundName.equals(boundName)) {
				return labelledAction;
			}
		}
		return null;
	}

	void doConditions(int numOfAttributesAndMeta, List<ConditionCol> conditionCols, String[] row, RuleModel rm) {

		List<FactPattern> patterns = new ArrayList<FactPattern>();

		for (int i = 0; i < conditionCols.size(); i++) {
			ConditionCol c = (ConditionCol) conditionCols.get(i);
			String cell = row[i + GuidedDecisionTable.INTERNAL_ELEMENTS + numOfAttributesAndMeta];

            if (!validCell(cell)) {
                //try default value
                cell = c.getDefaultValue();
            }
            
			if (validCell(cell)) {

				//get or create the pattern it belongs too
				FactPattern fp = findByFactPattern(patterns, c.getBoundName());
				if (fp == null) {
					fp = new FactPattern(c.getFactType());
					fp.boundName = c.getBoundName();
					patterns.add(fp);
				}



				//now add the constraint from this cell
				switch (c.getConstraintValueType()) {
					case BaseSingleFieldConstraint.TYPE_LITERAL:
					case BaseSingleFieldConstraint.TYPE_RET_VALUE:
						SingleFieldConstraint sfc = new SingleFieldConstraint(c.getFactField());
						if (no(c.getOperator())) {

							String[] a = cell.split("\\s");
							if (a.length > 1) {
								sfc.setOperator(a[0]);
								sfc.setValue(a[1]);
							} else {
								sfc.setValue(cell);
							}
						} else {
							sfc.setOperator(c.getOperator());
                            if (c.getOperator().equals("in")) {
                                sfc.setValue(makeInList(cell));
                            } else {
                                sfc.setValue(cell);
                            }

						}
						sfc.setConstraintValueType(c.getConstraintValueType());
						fp.addConstraint(sfc);
						break;
					case BaseSingleFieldConstraint.TYPE_PREDICATE:
						SingleFieldConstraint pred = new SingleFieldConstraint();
						pred.setConstraintValueType(c.getConstraintValueType());
                        if (c.getFactField() != null && c.getFactField().indexOf("$param") > -1) {
                            //handle interpolation
                            pred.setValue(c.getFactField().replace("$param", cell));  
                        } else {
						    pred.setValue(cell);
                        }
						fp.addConstraint(pred);
						break;
				default:
					throw new IllegalArgumentException("Unknown constraintValueType: " + c.getConstraintValueType());
				}
			}
		}
		rm.lhs = patterns.toArray(new IPattern[patterns.size()]);
	}

    /**
     * take a CSV list and turn it into DRL syntax
     */
    String makeInList(String cell) {
        if (cell.startsWith("(")) return cell;
        String res = "";
        StringTokenizer st = new StringTokenizer(cell, ",");
        while (st.hasMoreTokens()) {
            String t = st.nextToken().trim();
            if (t.startsWith("\"")) {
                res += t;
            } else {
                res += "\"" + t + "\"";
            }
            if (st.hasMoreTokens()) res += ", ";
        }
        return "(" + res + ")";
    }


    private boolean no(String operator) {
		return operator == null || "".equals(operator);
	}

	private FactPattern findByFactPattern(List<FactPattern> patterns, String boundName) {
		for (FactPattern factPattern : patterns) {
			if (factPattern.boundName.equals(boundName)) {
				return factPattern;
			}
		}
		return null;
	}

	void doAttribs(int numOfMeta, List<AttributeCol> attributeCols, String[] row, RuleModel rm) {
		List<RuleAttribute> attribs = new ArrayList<RuleAttribute>();
		for (int j = 0; j < attributeCols.size(); j++) {
			AttributeCol at = attributeCols.get(j);
			String cell = row[j + GuidedDecisionTable.INTERNAL_ELEMENTS + numOfMeta];
			if (validCell(cell)) {
				attribs.add(new RuleAttribute(at.attr, cell));
			} else if (at.getDefaultValue() != null) {
                attribs.add(new RuleAttribute(at.attr, at.getDefaultValue()));                
            }
		}
		if (attribs.size() > 0) {
			rm.attributes = attribs.toArray(new RuleAttribute[attribs.size()]);
		}
	}

	void doMetadata(List<MetadataCol> metadataCols, String[] row, RuleModel rm) {

		// setup temp list
		List<RuleMetadata> metadataList = new ArrayList<RuleMetadata>();

		for (int j = 0; j < metadataCols.size(); j++) {
			MetadataCol meta = metadataCols.get(j);
			String cell = row[j + GuidedDecisionTable.INTERNAL_ELEMENTS];
			if (validCell(cell)) {
				metadataList.add(new RuleMetadata(meta.attr, cell));
			}
		}
		if (metadataList.size() > 0) {
			rm.metadataList = metadataList.toArray(new RuleMetadata[metadataList.size()]);
		}
	}

	String getName(String tableName, String num) {
		return "Row " + num + " " + tableName;
	}

	boolean validCell(String c) {
		return (c != null) && (!c.trim().equals(""));
	}

	private class LabelledAction {
		String boundName;
		IAction action;
	}

}
