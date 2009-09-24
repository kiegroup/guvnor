package bpel.editor.gridcc.utility
{
	import bpel.editor.gridcc.data.*
	import bpel.editor.gridcc.constant.WorkflowActivities;
	public class ActivityDOSearch
	{
		// Should not return SequenceDo can be flow or scope also
		// not yet implemented
		
		public static function searchProcess(type:String, name:String):ActivityDO {			
			trace("ActivityDOSearch.searchProcess: " + type + "   " + name);
			var processDO:ProcessDO = ProcessDO.getInstance();
			var returnActivityDO:ActivityDO = null;
			
			//Starting with Index 2 which can be SequenceDO.
			for(var i:int = 2; i < processDO.subActivitiesArray.length; i++){
				
				//if(processDO.subActivitiesArray[i][0] == type){
					
					switch(processDO.subActivitiesArray[i][0]){					
							
						case WorkflowActivities.SEQUENCE:
							var tempSequenceDO:SequenceDO;
							tempSequenceDO = SequenceDO(processDO.subActivitiesArray[i][1]);
							//trace("ActivityDOSearch tempSequenceDO.getName(): " + tempSequenceDO.getName());
							if(tempSequenceDO.getName() == name && type == WorkflowActivities.SEQUENCE)	{
								return tempSequenceDO;
							} else {
								returnActivityDO = searchSequence(type, name, tempSequenceDO);
							}				
							
							if(returnActivityDO){
								//trace("returnActivityDO: " + returnActivityDO);
								return returnActivityDO;
							}
							break;
						
						case WorkflowActivities.WHILE:
							var tempWhileDO:WhileDO;
							tempWhileDO = WhileDO(processDO.subActivitiesArray[i][1]);
							//trace("ActivityDOSearch tempWhileDO.getName(): " + tempWhileDO.getName());
							if(tempWhileDO.getName() == name && type == WorkflowActivities.WHILE)	{								
								return tempWhileDO;
							} else {
								returnActivityDO = searchWhile(type, name, tempWhileDO);
							}				
							
							if(returnActivityDO){
								//trace("returnActivityDO: " + returnActivityDO);
								return returnActivityDO;
							}
							break;
							
							default: 
								trace("Not Implemented Yet");
					}				
				//}
			}
			//trace("returnActivityDO: " + returnActivityDO);
			
			return returnActivityDO;
		}
		
		public static function searchSequence(type:String, name:String, sequenceDO:SequenceDO):ActivityDO {	
			//trace("ActivityDOSearch.searchSequence: " + type + "   " + name);	
			var returnActivityDO:ActivityDO = null;
			if(sequenceDO.subActivitiesArray){
				for(var i:int = 0; i < sequenceDO.subActivitiesArray.length; i++){
					//if(sequenceDO.subActivitiesArray[i][0] == type){
						switch(sequenceDO.subActivitiesArray[i][0]){
													
							case WorkflowActivities.SEQUENCE:
								var tempSequenceDO:SequenceDO;
								tempSequenceDO = SequenceDO(sequenceDO.subActivitiesArray[i][1]);
								
								//trace("ActivityDOSearch tempSequenceDO.getName(): " + tempSequenceDO.getName());
								if(tempSequenceDO.getName() == name && type == WorkflowActivities.SEQUENCE)	{
									return tempSequenceDO;
								} else {
									returnActivityDO = searchSequence(type, name, tempSequenceDO);
								}				
								
								if(returnActivityDO){
									return returnActivityDO;
								}							
								break;	
								
							case WorkflowActivities.WHILE:
								var tempWhileDO:WhileDO;
								tempWhileDO = WhileDO(sequenceDO.subActivitiesArray[i][1]);
								//trace("ActivityDOSearch tempWhileDO.getName(): " + tempWhileDO.getName());
								if(tempWhileDO.getName() == name && type == WorkflowActivities.WHILE)	{
									//trace("tempWhileDO.subActivitiesArray.length: " + tempWhileDO.subActivitiesArray.length);
									return tempWhileDO;
								} else {
									returnActivityDO = searchWhile(type, name, tempWhileDO);
								}				
								
								if(returnActivityDO){
									return returnActivityDO;
								}
								break;						
						}					
					//}
				}
			}			
			return returnActivityDO;
		}
		
		public static function searchWhile(type:String, name:String, whileDO:WhileDO):ActivityDO {	
			//trace("ActivityDOSearch.searchWhile: " + type + "   " + name);	
			var returnActivityDO:ActivityDO = null;
			if(whileDO.subActivitiesArray){
				for(var i:int = 0; i < whileDO.subActivitiesArray.length; i++){
					if(whileDO.subActivitiesArray[i][0] == type){
						switch(whileDO.subActivitiesArray[i][0]){
													
							case WorkflowActivities.SEQUENCE:
								var tempSequenceDO:SequenceDO;
								tempSequenceDO = SequenceDO(whileDO.subActivitiesArray[i][1]);
								
								//trace("ActivityDOSearch tempSequenceDO.getName(): " + tempSequenceDO.getName());
								if(tempSequenceDO.getName() == name && type == WorkflowActivities.SEQUENCE)	{
									return tempSequenceDO;
								} else {
									returnActivityDO = searchSequence(type, name, tempSequenceDO);
								}				
								
								if(returnActivityDO){
									return returnActivityDO;
								}							
								break;	
								
							case WorkflowActivities.WHILE:
								var tempWhileDO:WhileDO;
								tempWhileDO = WhileDO(whileDO.subActivitiesArray[i][1]);
								//trace("ActivityDOSearch tempWhileDO.getName(): " + tempWhileDO.getName());
								if(tempWhileDO.getName() == name && type == WorkflowActivities.WHILE)	{
									return tempWhileDO;
								} else {
									returnActivityDO = searchWhile(type, name, tempWhileDO);
								}				
								
								if(returnActivityDO){
									return returnActivityDO;
								}
								break;							
						}					
					}
				}
			}			
			return returnActivityDO;
		}
		
		// Returning all partnerLink's in PartnerLinksDO for view ...
		public static function getPartnerLinkAsArray():Array{
			//var processDO:ProcessDO = ProcessDO.getInstance();
			var partnerLinksDO:PartnerLinksDO = PartnerLinksDO.getInstance();
			var tempPartnerLinkArray:Array = new Array();
			tempPartnerLinkArray.push(" - - - - - - - -");
			
			for (var index:int = 0; index < partnerLinksDO.subActivitiesArray.length; index++){
				var partnerLinkName:String = PartnerLinkDO(partnerLinksDO.subActivitiesArray[index][1]).getName();
				
				tempPartnerLinkArray.push(partnerLinkName);
			}			
			return tempPartnerLinkArray;
		}
		
		// Returning all variable's in VariablesDO for View
		public static function getVariableAsArray():Array{
			//var processDO:ProcessDO = ProcessDO.getInstance();
			var variablesDO:VariablesDO = VariablesDO.getInstance();
			var tempVariableArray:Array = new Array();
			tempVariableArray.push(" - - - - - - - - ");
			
			for (var index:int = 0; index < variablesDO.subActivitiesArray.length; index++){
				var variableName:String = VariableDO(variablesDO.subActivitiesArray[index][1]).getName();
				
				tempVariableArray.push(variableName);
			}			
			return tempVariableArray;
		}
		
		public static function variableIndex(variableName:String):int {
			var tempVariableArray:Array = ActivityDOSearch.getVariableAsArray();
			
			for(var index:int = 0; index < tempVariableArray.length; index++){
				var tempString:String = String (tempVariableArray[index]);
				//trace(tempString + "  " + variableName);
				if(tempString == variableName){
					return index;
				}
			}
			return 0;
		}
		
		public static function partnerLinkIndex(partnerLinkName:String):int {
			var tempPLArray:Array = ActivityDOSearch.getPartnerLinkAsArray();
			
			for(var index:int = 0; index < tempPLArray.length; index++){
				var tempString:String = String (tempPLArray[index]);
				//trace(tempString + "  " + partnerLinkName);
				if(tempString == partnerLinkName){
					return index;
				}
			}
			return 0;
		}
	}
}