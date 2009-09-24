package bpel.editor.gridcc.controller
{
	import bpel.editor.gridcc.data.*;
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.utility.ActivityDOSearch;
	
	public class BPELLoader
	{
		/**
         * The Singleton instance of BPELLoader
         */        
        protected static var instance:BPELLoader;        
        
        public var processDO:ProcessDO;        
        public var partnerLinks:PartnerLinksDO;        
        public var variables:VariablesDO;       
        
		/**
         * Constructor
         * 
         * Instantiates Singleton instance of BPELLoader
         */        
        public function BPELLoader()
        {
            if (BPELLoader.instance == null) 
            {                
                BPELLoader.instance = this;                 
            }            
        }
        
        /**
         * Determines if the singleton instance of BPELLoader
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of BPELLoader
         */        
        public static function getInstance():BPELLoader
        {
            if (instance == null) 
            {
               instance = new BPELLoader();   
            }            
            return instance;
        }
        
        public function parseWorkflow(workflowInstance:XML):void{        	
        	//var processArray:Array = new Array();        	
        	
        	var attNamesList:XMLList = workflowInstance.@*;        	
        	var nsArray:Array = workflowInstance.namespaceDeclarations(); 
        	
        	createProcessDO(attNamesList, nsArray);        	
			parseElements(workflowInstance, processDO);			
		}		
	
	
		private function createProcessDO(attributesList:XMLList, nsArray:Array):void{
			processDO = ProcessDO.getInstance();
			var porceessName:String = processDO.populateAttributes(attributesList);
			processDO.populateNS(nsArray);						
		}
	
		private function parseElements(node:XML, parentDO:DOInterface):void{		
						
			for each( var element:XML in node.elements() ){					
				//trace("element.localName(): " + element.localName());
				switch(element.localName()){
																		
					case WorkflowActivities.PARTNERLINKS:
						partnerLinks = PartnerLinksDO.getInstance();
						processDO.updateSubActivitiesArray(WorkflowActivities.PARTNERLINKS, partnerLinks);
						if(element.children().length() > 0){
							parseElements(element, partnerLinks);
						}							
						break;
							
					case WorkflowActivities.PARTNERLINK:
						var partnerLinkDO:PartnerLinkDO = new PartnerLinkDO();
						var partnerLinkAttributesList:XMLList = element.@*;
						partnerLinkDO.populateAttributes(partnerLinkAttributesList);
						partnerLinks.updateSubActivitiesArray(WorkflowActivities.PARTNERLINK, partnerLinkDO);
						break;
						
					case WorkflowActivities.VARIABLES:
						variables = VariablesDO.getInstance(); 
						processDO.updateSubActivitiesArray(WorkflowActivities.VARIABLES, variables);
						if(element.children().length() > 0){
							parseElements(element, variables);
						}							
						break;
							
					case WorkflowActivities.VARIABLE:
						var variableDO:VariableDO = new VariableDO(); 
						var variableAttributesList:XMLList = element.@*;
						variableDO.populateAttributes(variableAttributesList);
						parentDO.updateSubActivitiesArray(WorkflowActivities.VARIABLE, variableDO);
						break;
					
					case WorkflowActivities.EMPTY:
						var emptyDO:EmptyDO = new EmptyDO(); 
						var emptyAttributesList:XMLList = element.@*;
						emptyDO.populateAttributes(emptyAttributesList);
						parentDO.updateSubActivitiesArray(WorkflowActivities.EMPTY, emptyDO);
						break;
					
					case WorkflowActivities.TERMINATE:
						var terminateDO:TerminateDO = new TerminateDO(); 
						var terminateAttributesList:XMLList = element.@*;
						terminateDO.populateAttributes(terminateAttributesList);
						parentDO.updateSubActivitiesArray(WorkflowActivities.TERMINATE, terminateDO);
						break;
						
					case WorkflowActivities.SEQUENCE:
						var sequenceDO:SequenceDO = new SequenceDO();
						var sequenceAttributesList:XMLList = element.@*;	
						sequenceDO.populateAttributes(sequenceAttributesList);
						//sequenceDO.printArray();
						parentDO.updateSubActivitiesArray(WorkflowActivities.SEQUENCE, sequenceDO);
						if(element.children().length() > 0){
							parseElements(element, sequenceDO);
						}
						break;
						
					case WorkflowActivities.SCOPE:
						var scopeDO:ScopeDO = new ScopeDO();
						var scopeAttributesList:XMLList = element.@*;	
						scopeDO.populateAttributes(scopeAttributesList);
						//sequenceDO.printArray();
						parentDO.updateSubActivitiesArray(WorkflowActivities.SCOPE, scopeDO);
						if(element.children().length() > 0){
							parseElements(element, scopeDO);
						}
						break;
					
					case WorkflowActivities.SWITCH:
						var switchDO:SwitchDO = new SwitchDO();
						var switchAttributesList:XMLList = element.@*;	
						switchDO.populateAttributes(switchAttributesList);
						//sequenceDO.printArray();
						parentDO.updateSubActivitiesArray(WorkflowActivities.SWITCH, switchDO);
						if(element.children().length() > 0){
							parseElements(element, switchDO);
						}
						break;
					
					case WorkflowActivities.CASE:
						var caseDO:CaseDO = new CaseDO();
						var caseAttributesList:XMLList = element.@*;	
						caseDO.populateAttributes(caseAttributesList);
						//sequenceDO.printArray();
						parentDO.updateSubActivitiesArray(WorkflowActivities.CASE, caseDO);
						if(element.children().length() > 0){
							parseElements(element, caseDO);
						}
						break;
					
					case WorkflowActivities.OTHERWISE:
						var otherwiseDO:OtherwiseDO = new OtherwiseDO();
						var otherwiseAttributesList:XMLList = element.@*;	
						otherwiseDO.populateAttributes(otherwiseAttributesList);
						//sequenceDO.printArray();
						parentDO.updateSubActivitiesArray(WorkflowActivities.OTHERWISE, otherwiseDO);
						if(element.children().length() > 0){
							parseElements(element, otherwiseDO);
						}
						break;
						
					case WorkflowActivities.FAULTHANDLERS:
						var faultHandlersDO:FaultHandlersDO = new FaultHandlersDO();
						var faultHandlersAttributesList:XMLList = element.@*;	
						faultHandlersDO.populateAttributes(faultHandlersAttributesList);
						//sequenceDO.printArray();
						parentDO.updateSubActivitiesArray(WorkflowActivities.FAULTHANDLERS, faultHandlersDO);
						if(element.children().length() > 0){
							parseElements(element, faultHandlersDO);
						}
						break;
						
					case WorkflowActivities.CATCH:
						var catchDO:CatchDO = new CatchDO();
						var catchAttributesList:XMLList = element.@*;	
						catchDO.populateAttributes(catchAttributesList);
						//sequenceDO.printArray();
						parentDO.updateSubActivitiesArray(WorkflowActivities.CATCH, catchDO);
						if(element.children().length() > 0){
							parseElements(element, catchDO);
						}
						break;
						
					case WorkflowActivities.CATCHALL:
						var catchAllDO:CatchAllDO = new CatchAllDO();
						var catchAllAttributesList:XMLList = element.@*;	
						catchAllDO.populateAttributes(catchAllAttributesList);
						//sequenceDO.printArray();
						parentDO.updateSubActivitiesArray(WorkflowActivities.CATCHALL, catchAllDO);
						if(element.children().length() > 0){
							parseElements(element, catchAllDO);
						}
						break;
						
					case WorkflowActivities.WHILE:
						//trace("BPELLoader while parseElements");
						var whileDO:WhileDO = new WhileDO();
						var whileAttributesList:XMLList = element.@*;	
						whileDO.populateAttributes(whileAttributesList);
						//whileDO.printArray();
						parentDO.updateSubActivitiesArray(WorkflowActivities.WHILE, whileDO);
						if(element.children().length() > 0){
							parseElements(element, whileDO);
						}
						break;
					
					case WorkflowActivities.RECEIVE:
						var receiveDO:ReceiveDO = new ReceiveDO();
						var receiveAttributesList:XMLList = element.@*;	
						receiveDO.populateAttributes(receiveAttributesList);
						parentDO.updateSubActivitiesArray(WorkflowActivities.RECEIVE, receiveDO);
						break;
						
					case WorkflowActivities.REPLY:
						var replyDO:ReplyDO = new ReplyDO();
						var replyAttributesList:XMLList = element.@*;	
						replyDO.populateAttributes(replyAttributesList);
						parentDO.updateSubActivitiesArray(WorkflowActivities.REPLY, replyDO);
						break;
						
					case WorkflowActivities.INVOKE:
						var invokeDO:InvokeDO = new InvokeDO();
						var invokeAttributesList:XMLList = element.@*;	
						for each(var item:XML in invokeAttributesList){
							//trace( item.name() + " : " + item);
						}
						invokeDO.populateAttributes(invokeAttributesList);
						//invokeDO.printArray();
						parentDO.updateSubActivitiesArray(WorkflowActivities.INVOKE, invokeDO);
						break;
					
					case WorkflowActivities.WAIT:
						var waitDO:WaitDO = new WaitDO();
						var waitAttributesList:XMLList = element.@*;	
						waitDO.populateAttributes(waitAttributesList);
						parentDO.updateSubActivitiesArray(WorkflowActivities.WAIT, waitDO);
						break;
						
					case WorkflowActivities.ASSIGN:					
						var assignDO:AssignDO = new AssignDO();
						var assignAttributesList:XMLList = element.@*;
						// assignDO.
						//trace("Assign DO from BPEL Loader" + element)
						if(element.@name){
							//trace("Assign Name " + element.@name)
							if(assignAttributesList){
								//trace("Assign Attributes Length " + assignAttributesList.length())
								assignDO.populateAttributes(assignAttributesList);
							}
						}
						// To get Child Copy Elements
						if(element.children().length() > 0){
							parseElements(element, assignDO);
						}
						
						// Populate the 						
						parentDO.updateSubActivitiesArray(WorkflowActivities.ASSIGN, assignDO);					
						break;
					
					case WorkflowActivities.COPY:
						var copyDO:CopyDO = new CopyDO();
						
						// To get Child From and To Elements
						if(element.children().length() > 0){
							parseElements(element, copyDO);
						}
						parentDO.updateSubActivitiesArray(WorkflowActivities.COPY, copyDO);
						break;
					
					case WorkflowActivities.FROM:
						var fromDO:FromDO = new FromDO();
						var fromAttributesList:XMLList = element.@*;
						
						// Element is never NULL in From but can be empty
						if(element){	
							//trace("From Element: " + element);
							
							// Attribute List Length can be from Zero to Two
							//trace("From Element Attribute List: " + fromAttributesList.length());
							if(fromAttributesList.length() > 0){								
								fromDO.populateAttributes(fromAttributesList);
							} else {
								fromDO.queryType = "empty";
								fromDO.fromValue = element.toString();
								//trace("Element Text Value: " + element.toString());
								
								// only to fill the attribute array with updated values
								fromDO.updateAttributesArray("","");
							}				
						}
						//fromDO.populateAttributes(fromAttributesList);
						parentDO.updateSubActivitiesArray(WorkflowActivities.FROM, fromDO);
						
						break;
						
					case WorkflowActivities.TO:
						var toDO:ToDO = new ToDO();
						var toAttributesList:XMLList = element.@*;
						
						// Element is never NULL in To but can be empty
						if(element){	
							//trace("To Element: " + element);
							
							// Attribute List Length can be from Zero to Two
							//trace("To Element Attribute List: " + toAttributesList.length());
							if(toAttributesList.length() > 0){								
								toDO.populateAttributes(toAttributesList);
							} else {
								toDO.queryType = "empty";
								toDO.toValue = element.toString();
								//trace("Element Text Value: " + element.toString());
								
								// only to fill the attribute array with updated values
								toDO.updateAttributesArray("","");
							}							
						}
						//fromDO.populateAttributes(fromAttributesList);
						parentDO.updateSubActivitiesArray(WorkflowActivities.TO, toDO);
						
						break;
					
					default:
						trace("Not yet implemented");
							
				}					
			}						
		}
		
		public function addNewActivity(parentType:String, parentName:String, childType:String, dataObject:Object):Boolean{
			//trace(parentType + "  " + parentName + "  " + childType)
			var processDO:ProcessDO = ProcessDO.getInstance();
			
			var sequenceDO:SequenceDO;
			var whileDO:WhileDO;
			var scopeDO:ScopeDO;
			var switchDO:SwitchDO;
			var caseDO:CaseDO;
			var otherwiseDO:OtherwiseDO;
			
			var booleanResult:Boolean = true;
			//for(var index:int = 0; index < processDO.subActivitiesArray.length; index++){
				//if
				switch(childType){				
							
					case WorkflowActivities.PARTNERLINK:
						if (processDO.subActivitiesArray[0][0] == parentType){
							PartnerLinksDO(processDO.subActivitiesArray[0][1]).updateSubActivitiesArray(WorkflowActivities.PARTNERLINK, PartnerLinkDO(dataObject));
						}
						break;				
							
					case WorkflowActivities.VARIABLE:
						if (processDO.subActivitiesArray[1][0] == parentType){
							VariablesDO(processDO.subActivitiesArray[1][1]).updateSubActivitiesArray(WorkflowActivities.VARIABLE, VariableDO(dataObject));
						}
						break;
						
					case WorkflowActivities.SEQUENCE:
						if(parentType == WorkflowActivities.PROCESS){
							processDO.updateSubActivitiesArray(WorkflowActivities.SEQUENCE, dataObject);
						} else if(parentType == WorkflowActivities.SEQUENCE){
							sequenceDO = SequenceDO (ActivityDOSearch.searchProcess(parentType, parentName)as SequenceDO);
							sequenceDO.updateSubActivitiesArray(WorkflowActivities.SEQUENCE, dataObject);
						} else if(parentType == WorkflowActivities.WHILE){
							//trace("Sequence: " + parentType)
							whileDO = WhileDO (ActivityDOSearch.searchProcess(parentType, parentName) as WhileDO);
							if(whileDO){
								// whileDO.subActivitiesArray should be null for first activity
								// while can have only one activity
								if(!whileDO.subActivitiesArray){									
									whileDO.updateSubActivitiesArray(WorkflowActivities.SEQUENCE, dataObject);
								} else { 									
									return false; 
								}
							}
						}						
						break;
					
					case WorkflowActivities.WHILE:
						if(parentType == WorkflowActivities.PROCESS){
							processDO.updateSubActivitiesArray(WorkflowActivities.WHILE, dataObject);
						} else if(parentType == WorkflowActivities.SEQUENCE){
							sequenceDO = SequenceDO(ActivityDOSearch.searchProcess(parentType, parentName) as SequenceDO);
							sequenceDO.updateSubActivitiesArray(WorkflowActivities.WHILE, dataObject);
						} else if(parentType == WorkflowActivities.WHILE) {
							whileDO = WhileDO(ActivityDOSearch.searchProcess(parentType, parentName)as WhileDO);
							if(whileDO){
								// whileDO.subActivitiesArray should be null for first activity
								// while can have only one activity
								if(!whileDO.subActivitiesArray){									
									whileDO.updateSubActivitiesArray(WorkflowActivities.WHILE, dataObject);
								} else { 									
									return false; 
								}
							}
						}						
						break;
					
					case WorkflowActivities.RECEIVE:
						if(parentType == WorkflowActivities.PROCESS){
							processDO.updateSubActivitiesArray(WorkflowActivities.RECEIVE, dataObject);
						} else if(parentType == WorkflowActivities.SEQUENCE){
							sequenceDO = SequenceDO(ActivityDOSearch.searchProcess(parentType, parentName) as SequenceDO);
							sequenceDO.updateSubActivitiesArray(WorkflowActivities.RECEIVE, dataObject);
						} else if(parentType == WorkflowActivities.WHILE){
							whileDO = WhileDO(ActivityDOSearch.searchProcess(parentType, parentName) as WhileDO);
							if(whileDO){
								// whileDO.subActivitiesArray should be null for first activity
								// while can have only one activity
								if(!whileDO.subActivitiesArray){									
									whileDO.updateSubActivitiesArray(WorkflowActivities.RECEIVE, dataObject);
								} else { 									
									return false; 
								}
							}
						}	
						break;
						
					case WorkflowActivities.REPLY:
						if(parentType == WorkflowActivities.PROCESS){
							processDO.updateSubActivitiesArray(WorkflowActivities.REPLY, dataObject);
						} else if(parentType == WorkflowActivities.SEQUENCE){
							sequenceDO = SequenceDO(ActivityDOSearch.searchProcess(parentType, parentName)as SequenceDO);
							sequenceDO.updateSubActivitiesArray(WorkflowActivities.REPLY, dataObject);
						} else if(parentType == WorkflowActivities.WHILE){
							whileDO = WhileDO(ActivityDOSearch.searchProcess(parentType, parentName) as WhileDO);
							if(whileDO){	
								// whileDO.subActivitiesArray should be null for first activity
								// while can have only one activity							
								if(!whileDO.subActivitiesArray){									
									whileDO.updateSubActivitiesArray(WorkflowActivities.REPLY, dataObject);
								} else { 									
									return false; 
								}
							}
						}	
						break;
						
					case WorkflowActivities.INVOKE:
						if(parentType == WorkflowActivities.PROCESS){
							processDO.updateSubActivitiesArray(WorkflowActivities.INVOKE, dataObject);
						} else if(parentType == WorkflowActivities.SEQUENCE){
							sequenceDO = SequenceDO(ActivityDOSearch.searchProcess(parentType, parentName) as SequenceDO);
							if(sequenceDO){
								sequenceDO.updateSubActivitiesArray(WorkflowActivities.INVOKE, dataObject);
							}
						} else if(parentType == WorkflowActivities.WHILE){
							whileDO = WhileDO(ActivityDOSearch.searchProcess(parentType, parentName)as WhileDO);
							if(whileDO){
								// whileDO.subActivitiesArray should be null for first activity
								// while can have only one activity
								if(!whileDO.subActivitiesArray){									
									whileDO.updateSubActivitiesArray(WorkflowActivities.INVOKE, dataObject);
								} else { 									
									return false; 
								}
							}
						}	
						break;
						
					case WorkflowActivities.ASSIGN:
					/*
						
					*/
						break;
					
					case WorkflowActivities.WAIT:
						if(parentType == WorkflowActivities.PROCESS){
							processDO.updateSubActivitiesArray(WorkflowActivities.WAIT, dataObject);
						} else if(parentType == WorkflowActivities.SEQUENCE){
							sequenceDO = SequenceDO(ActivityDOSearch.searchProcess(parentType, parentName) as SequenceDO);
							sequenceDO.updateSubActivitiesArray(WorkflowActivities.WAIT, dataObject);
						} else if(parentType == WorkflowActivities.WHILE){
							whileDO = WhileDO(ActivityDOSearch.searchProcess(parentType, parentName) as WhileDO);
							if(whileDO){
								// whileDO.subActivitiesArray should be null for first activity
								// while can have only one activity
								if(!whileDO.subActivitiesArray){									
									whileDO.updateSubActivitiesArray(WorkflowActivities.WAIT, dataObject);
								} else { 									
									return false; 
								}
							}
						}	
						break;
					default:
						trace("Not yet implemented");
							
				}
				return booleanResult;
			//}
		}
		
	}
}