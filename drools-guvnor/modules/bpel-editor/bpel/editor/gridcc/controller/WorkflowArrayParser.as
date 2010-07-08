package bpel.editor.gridcc.controller

{	import bpel.editor.gridcc.data.*;
	import bpel.editor.gridcc.view.*;	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.constant.VisualCoordinateConstant;
	
	 import flash.events.TimerEvent;
     import flash.utils.Timer;	
	
	import mx.controls.Button;
	import mx.containers.Canvas;
	
	public class WorkflowArrayParser {
		
		/**
         * The Singleton instance of WorkflowArrayParser
         */        
        protected static var instance:WorkflowArrayParser;
        //private var rootParent:Canvas;	
        //private var rootParent:Canvas; 
        
        private var processContainer:Process;
        
        //private var minuteTimer:Timer; 
        
		/**
         * Constructor
         * 
         * Instantiates Singleton instance of WorkflowArrayParser
         */        
        public function WorkflowArrayParser()
        {
            if (WorkflowArrayParser.instance == null) 
            {                
                WorkflowArrayParser.instance = this; 
                
            }            
        }
        
        /**
         * Determines if the singleton instance of WorkflowArrayParser
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of WorkflowParser
         */        
        public static function getInstance():WorkflowArrayParser
        {
            if (instance == null) 
            {
               instance = new WorkflowArrayParser();   
            }            
            return instance;
        }
        
        public function parseWorkflowArray():CompositeActivity {   	
        	var processDO:ProcessDO = ProcessDO.getInstance();       	    	
        	createProcessVisualObject();        			
			return processContainer;
		}
		
		private function createProcessVisualObject():void{
			var processDO:ProcessDO = ProcessDO.getInstance();
			var processName:String = processDO.getName(); 
			//trace("WFAP.processName: " + processName);			
			processContainer = new Process(null, processName, "process", processDO);
			parseSubActivities(processContainer);			
		}
		
		// This operation is called recursively
		// When ever Process, PartnerLinks, Variables, Sequence or Assign 
		// will be found it will be called.
		public function parseSubActivities(parent:CompositeActivity):void{			
			//trace(parent.name);			
			var verticalGap:Number = VisualCoordinateConstant.verticalGapBetweenActivities;			
			var tempComponentYPosition:Number = verticalGap;
			//var tempActivityType:String;
			var tempSubActivitiesArray:Array;
			
			if(parent is Process){
				tempSubActivitiesArray  = Process(parent).dataObject.subActivitiesArray;
			} 
			else if (parent is PartnerLinks){
				tempSubActivitiesArray  = PartnerLinks(parent).dataObject.subActivitiesArray;
			}
			else if (parent is Variables) {
				tempSubActivitiesArray  = Variables(parent).dataObject.subActivitiesArray;
			}
			else if (parent is Sequence){
				tempSubActivitiesArray  = Sequence(parent).dataObject.subActivitiesArray;
			}			
			else if (parent is Assign){
				tempSubActivitiesArray  = Assign(parent).dataObject.subActivitiesArray;
			}
			else if (parent is While){
				tempSubActivitiesArray  = While(parent).dataObject.subActivitiesArray;
			}
			else if (parent is Scope){
				tempSubActivitiesArray  = Scope(parent).dataObject.subActivitiesArray;
			}
			else if (parent is Switch){
				tempSubActivitiesArray  = Switch(parent).dataObject.subActivitiesArray;
			}
			else if (parent is Case){
				tempSubActivitiesArray  = Case(parent).dataObject.subActivitiesArray;
			}
			else if (parent is Otherwise){
				tempSubActivitiesArray  = Otherwise(parent).dataObject.subActivitiesArray;
			}
			else if (parent is FaultHandlers){
				tempSubActivitiesArray  = FaultHandlers(parent).dataObject.subActivitiesArray;
			}
			else if (parent is Catch){
				tempSubActivitiesArray  = Catch(parent).dataObject.subActivitiesArray;
			}
			else if (parent is CatchAll){
				tempSubActivitiesArray  = CatchAll(parent).dataObject.subActivitiesArray;
			}
			
			
			// This is only to avoid if containerDO is empty
			// without any sub activity
			if(tempSubActivitiesArray){
				//trace("tempSubActivitiesArray in parseSubActivities");
				for(var i:int = 0; i < tempSubActivitiesArray.length; i++){
					var tempContainer:CompositeActivity = null;
					//trace(parent + " $$$  " + tempContainer);
					var tempButton:Button;
					
					switch(tempSubActivitiesArray[i][0]){
						
						case WorkflowActivities.PARTNERLINKS:
							//trace(indention + tempSubActivitiesArray[i][0]);
							var partnerLinksDO:PartnerLinksDO = PartnerLinksDO (tempSubActivitiesArray[i][1]);
							
							tempContainer = new PartnerLinks(parent, partnerLinksDO.getName(), "partnerLinks", partnerLinksDO);
							
							break;
							
						case WorkflowActivities.VARIABLES:
							//trace(indention + array[i][0]);
							var variablesDO:VariablesDO = VariablesDO (tempSubActivitiesArray[i][1]);
							
							tempContainer = new Variables(parent, variablesDO.getName(), "variables", variablesDO);
							
							break;
							
						case WorkflowActivities.SEQUENCE:
							//trace(indention + array[i][0]);
							var sequenceDO:SequenceDO = SequenceDO (tempSubActivitiesArray[i][1]);
							//sequenceDO.printArray();
							//trace(sequenceDO.attributesArray.length)
							tempContainer = new Sequence(parent, sequenceDO.getName(), "sequence", sequenceDO);
							/*
							if(sequenceDO.subActivitiesArray){
								//parseSubActivities(sequenceDO.subActivitiesArray);
							}
							*/
							break;
						
						case WorkflowActivities.WHILE:							
							var whileDO:WhileDO = WhileDO (tempSubActivitiesArray[i][1]);							
							tempContainer = new While(parent, whileDO.getName(), "while", whileDO);							
							break;
						
						case WorkflowActivities.SCOPE:							
							var scopeDO:ScopeDO = ScopeDO (tempSubActivitiesArray[i][1]);							
							tempContainer = new Scope(parent, scopeDO.getName(), "scope", scopeDO);							
							break;
							
						case WorkflowActivities.SWITCH:							
							var switchDO:SwitchDO = SwitchDO (tempSubActivitiesArray[i][1]);							
							tempContainer = new Switch(parent, switchDO.getName(), "switch", switchDO);							
							break;
						
						case WorkflowActivities.CASE:							
							var caseDO:CaseDO = CaseDO (tempSubActivitiesArray[i][1]);							
							tempContainer = new Case(parent, caseDO.getName(), "case", caseDO);							
							break;
						
						case WorkflowActivities.OTHERWISE:							
							var otherwiseDO:OtherwiseDO = OtherwiseDO (tempSubActivitiesArray[i][1]);							
							tempContainer = new Otherwise(parent, otherwiseDO.getName(), "otherwise", otherwiseDO);							
							break;
						
						case WorkflowActivities.FAULTHANDLERS:							
							var faultHandlersDO:FaultHandlersDO = FaultHandlersDO (tempSubActivitiesArray[i][1]);							
							tempContainer = new FaultHandlers(parent, faultHandlersDO.getName(), "faultHandlers", faultHandlersDO);							
							break;
						
						case WorkflowActivities.CATCH:							
							var catchDO:CatchDO = CatchDO (tempSubActivitiesArray[i][1]);							
							tempContainer = new Catch(parent, catchDO.getName(), "catch", catchDO);							
							break;
						
						case WorkflowActivities.CATCHALL:							
							var catchAllDO:CatchAllDO = CatchAllDO (tempSubActivitiesArray[i][1]);							
							tempContainer = new CatchAll(parent, catchAllDO.getName(), "catchAll", catchAllDO);							
							break;
						
						case WorkflowActivities.PARTNERLINK:
							//trace(indention + array[i][0]);
							var partnerLinkDO:PartnerLinkDO = PartnerLinkDO (tempSubActivitiesArray[i][1]);
							
							var tempPartnerLink:PartnerLink = new PartnerLink(parent, partnerLinkDO, tempComponentYPosition);
							parent.addNewActivity(tempPartnerLink);
							
							// set Y position for next component
							// 40 is relevant to button size
							tempComponentYPosition = tempComponentYPosition + VisualCoordinateConstant.nextActivityYPositionIncrement;					

							parent.resetHeight(tempComponentYPosition + VisualCoordinateConstant.parentHightResize);
							
							break;
						
						case WorkflowActivities.VARIABLE:
							//trace(indention + array[i][0]);
							var variableDO:VariableDO = VariableDO (tempSubActivitiesArray[i][1]);
							var tempVariable:Variable = new Variable(parent, variableDO, tempComponentYPosition);
							parent.addNewActivity(tempVariable);
							
							// set Y position for next component
							tempComponentYPosition = tempComponentYPosition + VisualCoordinateConstant.nextActivityYPositionIncrement;						

							parent.resetHeight(tempComponentYPosition + VisualCoordinateConstant.parentHightResize);							
							break;
						
						case WorkflowActivities.RECEIVE:
							//trace("  "  + tempSubActivitiesArray[i][0]);
							var receiveDO:ReceiveDO = ReceiveDO (tempSubActivitiesArray[i][1]);
							
							var tempReceive:Receive = new Receive(parent,receiveDO,tempComponentYPosition);
							parent.addNewActivity(tempReceive);
							
							// set Y position for next component
							tempComponentYPosition = tempComponentYPosition + VisualCoordinateConstant.nextActivityYPositionIncrement						

							parent.resetHeight(tempComponentYPosition + VisualCoordinateConstant.parentHightResize);
							break;
							
						case WorkflowActivities.REPLY:
							//trace(indention + array[i][0]);
							var replyDO:ReplyDO = ReplyDO (tempSubActivitiesArray[i][1]);
							
							var tempReply:Reply = new Reply (parent,replyDO,tempComponentYPosition);
							
							parent.addNewActivity(tempReply);
							
							// set Y position for next component
							tempComponentYPosition = tempComponentYPosition + VisualCoordinateConstant.nextActivityYPositionIncrement						

							parent.resetHeight(tempComponentYPosition + VisualCoordinateConstant.parentHightResize);
							break;
							
						case WorkflowActivities.WAIT:
							//trace(indention + array[i][0]);
							var waitDO:WaitDO = WaitDO (tempSubActivitiesArray[i][1]);
							
							var tempWait:Wait = new Wait (parent,waitDO,tempComponentYPosition);
							
							parent.addNewActivity(tempWait);
							
							// set Y position for next component
							tempComponentYPosition = tempComponentYPosition + VisualCoordinateConstant.nextActivityYPositionIncrement						

							parent.resetHeight(tempComponentYPosition + VisualCoordinateConstant.parentHightResize);
							break;
							
						case WorkflowActivities.INVOKE:
							//trace(indention + array[i][0]);
							var invokeDO:InvokeDO = InvokeDO (tempSubActivitiesArray[i][1]);
							
							var tempInvoke:Invoke = new Invoke (parent,invokeDO,tempComponentYPosition);
							
							parent.addNewActivity(tempInvoke);
							
							// set Y position for next component
							tempComponentYPosition = tempComponentYPosition + VisualCoordinateConstant.nextActivityYPositionIncrement						

							parent.resetHeight(tempComponentYPosition + VisualCoordinateConstant.parentHightResize);
							break;
							
						case WorkflowActivities.EMPTY:
							//trace(indention + array[i][0]);
							var emptyDO:EmptyDO = EmptyDO (tempSubActivitiesArray[i][1]);
							
							var tempEmpty:Empty = new Empty (parent,emptyDO,tempComponentYPosition);
							
							parent.addNewActivity(tempEmpty);
							
							// set Y position for next component
							tempComponentYPosition = tempComponentYPosition + VisualCoordinateConstant.nextActivityYPositionIncrement						

							parent.resetHeight(tempComponentYPosition + VisualCoordinateConstant.parentHightResize);
							break;
							
						case WorkflowActivities.TERMINATE:
							//trace(indention + array[i][0]);
							var terminateDO:TerminateDO = TerminateDO (tempSubActivitiesArray[i][1]);
							
							var tempTerminate:Terminate = new Terminate (parent, terminateDO, tempComponentYPosition);
							
							parent.addNewActivity(tempTerminate);
							
							// set Y position for next component
							tempComponentYPosition = tempComponentYPosition + VisualCoordinateConstant.nextActivityYPositionIncrement						

							parent.resetHeight(tempComponentYPosition + VisualCoordinateConstant.parentHightResize);
							break;
							
						case WorkflowActivities.ASSIGN:
							var assignDO:AssignDO = AssignDO (tempSubActivitiesArray[i][1]);							
							tempContainer = new Assign(parent, assignDO.getName(), "assign", assignDO);
							break;
							
						case WorkflowActivities.COPY:
							//trace(indention + array[i][0]);
							var copyDO:CopyDO = CopyDO (tempSubActivitiesArray[i][1]);
							
							var tempCopy:Copy = new Copy (parent,copyDO,tempComponentYPosition);
							
							parent.addNewActivity(tempCopy);
							
							// set Y position for next component
							tempComponentYPosition = tempComponentYPosition + VisualCoordinateConstant.nextActivityYPositionIncrement						

							parent.resetHeight(tempComponentYPosition + VisualCoordinateConstant.parentHightResize);
							break;
							
						default:
						   //trace(indention + tempSubActivitiesArray[i][0]);					
					}
					if(tempContainer){
						//minuteTimer = new Timer(200, 1);
						// Starting width for any activity 
						// endups for the innermost composite activity
						tempContainer.resetWidth(200);									
						
						// Temp Container's Y position
						tempContainer.y = tempComponentYPosition ;
						
						parseSubActivities(tempContainer);
						
						// set Y position for next component
						tempComponentYPosition = tempContainer.height + tempComponentYPosition + verticalGap;
						
						// set the height of the parent activity
						parent.resetHeight(tempComponentYPosition + VisualCoordinateConstant.parentHightResize);
						
						// Increment the Width of the Parent if required
							if(parent.width <= tempContainer.width) {							
								parent.resetWidth(tempContainer.width + 40);
							}
							
							// set the X poisition of current activity in the middle
							tempContainer.x = ((parent.width - tempContainer.width)/2) - 6;
							//trace(parent.name + "  ++  " + tempContainer.name);
							parent.addNewActivity(tempContainer);
							
							//tempContainer = parent;
					} 					
				}
			}
		}
		
		// Returns NULL for atomic activities or composite activity
		public function computeAndAddChild(parentType:String, parentName:String, childType:String, dataObject:Object):CompositeActivity {
			if(BPELLoader.getInstance().addNewActivity(parentType,parentName,childType,dataObject)){
				var tempParentCompositeActivity:CompositeActivity;
				switch(childType){
					
					case WorkflowActivities.PARTNERLINK:
						var tempPartnerLink:PartnerLink = new PartnerLink(getPartnerLinksView(), PartnerLinkDO(dataObject), (getPartnerLinksView().height - 40));
						
						getPartnerLinksView().addNewActivity(tempPartnerLink);
						getPartnerLinksView().height = getPartnerLinksView().height + tempPartnerLink.height + 30;
						getPartnerLinksView().activityContainer.height = getPartnerLinksView().activityContainer.height + tempPartnerLink.height + 30;
						getPartnerLinksView().activityContainer.setStyle("borderStyle", "solid");
						redrawActivities(getPartnerLinksView());
						
						return null;
						break;
						
					case WorkflowActivities.VARIABLE:					
						var tempVariable:Variable = new Variable(getVariablesView(), VariableDO(dataObject), (getVariablesView().height - 40));
						
						getVariablesView().addNewActivity(tempVariable);
						getVariablesView().height = getVariablesView().height + tempVariable.height + 30;
						getVariablesView().activityContainer.height = getVariablesView().activityContainer.height + tempVariable.height + 30;
						getVariablesView().activityContainer.setStyle("borderStyle", "solid");
						redrawActivities(getVariablesView());
						
						return null;
						break;
					
					case WorkflowActivities.INVOKE:
						    //trace("invoke");
						    var tempInvoke:Invoke;
						    if(parentType == WorkflowActivities.PROCESS){
						    	tempInvoke = new Invoke(processContainer,InvokeDO(dataObject), processContainer.height - 40);
						    	processContainer.addNewActivity(tempInvoke);
						    	processContainer.height = processContainer.height + tempInvoke.height + 30;
								processContainer.activityContainer.height = processContainer.activityContainer.height + tempInvoke.height + 20;
						    	processContainer.activityContainer.setStyle("borderStyle", "solid");
						    }else {					      	
		                		tempParentCompositeActivity= findActivity(processContainer, parentType, parentName);
						    	//trace(tempParentCompositeActivity.name + " +++++++++ " + tempParentCompositeActivity.activityType);
						    	tempInvoke = new Invoke(tempParentCompositeActivity,InvokeDO(dataObject), tempParentCompositeActivity.height - 40);
						    	tempParentCompositeActivity.addNewActivity(tempInvoke);
						    	tempParentCompositeActivity.height = tempParentCompositeActivity.height + tempInvoke.height + 30;
								tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + tempInvoke.height + 30;
						    	tempParentCompositeActivity.activityContainer.setStyle("borderStyle", "solid");
						    	redrawActivities(tempParentCompositeActivity);
						    }
						    
						    return null;
						    break;
						      
						case WorkflowActivities.RECEIVE:
						    //trace("receive");					      	
		                	var tempReceive:Receive;
						    if(parentType == WorkflowActivities.PROCESS){
						    	tempReceive = new Receive(processContainer,ReceiveDO(dataObject), processContainer.height - 40);
						    	processContainer.addNewActivity(tempReceive);
						    	processContainer.height = processContainer.height + tempReceive.height + 30;
								processContainer.activityContainer.height = processContainer.activityContainer.height + tempReceive.height + 30;
						    	processContainer.activityContainer.setStyle("borderStyle", "solid");
						    }else {					      	
		                		tempParentCompositeActivity = findActivity(processContainer, parentType, parentName);
						    	//trace(tempParentCompositeActivity.name + " +++++++++ " + tempParentCompositeActivity.activityType);
						    	tempReceive = new Receive(tempParentCompositeActivity,ReceiveDO(dataObject), tempParentCompositeActivity.height - 40);
						    	tempParentCompositeActivity.addNewActivity(tempReceive);
						    	tempParentCompositeActivity.height = tempParentCompositeActivity.height + tempReceive.height + 30;
								tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + tempReceive.height + 30;
						    	tempParentCompositeActivity.activityContainer.setStyle("borderStyle", "solid");
						    	redrawActivities(tempParentCompositeActivity);
						    }
						    
						    return null;
						    break;
						    
						case WorkflowActivities.REPLY:
						    //trace("reply");					      	
		                	var tempReply:Reply;
						    if(parentType == WorkflowActivities.PROCESS){
						    	tempReply = new Reply(processContainer,ReplyDO(dataObject), processContainer.height - 40);
						    	processContainer.addNewActivity(tempReply);
						    	processContainer.height = processContainer.height + tempReply.height + 30;
								processContainer.activityContainer.height = processContainer.activityContainer.height + tempReply.height + 30;
						    	processContainer.activityContainer.setStyle("borderStyle", "solid");
						    }else {					      	
		                		tempParentCompositeActivity = findActivity(processContainer, parentType, parentName);
						    	//trace(tempParentCompositeActivity.name + " +++++++++ " + tempParentCompositeActivity.activityType);
						    	tempReply = new Reply(tempParentCompositeActivity,ReplyDO(dataObject), tempParentCompositeActivity.height - 40);
						    	tempParentCompositeActivity.addNewActivity(tempReply);
						    	tempParentCompositeActivity.height = tempParentCompositeActivity.height + tempReply.height + 30;
								tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + tempReply.height + 30;
						    	tempParentCompositeActivity.activityContainer.setStyle("borderStyle", "solid");
						    	redrawActivities(tempParentCompositeActivity);
						    }
						    
						    return null;
						    break;
						
						case WorkflowActivities.WAIT:
						    //trace("wait");					      	
		                	var tempWait:Wait;
						    if(parentType == WorkflowActivities.PROCESS){
						    	tempWait = new Wait(processContainer,WaitDO(dataObject), processContainer.height - 40);
						    	processContainer.addNewActivity(tempWait);
						    	processContainer.height = processContainer.height + tempWait.height + 30;
								processContainer.activityContainer.height = processContainer.activityContainer.height + tempWait.height + 30;
						    	processContainer.activityContainer.setStyle("borderStyle", "solid");
						    }else {					      	
		                		tempParentCompositeActivity = findActivity(processContainer, parentType, parentName);
						    	//trace(tempParentCompositeActivity.name + " +++++++++ " + tempParentCompositeActivity.activityType);
						    	tempWait = new Wait(tempParentCompositeActivity, WaitDO(dataObject), tempParentCompositeActivity.height - 40);
						    	tempParentCompositeActivity.addNewActivity(tempWait);
						    	tempParentCompositeActivity.height = tempParentCompositeActivity.height + tempWait.height + 30;
								tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + tempWait.height + 30;
						    	tempParentCompositeActivity.activityContainer.setStyle("borderStyle", "solid");
						    	redrawActivities(tempParentCompositeActivity);
						    }
						    
						    return null;
						    break;
						        
						case WorkflowActivities.SEQUENCE:
							var tempSequence:Sequence;
						    if(parentType == WorkflowActivities.PROCESS){
						    	tempSequence = new Sequence(processContainer, SequenceDO(dataObject).getName(),childType, SequenceDO(dataObject));
						    	tempSequence.y = processContainer.height - 40;
						    	tempSequence.x = (processContainer.width - tempSequence.width)/2;
						    	processContainer.addNewActivity(tempSequence);
						    	processContainer.height = processContainer.height + tempSequence.height + 30;
								processContainer.activityContainer.height = processContainer.activityContainer.height + tempSequence.height + 30;
						    	processContainer.activityContainer.setStyle("borderStyle", "solid");
						    }else {					      	
		                		tempParentCompositeActivity = findActivity(processContainer, parentType, parentName);
						    	//trace(tempParentCompositeActivity.name + " +++++++++ " + tempParentCompositeActivity.activityType);
						    	tempSequence = new Sequence(tempParentCompositeActivity,SequenceDO(dataObject).getName(),childType, SequenceDO(dataObject));
						    	tempSequence.y = tempParentCompositeActivity.height - 40;
						    	if(tempSequence.width >= tempParentCompositeActivity.width){
						    		tempSequence.resetWidth(tempParentCompositeActivity.width -50);
						    	}
						    	tempSequence.x = (tempParentCompositeActivity.width - tempSequence.width)/2;
						    	tempParentCompositeActivity.addNewActivity(tempSequence);
						    	tempParentCompositeActivity.height = tempParentCompositeActivity.height + tempSequence.height + 30;
								tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + tempSequence.height + 30;
						    	tempParentCompositeActivity.activityContainer.setStyle("borderStyle", "solid");
						    	redrawActivities(tempParentCompositeActivity);					    	
						    }
						    return tempSequence;
							break;	
							
						case WorkflowActivities.WHILE:
							var tempWhile:While;
						    if(parentType == WorkflowActivities.PROCESS){
						    	tempWhile = new While(processContainer, WhileDO(dataObject).getName(),childType, WhileDO(dataObject));
						    	tempWhile.y = processContainer.height - 40;
						    	tempWhile.x = (processContainer.width - tempWhile.width)/2;
						    	processContainer.addNewActivity(tempWhile);
						    	processContainer.height = processContainer.height + tempWhile.height + 30;
								processContainer.activityContainer.height = processContainer.activityContainer.height + tempWhile.height + 30;
						    	processContainer.activityContainer.setStyle("borderStyle", "solid");
						    }else {					      	
		                		tempParentCompositeActivity = findActivity(processContainer, parentType, parentName);
						    	//trace(tempParentCompositeActivity.name + " +++++++++ " + tempParentCompositeActivity.activityType);
						    	tempWhile = new While(tempParentCompositeActivity,WhileDO(dataObject).getName(),childType, WhileDO(dataObject));
						    	tempWhile.y = tempParentCompositeActivity.height - 40;
						    	if(tempWhile.width >= tempParentCompositeActivity.width){
						    		tempWhile.resetWidth(tempParentCompositeActivity.width -50);
						    	}
						    	tempWhile.x = (tempParentCompositeActivity.width - tempWhile.width)/2;
						    	tempParentCompositeActivity.addNewActivity(tempWhile);
						    	tempParentCompositeActivity.height = tempParentCompositeActivity.height + tempWhile.height + 30;
								tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + tempWhile.height + 30;
						    	tempParentCompositeActivity.activityContainer.setStyle("borderStyle", "solid");
						    	redrawActivities(tempParentCompositeActivity);					    	
						    }
						    return tempWhile;
							break;
						
							
						case WorkflowActivities.SCOPE:
							var tempScope:Scope;
						    if(parentType == WorkflowActivities.PROCESS){
						    	tempScope = new Scope(processContainer, ScopeDO(dataObject).getName(),childType, ScopeDO(dataObject));
						    	tempScope.y = processContainer.height - 40;
						    	tempScope.x = (processContainer.width - tempScope.width)/2;
						    	processContainer.addNewActivity(tempScope);
						    	processContainer.height = processContainer.height + tempScope.height + 30;
								processContainer.activityContainer.height = processContainer.activityContainer.height + tempScope.height + 30;
						    	processContainer.activityContainer.setStyle("borderStyle", "solid");
						    }else {					      	
		                		tempParentCompositeActivity = findActivity(processContainer, parentType, parentName);
						    	//trace(tempParentCompositeActivity.name + " +++++++++ " + tempParentCompositeActivity.activityType);
						    	tempScope = new Scope(tempParentCompositeActivity,ScopeDO(dataObject).getName(),childType, ScopeDO(dataObject));
						    	tempScope.y = tempParentCompositeActivity.height - 40;
						    	if(tempScope.width >= tempParentCompositeActivity.width){
						    		tempScope.resetWidth(tempParentCompositeActivity.width -50);
						    	}
						    	tempScope.x = (tempParentCompositeActivity.width - tempScope.width)/2;
						    	tempParentCompositeActivity.addNewActivity(tempScope);
						    	tempParentCompositeActivity.height = tempParentCompositeActivity.height + tempScope.height + 30;
								tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + tempScope.height + 30;
						    	tempParentCompositeActivity.activityContainer.setStyle("borderStyle", "solid");
						    	redrawActivities(tempParentCompositeActivity);					    	
						    }
						    return tempScope;
							break;						
							
						case WorkflowActivities.FAULTHANDLERS:
							var tempFaultHandlers:FaultHandlers;
						    if(parentType == WorkflowActivities.PROCESS){
						    	tempFaultHandlers = new FaultHandlers(processContainer, FaultHandlersDO(dataObject).getName(),childType, FaultHandlersDO(dataObject));
						    	tempFaultHandlers.y = processContainer.height - 40;
						    	tempFaultHandlers.x = (processContainer.width - tempFaultHandlers.width)/2;
						    	processContainer.addNewActivity(tempFaultHandlers);
						    	processContainer.height = processContainer.height + tempFaultHandlers.height + 30;
								processContainer.activityContainer.height = processContainer.activityContainer.height + tempFaultHandlers.height + 30;
						    	processContainer.activityContainer.setStyle("borderStyle", "solid");
						    }else {					      	
		                		tempParentCompositeActivity = findActivity(processContainer, parentType, parentName);
						    	//trace(tempParentCompositeActivity.name + " +++++++++ " + tempParentCompositeActivity.activityType);
						    	tempFaultHandlers = new FaultHandlers(tempParentCompositeActivity, FaultHandlersDO(dataObject).getName(),childType, FaultHandlersDO(dataObject));
						    	tempFaultHandlers.y = tempParentCompositeActivity.height - 40;
						    	if(tempFaultHandlers.width >= tempParentCompositeActivity.width){
						    		tempFaultHandlers.resetWidth(tempParentCompositeActivity.width -50);
						    	}
						    	tempFaultHandlers.x = (tempParentCompositeActivity.width - tempFaultHandlers.width)/2;
						    	tempParentCompositeActivity.addNewActivity(tempFaultHandlers);
						    	tempParentCompositeActivity.height = tempParentCompositeActivity.height + tempFaultHandlers.height + 30;
								tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + tempFaultHandlers.height + 30;
						    	tempParentCompositeActivity.activityContainer.setStyle("borderStyle", "solid");
						    	redrawActivities(tempParentCompositeActivity);					    	
						    }
						    return tempFaultHandlers;
							break;						
						
						case WorkflowActivities.ASSIGN:
						    //trace("assign");
							var tempAssign:Assign;
						    if(parentType == WorkflowActivities.PROCESS){
						    	tempAssign = new Assign(processContainer, AssignDO(dataObject).getName(),childType, AssignDO(dataObject));
						    	tempAssign.y = processContainer.height - 40;
						    	tempAssign.x = (processContainer.width - tempAssign.width)/2;
						    	processContainer.addNewActivity(tempAssign);
						    	processContainer.height = processContainer.height + tempAssign.height + 30;
								processContainer.activityContainer.height = processContainer.activityContainer.height + tempAssign.height + 30;
						    	processContainer.activityContainer.setStyle("borderStyle", "solid");
						    }else {					      	
		                		tempParentCompositeActivity = findActivity(processContainer, parentType, parentName);
						    	//trace(tempParentCompositeActivity.name + " +++++++++ " + tempParentCompositeActivity.activityType);
						    	tempAssign = new Assign(tempParentCompositeActivity,AssignDO(dataObject).getName(),childType, AssignDO(dataObject));
						    	tempAssign.y = tempParentCompositeActivity.height - 40;
						    	if(tempAssign.width >= tempParentCompositeActivity.width){
						    		tempAssign.resetWidth(tempParentCompositeActivity.width -50);
						    	}
						    	tempAssign.x = (tempParentCompositeActivity.width - tempAssign.width)/2;
						    	tempParentCompositeActivity.addNewActivity(tempAssign);
						    	tempParentCompositeActivity.height = tempParentCompositeActivity.height + tempAssign.height + 30;
								tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + tempAssign.height + 30;
						    	tempParentCompositeActivity.activityContainer.setStyle("borderStyle", "solid");
						    	redrawActivities(tempParentCompositeActivity);					    	
						    }
						    return tempAssign;
						    break;				
						
						case WorkflowActivities.COPY:
						    //trace("copy");					      	
		                	var tempCopy:Copy;
		                	/*
						    if(parentType == WorkflowActivities.PROCESS){
						    	tempReceive = new Receive(processContainer,ReceiveDO(dataObject), processContainer.height - 40);
						    	processContainer.addNewActivity(tempReceive);
						    	processContainer.height = processContainer.height + tempReceive.height + 30;
								processContainer.activityContainer.height = processContainer.activityContainer.height + tempReceive.height + 30;
						    	processContainer.activityContainer.setStyle("borderStyle", "solid");
						    }else */{					      	
		                		tempParentCompositeActivity = findActivity(processContainer, parentType, parentName);
						    	//trace(tempParentCompositeActivity.name + " +++++++++ " + tempParentCompositeActivity.activityType);
						    	tempCopy = new Copy(tempParentCompositeActivity, CopyDO(dataObject), tempParentCompositeActivity.height - 40);
						    	tempParentCompositeActivity.addNewActivity(tempCopy);
						    	tempParentCompositeActivity.height = tempParentCompositeActivity.height + tempCopy.height + 30;
								tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + tempCopy.height + 30;
						    	tempParentCompositeActivity.activityContainer.setStyle("borderStyle", "solid");
						    	redrawActivities(tempParentCompositeActivity);
						    }
						    
						    return null;
						    break;
						    
					default: 
						trace ("Not yet implemented");				
					
				}
			}
			return null;
			//redrawActivities();
		}
		
		private function getVariablesView():Variables {
			var tempArray:Array = processContainer.subActivitiesArray;
			var tempVariablesView:Variables;
			
			//trace("tempArray[1]: " + tempArray[1]);
			if(tempArray[1]is Variables){
				//trace ("I have found Variables")
				tempVariablesView = Variables(tempArray[1]);
			}
			return tempVariablesView;
		}
		
		private function getPartnerLinksView():PartnerLinks {
			var tempArray:Array = processContainer.subActivitiesArray;
			var tempPartnerLinksView:PartnerLinks;
			
			//trace("tempArray[1]: " + tempArray[0]);
			if(tempArray[0]is PartnerLinks){
				//trace ("I have found PartnerLinks")
				tempPartnerLinksView = PartnerLinks(tempArray[0]);
			}
			return tempPartnerLinksView;
		}
		
		private function redrawActivities(currentActivity:CompositeActivity):void {
			
			var parentActivity:CompositeActivity = CompositeActivity(currentActivity.parent.parent);
			
			var tempParentCanvas:Canvas = parentActivity.activityContainer;
			
			var newYCordinate:int = 10;
			for (var index:int=0; index < tempParentCanvas.numChildren; index++){
				tempParentCanvas.getChildAt(index).y = newYCordinate;
				
				// Incrementing for the Next One
				newYCordinate = newYCordinate + tempParentCanvas.getChildAt(index).height + 20;				
			}
			parentActivity.activityContainer.height = newYCordinate + 10;
			parentActivity.activityContainer.setStyle("borderStyle", "solid");
			parentActivity.height = newYCordinate + 50;
			if(parentActivity.activityType != WorkflowActivities.PROCESS){
				redrawActivities(parentActivity);
			}
		}
		
		private function findActivity(parentActivity:CompositeActivity, activityType:String, activityName:String):CompositeActivity {
			
			var tempCompositeActivityCanvas:Canvas = parentActivity.activityContainer;
			var tempChildCompositeActivity:CompositeActivity;
			for (var index:int=0; index < tempCompositeActivityCanvas.numChildren; index++){				
				//trace(tempCompositeActivityCanvas.getChildAt(index).name);
				if(tempCompositeActivityCanvas.getChildAt(index) is CompositeActivity){
					tempChildCompositeActivity = CompositeActivity(tempCompositeActivityCanvas.getChildAt(index));
					if((tempChildCompositeActivity.activityType == activityType) && (tempChildCompositeActivity.name == activityName)){
						return tempChildCompositeActivity;
					} 
					else {
						tempChildCompositeActivity = findActivity(tempChildCompositeActivity, activityType, activityName);
						if(tempChildCompositeActivity){
							return tempChildCompositeActivity;
						}
					}
				}								
			}
			return null;
		}		
		
		/// Never used this operation ... Cool Logic went to Drain ....!
		private function resizeParentActivities(currentCompositeActivity:CompositeActivity, incrementY:int):void {
			var tempParentCompositeActivity:CompositeActivity;
			if(currentCompositeActivity.parent.parent is CompositeActivity){
				tempParentCompositeActivity = CompositeActivity (currentCompositeActivity.parent.parent);
			}
			if(tempParentCompositeActivity){
				if(tempParentCompositeActivity.activityType == WorkflowActivities.PROCESS){
					// do Nothing Assuming that parent size is changed in computeAndAndChild
				}
				else {
					tempParentCompositeActivity.height = tempParentCompositeActivity.height + incrementY;
					tempParentCompositeActivity.activityContainer.height = tempParentCompositeActivity.activityContainer.height + incrementY;
					// Here should call modified Resize.
					resizeParentActivities(tempParentCompositeActivity, incrementY);
				}
			}
		}		
	}
}