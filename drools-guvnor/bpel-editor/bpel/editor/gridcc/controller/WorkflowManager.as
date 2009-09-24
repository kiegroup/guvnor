package bpel.editor.gridcc.controller
{
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.*;
	import bpel.editor.gridcc.utility.DragNDropLogic;
	import bpel.editor.gridcc.utility.QueryString;
	import bpel.editor.gridcc.view.*;
	
	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	
   	import flash.utils.Timer;	
    		
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.net.URLRequestMethod;
	import flash.net.URLVariables;
	
	import mx.containers.Canvas;
	import mx.containers.Panel;
	import mx.controls.Alert;
	import mx.controls.Button;
	import mx.core.DragSource;
	import mx.core.IFlexDisplayObject;
	import mx.core.UIComponent;
	import mx.events.DragEvent;
	import mx.managers.DragManager;
	import mx.managers.PopUpManager;
	import mx.rpc.events.AbstractEvent;
	
	public class WorkflowManager 
	{
		protected static var instance:WorkflowManager;
			
		private static var bpelActivityPanel:ActivityPanel = null;
		
		private static var bpelEditor:BPELEditor = null;
		
		private static var processCreator:ProcessCreator = null;	
		
		private static var workflowParser:WorkflowParser = null;
		
		private static var workflowArrayParser:WorkflowArrayParser = null;	
		
		private static var bpelLoader:BPELLoader = null;	
		
		private var retrieveWorkflow:RetrieveWorkflow = null;
		
		private var currentTarget:UIComponent;
		
		private var tempParentType:String;
		
		private var tempURLRequest:URLRequest;
		
		private var minuteTimer:Timer; 
		
		
		private var uuid:String =  null;
		
		private var dirName:String =  null;
		private var fileName:String = null;
		private var servletName:String = null;
		private var workflowID:String = null;
		private var servletURL:String = "http://localhost:8888/org.drools.guvnor.Guvnor/workflowmanager"
		//"http://localhost:8082/VCRServlet/workflowmanagerservlet";
		private var WSDLServletURL:String = "http://localhost:8888/org.drools.guvnor.Guvnor/wsdlparser"
		//"http://localhost:8082/VCRServlet/wsdlparserservlet";
		
		public function WorkflowManager(){	
			
            if (WorkflowManager.instance == null) {               
                WorkflowManager.instance = this;
                
	            processCreator = ProcessCreator.getInstance();  
	            workflowParser = WorkflowParser.getInstance(); 
	            workflowArrayParser = WorkflowArrayParser.getInstance();  
	            bpelLoader = BPELLoader.getInstance();
	            setUploadDownloadVariables();     
            }             		
		}
		
		public static function getInstance():WorkflowManager
        {
            if (instance == null) {
               instance = new WorkflowManager();
            }            
            return instance;
        }
		
		private function setUploadDownloadVariables():void{
			//trace ("setUploadDownloadVariables ")
			var queryString:QueryString = new QueryString();		
			
			servletName = queryString.parameters.servletName;
			if(!servletName  || servletName == "undefined"){							
				servletURL = null;
				
				// For testing
				servletURL = "http://localhost:8888/org.drools.guvnor.Guvnor/workflowmanager";							
			} else {				
				var URLString:String = queryString.url;
				URLString = URLString.slice(0,URLString.indexOf("bpeleditor"));
				servletURL = URLString + servletName;
				WSDLServletURL = URLString + "wsdlparser"
			}
			//Alert.show("ServletURL: "+ servletURL + " WSDLServletURL: " + WSDLServletURL, 'Message');
			dirName = queryString.parameters.dirName;
			
			// For testing
			//dirName = "temp\\";
			
			fileName = queryString.parameters.fileName;	
			uuid = queryString.parameters.uuid;	
			var isNew:String = queryString.parameters.isNew;	
					
			//Alert.show("fileName: " + fileName + " uuid: " +uuid, 'Message');				
				
			// For testing
			//fileName = "CREAMPing";
			//fileName = "Full_FeedbackCorrection"
			//fileName = "Full_OneButtonMachine";
			//fileName = "Full_FeedbackCorrection_withWMProxy";
			//fileName = "Full_FeedbackCorrection_withCREAM";
			//fileName = "Ping_KRB";
			
			workflowID = queryString.parameters.wfId;
			
			// For testing
			//workflowID = "workflowID";
			
			// Alert.show("is new : " + isNew,'Messsage');
				
			// if(!fileName  || fileName == "undefined" || fileName == "WorkflowNotSelected"){				
			if(isNew != null && isNew == "true"){				
				// Alert.show("No workflow is selected", 'Message');				
			} else {
				
				retrieveWorkflow = RetrieveWorkflow.createInstance(uuid, fileName, servletURL, dirName);
				
				retrieveWorkflow.loadWorkflowFromServer("bpel");	
				if(workflowID){
				Alert.show("retrieveWorkflow.retrieveMonitoringWorkflow(workflowID)", 'Message');
					retrieveWorkflow.retrieveMonitoringWorkflow(workflowID);
				}			
			}			
			if(!servletName){
				Alert.show("Servlet managing workflow is not available", 'Message');
				//servletName = "workflowmanagerservlet";
			}
			if(!dirName){
				Alert.show("File System managing workflow is not available", 'Message');
				//dirName = "temp/";
			}			
		}
		
		public function setBPELEditor(value:BPELEditor):void {
			bpelEditor = value;
		}
		
		public function getBPELEditor():BPELEditor{
			return bpelEditor;
		}
		
		public function setbpelActivityPanel(value:ActivityPanel):void {
			bpelActivityPanel = value;
		}
		
		public function dragStart(event:MouseEvent, value:String):void {
			//trace("WorkflowManager.dragStart ");
			                               
            var dragInitiator:Button = event.currentTarget as Button;
            //var dragInitiator:Button = new Button();
            if(dragInitiator.enabled){
                           
                // Create a DragSource object.
	            var dragSource:DragSource = new DragSource();
	    			
	            // Add the data to the object.
	            dragSource.addData(value, "activity");	            
	            
	            var dragProxy:Button = new Button();
	            dragProxy.width = 80;
	            dragProxy.height = 20;                    
	    		dragProxy.label = value; 
	    		
	            // Call the DragManager doDrag() method to start the drag. 
	            DragManager.doDrag(dragInitiator, dragSource, event, dragProxy); 
            }    
		}	
		
		public function workflowUploadDownload(event:MouseEvent, value:String):void {
			/*
			var tempDirFileName:DirFileName = new DirFileName();
			tempDirFileName.loadOrSave = value;
			tempDirFileName.myDir = dirName;
			tempDirFileName.myFile = fileName;
			tempDirFileName.myServlet = servletURL;
			PopUpManager.addPopUp(tempDirFileName, bpelEditor, true);	*/
			fileName = processCreator.BPELProcess.@name;
			SaveWorkflow.getInstance(uuid, fileName, servletURL, dirName).saveWorkflowOnServer("wsdl");	
			//this.saveWorkflowOnServer("wsdl");				
		}		
		
		public function dragDropped(parentType:String, childType:String, event:DragEvent):void{
			//trace("WorkflwoManager.dragDropped: " + parentType + "  " +  childType);
			
			currentTarget = UIComponent(event.currentTarget);
			//trace(currentTarget)
			if(childType == WorkflowActivities.PROCESS){				
				PopUpManager.createPopUp(bpelEditor, MyLoginForm, true);					
			}
			
			if(DragNDropLogic.dragNdrop(parentType, childType)){
	        	switch(childType) {				
					    	
					case WorkflowActivities.SEQUENCE:
					    //trace("sequence");
					    var sequencePopup:SequencePopup = new SequencePopup();
					    var sequenceDO:SequenceDO = new SequenceDO();
					    
					    sequencePopup.setSequenceDO(sequenceDO);
					    
					     PopUpManager.addPopUp(sequencePopup, bpelEditor, true);						   
					    break;
					      
					case WorkflowActivities.INVOKE:
					    //trace("invoke");					      	
	                	
						var invokePopup:InvokePopup = new InvokePopup();
					    var invokeDO:InvokeDO = new InvokeDO();
					    
					    invokePopup.setInvokeDO(invokeDO);
					    
					     PopUpManager.addPopUp(invokePopup, bpelEditor, true);
					    break;
					      
					case WorkflowActivities.RECEIVE:
					    //trace("receive");
					    					      	
	                	var receivePopup:ReceivePopup = new ReceivePopup();
					    var receiveDO:ReceiveDO = new ReceiveDO();
					    
					    receivePopup.setReceiveDO(receiveDO);
					    
					     PopUpManager.addPopUp(receivePopup, bpelEditor, true);
					    break;
					    
					case WorkflowActivities.REPLY:
					    //trace("reply");					      	
	                	var replyPopup:ReplyPopup = new ReplyPopup();
					    var replyDO:ReplyDO = new ReplyDO();
					    
					    replyPopup.setReplyDO(replyDO);
					    
					     PopUpManager.addPopUp(replyPopup, bpelEditor, true); 
					    break;			    
										    
					case WorkflowActivities.PARTNERLINK:
					    //trace("partnerLink");					     
					    var partnerLinkPopup:PartnerLinkPopup = new PartnerLinkPopup();
					    var partnerLinkDO:PartnerLinkDO = new PartnerLinkDO();
					    
					    partnerLinkPopup.setPartnerLinkDO(partnerLinkDO);
					    
					    PopUpManager.addPopUp(partnerLinkPopup, bpelEditor, true);
					    break;
					      
					case WorkflowActivities.VARIABLE:
					    //trace("variable");				      
					    
					    // Bring PopUP for Variable
					    var variablePopup:VariablePopup = new VariablePopup();
					    var variableDO:VariableDO = new VariableDO();
					    
					    variablePopup.setVariableDO(variableDO);
					    PopUpManager.addPopUp(variablePopup, bpelEditor, true);
					    break;
					
					case WorkflowActivities.ASSIGN:					
					    //trace("assign");				      
					    
					    // Bring PopUP for Assign
					    var assignPopup:AssignPopup = new AssignPopup();
					    var assignDO:AssignDO = new AssignDO();
					    var assignToDO:ToDO = new ToDO();
					    var assignFromDO:FromDO = new FromDO();
					    					    
					    assignPopup.setAssignDO(assignDO);
					    assignPopup.setToDO(assignToDO);
					    assignPopup.setFromDO(assignFromDO);
					    PopUpManager.addPopUp(assignPopup, bpelEditor, true);
					    break;
						
					case WorkflowActivities.COPY:
					/*
					    trace("copy");				      
					    
					    // Bring PopUP for Variable
					    var variablePopup:VariablePopup = new VariablePopup();
					    var variableDO:VariableDO = new VariableDO();
					    
					    //variableDO.updateAttributesArray("name","Asif");					    
					    //variableDO.updateAttributesArray("type","");
					    
					    variablePopup.setVariableDO(variableDO);
					    PopUpManager.addPopUp(variablePopup, bpelEditor, true); */
					    break;	
					    
					case WorkflowActivities.WAIT:					
					    //trace("wait");				      
					    
					    // Bring PopUP for Variable
					    var waitPopup:WaitPopup = new WaitPopup();
					    var waitDO:WaitDO = new WaitDO();
					    					    
					    waitPopup.setWaitDO(waitDO);
					    PopUpManager.addPopUp(waitPopup, bpelEditor, true); /* */
					    break;	
					    
					case WorkflowActivities.WHILE:					
					    //trace("while");				      
					   
					    // Bring PopUP for While
					    var whilePopup:WhilePopup = new WhilePopup();
					    var whileDO:WhileDO = new WhileDO();					   
					    
					    whilePopup.setWhileDO(whileDO);
					    PopUpManager.addPopUp(whilePopup, bpelEditor, true); 
					    break;								
						
					default:
					    trace("Not Allowed Activity Structured Activity");					      
	                }  
                }
		}
		
		public function popUPOKHandler(popupWindow:IFlexDisplayObject, type:String ):void{
			var process:XML;
			var tempCompositeActivity:CompositeActivity;
			
			//var bpelLoader:BPELLoader = BPELLoader.getInstance();
			if(currentTarget.parent is CompositeActivity){
				tempCompositeActivity = CompositeActivity(currentTarget.parent);
				//trace("***** " + tempCompositeActivity.activityType + "  " + tempCompositeActivity.name);
			}
			
			switch(type){
				
				case WorkflowActivities.PROCESS:
					var loginForm:MyLoginForm = MyLoginForm(popupWindow);				
					
					process = processCreator.creatProcess(loginForm.workflowName.text,loginForm.targetNamespace.text,loginForm.workflowType.text );
					fileName = processCreator.BPELProcess.@name;
					//trace("popUPOKHandler fileName: " + fileName);
            		bpelActivityPanel.isEditable = true;
            		//trace(process);
            		
            		// This is for the first time when workflow is created or any workflow is loaded
            		bpelLoader.parseWorkflow(processCreator.BPELProcess);
            		
            		/* Using Old API workflowParser works on XML
            		*/
            		//var tempParent:CompositeActivity = workflowParser.parseWorkflow(process, currentTarget.width);
            		//trace(tempParent.width + "  " + tempParent.height);
            		
            		//printSubActivities(bpelLoader.processDO.subActivitiesArray, "  ");
            		// workflowArrayParser works on the Activity Array created by BPEL Loader
            		var tempParent:CompositeActivity = workflowArrayParser.parseWorkflowArray();          		
            		
            		tempParent.x = ((currentTarget.width - tempParent.width)/2);
            		if(tempParent.height < currentTarget.height){
            			tempParent.resetHeight(currentTarget.height);
            		}
            		UIComponent(currentTarget).addChild(tempParent);  
            		          		
            		break;
            		
            	case WorkflowActivities.VARIABLE:
            		var variablePopup:VariablePopup = VariablePopup(popupWindow);
            		//variablePopup.variableDO.printArray();
            		
            		processCreator.createNewActivity(WorkflowActivities.VARIABLES, "", 
            			WorkflowActivities.VARIABLE, variablePopup.variableDO.attributesArray);
            		
            		if(!variablePopup.modifyable)
            			workflowArrayParser.computeAndAddChild(WorkflowActivities.VARIABLES, "", 
            				WorkflowActivities.VARIABLE, variablePopup.variableDO);
            		//trace(processCreator.BPELProcess);
            		break;
            		
            	case WorkflowActivities.INVOKE:
					//trace("invoke");
										      	
	            	var invokePopup:InvokePopup = InvokePopup(popupWindow);            		
            		
            		//invokePopup.invokeDO.printArray();
            		if(tempCompositeActivity)
            			processCreator.createNewActivity(tempCompositeActivity.activityType, tempCompositeActivity.name, WorkflowActivities.INVOKE,invokePopup.invokeDO.attributesArray);
            		
            		if(!invokePopup.modifyable)
            		workflowArrayParser.computeAndAddChild(tempCompositeActivity.activityType, 
            			tempCompositeActivity.name, WorkflowActivities.INVOKE, invokePopup.invokeDO);
            		
					break;
					      
				case WorkflowActivities.RECEIVE:				
					//trace("receive");					      	
	                var receivePopup:ReceivePopup = ReceivePopup(popupWindow);            		
            		
            		if(tempCompositeActivity)
            			processCreator.createNewActivity(tempCompositeActivity.activityType, tempCompositeActivity.name, 
            				WorkflowActivities.RECEIVE, receivePopup.receiveDO.attributesArray);
            		
            		if(!receivePopup.modifyable)
	            		workflowArrayParser.computeAndAddChild(tempCompositeActivity.activityType, 
	            			tempCompositeActivity.name, WorkflowActivities.RECEIVE, receivePopup.receiveDO);
            		
					break;
				    
				case WorkflowActivities.REPLY:
					//trace("reply");					      	
	                var replyPopup:ReplyPopup = ReplyPopup(popupWindow);            		
            		
            		if(tempCompositeActivity)
	            		processCreator.createNewActivity(tempCompositeActivity.activityType, 
	            			tempCompositeActivity.name, WorkflowActivities.REPLY, replyPopup.replyDO.attributesArray);
            		
            		if(!replyPopup.modifyable)
	            		workflowArrayParser.computeAndAddChild(tempCompositeActivity.activityType, 
	            			tempCompositeActivity.name, WorkflowActivities.REPLY, replyPopup.replyDO);
            		
					break;
					    
				case WorkflowActivities.ASSIGN:
					//trace("assign");					      	
	                var assignPopup:AssignPopup = AssignPopup(popupWindow);            		
            		
            		processCreator.createNewActivity(tempCompositeActivity.activityType, 
            			tempCompositeActivity.name, WorkflowActivities.ASSIGN, assignPopup.assignDO.attributesArray);
            		
            		tempCompositeActivity = (workflowArrayParser.computeAndAddChild(tempCompositeActivity.activityType, 
            			tempCompositeActivity.name, WorkflowActivities.ASSIGN, assignPopup.assignDO));
            			
            		var tempCopyDO:CopyDO = new CopyDO();
            		tempCopyDO.updateSubActivitiesArray("from", assignPopup.fromDO );
            		tempCopyDO.updateSubActivitiesArray("to", assignPopup.toDO);
            		
            		processCreator.createNewCopy(tempCompositeActivity.activityType, 
            			tempCompositeActivity.name, tempCopyDO);
            			
            		workflowArrayParser.computeAndAddChild(tempCompositeActivity.activityType, 
            			tempCompositeActivity.name, WorkflowActivities.COPY, tempCopyDO)
            			
					break;
					    
				case WorkflowActivities.PARTNERLINK:
					if(!tempCompositeActivity) {	
						// Not a clean logic but working for time Being ....!  
						var tempPartnerLinkDO:PartnerLinkDO;
						if(popupWindow is PartnerLinkPopup){
							var partnerLinkPopup:PartnerLinkPopup = PartnerLinkPopup(popupWindow);  
							tempPartnerLinkDO =  partnerLinkPopup.partnerLinkDO
		            	} else if(popupWindow is PartnerLinkWSDLPopup){
		            		var partnerLinkWSDLPopup:PartnerLinkWSDLPopup = PartnerLinkWSDLPopup(popupWindow);  
							tempPartnerLinkDO =  partnerLinkWSDLPopup.partnerLinkDO
		            	}
						
		        		processCreator.createNewActivity(WorkflowActivities.PARTNERLINKS, "", 
		        			WorkflowActivities.PARTNERLINK,tempPartnerLinkDO.attributesArray);
		        		
		        		//workflowArrayParser.computeAndAddChild(WorkflowActivities.PARTNERLINKS, "",
	        			//	WorkflowActivities.PARTNERLINK, tempPartnerLinkDO);
	    			}
					break;						
						
				case WorkflowActivities.SEQUENCE:
					var sequencePopup:SequencePopup = SequencePopup(popupWindow);            		
            		
            		processCreator.createNewActivity(tempCompositeActivity.activityType, 
            			tempCompositeActivity.name, WorkflowActivities.SEQUENCE,sequencePopup.sequenceDO.attributesArray);
            		
            		workflowArrayParser.computeAndAddChild(tempCompositeActivity.activityType, 
            			tempCompositeActivity.name, WorkflowActivities.SEQUENCE, sequencePopup.sequenceDO);
            		
					break;
					
				case WorkflowActivities.WHILE:
					var whilePopup:WhilePopup = WhilePopup(popupWindow);            		
            		
            		processCreator.createNewActivity(tempCompositeActivity.activityType, 
            			tempCompositeActivity.name, WorkflowActivities.WHILE, 
            			whilePopup.whileDO.attributesArray);
            		
            		workflowArrayParser.computeAndAddChild(tempCompositeActivity.activityType, 
            			tempCompositeActivity.name, WorkflowActivities.WHILE, whilePopup.whileDO);
            		
					break;
				
				case WorkflowActivities.WAIT:
					var waitPopup:WaitPopup = WaitPopup(popupWindow);            		
            		
            		processCreator.createNewActivity(tempCompositeActivity.activityType, 
            			tempCompositeActivity.name, WorkflowActivities.WAIT, 
            			waitPopup.waitDO.attributesArray);
            		
            		if(!waitPopup.modifyable)
	            		workflowArrayParser.computeAndAddChild(tempCompositeActivity.activityType, 
	            			tempCompositeActivity.name, WorkflowActivities.WAIT, waitPopup.waitDO);
            		
					break;
				default:
					trace("Not implemented yet");
						
			}		
			
			var processDO:ProcessDO = ProcessDO.getInstance();	
			printSubActivities(processDO.subActivitiesArray, " ");		
		}	
		
		public function updateNameForMonitoring(activityType:String, 
			activityOldName:String, activityNewName:String):Boolean{			
			return processCreator.modifyNameForMonitoring(activityType, activityOldName, activityNewName);
		}
		
		// Only for testing purposes		
		private function printSubActivities(array:Array, indention:String):void{
			indention = indention + "  ";
			
			for(var i:int = 0; i < array.length; i++){
				
				switch(array[i][0]){
					case WorkflowActivities.PARTNERLINKS:
						trace(indention + array[i][0]);
						var partnerLinksDO:PartnerLinksDO = PartnerLinksDO (array[i][1]);
						printSubActivities(partnerLinksDO.subActivitiesArray, indention);
						break;
						
					case WorkflowActivities.VARIABLES:
						trace(indention + array[i][0]);
						var variablesDO:VariablesDO = VariablesDO (array[i][1]);
						printSubActivities(variablesDO.subActivitiesArray, indention);
						break;
						
					case WorkflowActivities.SEQUENCE:
						trace(indention + array[i][0]);
						var sequenceDO:SequenceDO = SequenceDO (array[i][1]);
						if(sequenceDO.subActivitiesArray){
							printSubActivities(sequenceDO.subActivitiesArray, indention);
						}
						break;
					
					case WorkflowActivities.WHILE:
						trace(indention + array[i][0]);
						var whileDO:WhileDO = WhileDO (array[i][1]);
						if(whileDO.subActivitiesArray){
							printSubActivities(whileDO.subActivitiesArray, indention);
						}
						break;
						
					default:
					   trace(indention + array[i][0]);					
				}
			}
		}
		
		public function retrieveBPEL():XML{	
			//processCreator.printNameSpaceArray();			
			return processCreator.BPELProcess;
		}	
		
		public function retrieveWSDL():XML{
			//trace(processCreator.BPELWSDL);
			var wsdlCreator:WSDLCreator = WSDLCreator.getInstance();			
			return wsdlCreator.WorkflowWSDL;
		}  
		
		public function retrievePDD():XML{
			//trace(processCreator.retrievePDD);
			var pddCreator:PDDCreator = PDDCreator.getInstance();
			if(pddCreator != null)
				return pddCreator.WorkflowPDD;
			return null;
		} 
		
		public function retrieveWSDLCatalog():XML{
			var wsdlCatalog:WSDLCatalogCreator = WSDLCatalogCreator.getInstance();
			if(wsdlCatalog != null){
				return wsdlCatalog.WSDLCatalog;
			}
			return null;
		} 
		
		public function retrieveSubDoc():XML{
			var subDoc:SubmissionDocumentCreator = SubmissionDocumentCreator.getInstance();
			if(subDoc != null){
				return subDoc.SubmissionDocument;
			}
			return null;
		}
		
		public function retrieveQoSDoc():XML{
			var qosDoc:QoSCreator = QoSCreator.getInstance();
			if(qosDoc != null){
				return qosDoc.QoSDocument;
			}
			return null;
		}
				
		public function setDirAndFileName(popupWindow:IFlexDisplayObject):void{
			var dirFileName:DirFileName = DirFileName(popupWindow);
			dirName =  dirFileName.dir.text;
			fileName = dirFileName.fileName.text;
		
			servletURL = dirFileName.servletURL.text;
			
			if(dirFileName.loadOrSave == "SAVE"){
				// calls save function
				SaveWorkflow.getInstance(uuid, fileName, servletURL, dirName).saveWorkflowOnServer("wsdl");
			} else if(dirFileName.loadOrSave == "LOAD"){
				// call Load function
				RetrieveWorkflow.createInstance(uuid, fileName, servletURL, dirName).loadWorkflowFromServer("wsdl");
				//loadWorkflowFromServer("wsdl");
			}
			else {
				trace("(setDirAndFileName) Should not reach here ...!");
			}
		}
		
		public function createGUIFromBPELProcess():void {		
			
    		//trace(process);
    		
    		// This is for the first time when workflow is created or any workflow is loaded
    		bpelLoader.parseWorkflow(processCreator.BPELProcess);
    		
    		/* 
    		** Using Old API workflowParser works on XML
    		*/
    		//var tempParent:CompositeActivity = workflowParser.parseWorkflow(process, currentTarget.width);
    		//trace(tempParent.width + "  " + tempParent.height);
    		//printSubActivities(bpelLoader.processDO.subActivitiesArray, "  ");
    		// workflowArrayParser works on the Activity Array created by BPEL Loader
    		var tempParent:CompositeActivity = workflowArrayParser.parseWorkflowArray();  
    		
    		// hard coded main canvas
    		if(bpelEditor){
    			//if(bpelEditor.mainCanvas) {   		
		    		currentTarget = bpelEditor.mainCanvas;
		    		for(var children:Number = currentTarget.numChildren ; children > 0; children--){
		    			currentTarget.removeChildAt(children);
		    		}    		
		    		
		    		if(tempParent.width > currentTarget.width){
		    			//currentTarget.width = tempParent.width + 10;
		    		}
		    		
		    		tempParent.x = ((currentTarget.width - tempParent.width)/2);
		    		if(tempParent.height < currentTarget.height){
		    			tempParent.resetHeight(currentTarget.height);
		    		}
		    		UIComponent(currentTarget).addChild(tempParent);
		            
		            if(bpelActivityPanel)	{			
						bpelActivityPanel.isEditable = true;
					} else {
						bpelEditor.BPELActivitiesPanel.isEditable = true;
					}
					
					var processDO:ProcessDO = ProcessDO.getInstance();	
					//printSubActivities(processDO.subActivitiesArray, " ");		
					//MonitoringWorkflow.getInstance(null).getMonitoringWorkflow();	
    			//}
    		} else {
    			minuteTimer = new Timer(200, 1);            	            	
				minuteTimer.addEventListener(TimerEvent.TIMER_COMPLETE, onTimerComplete);
				minuteTimer.start();
    		}
		}
		
		public function onTimerComplete(evt:Event):void {
			trace("Timer Started");
			createGUIFromBPELProcess();
		}
		public function displayWSDLPopUp():void {
			
			if(fileName){
				var wsdlPopUp:WSDLPopUp  = new WSDLPopUp();
				wsdlPopUp.workflowName = fileName;
				PopUpManager.addPopUp(wsdlPopUp, bpelEditor, true);
			} else {
				Alert.show("Please create workflow before using Web Service Registry");
			}			
		}
		
		private var tempwsdlPopUp:WSDLPopUp = null;
		public function WSDLPopUPOKButtonHandler(popupWindow:IFlexDisplayObject):void{
			tempwsdlPopUp = WSDLPopUp (popupWindow);
			//String wsdlPopUp.webServiceName
			tempURLRequest = new URLRequest(WSDLServletURL);
			var operationString:String = "operation=getFromURL&";
			var fullPath:String = "fullPath="+dirName+"&";
			var WorkflowName:String = "WorkflowName=" + tempwsdlPopUp.workflowName + "&";
			var WSName:String = "WSname=" + tempwsdlPopUp.webServiceName.text +"&";
			var WSDLURL:String = "WSDLURL="+tempwsdlPopUp.wsdlURL.text;
			
			var tempWSDLLoader:URLLoader = new URLLoader();
			var tempVariables2:URLVariables = new URLVariables(operationString + fullPath + WorkflowName + WSName + WSDLURL);
			tempWSDLLoader.addEventListener(Event.COMPLETE, WSDLParserHandler);
			tempWSDLLoader.addEventListener(IOErrorEvent.IO_ERROR, catchIOError);			

			tempURLRequest.data = tempVariables2;
			tempWSDLLoader.load(tempURLRequest);
		}
		
		public function WSDLParserHandler(event:Event):void{
			try{
				var tempXML:XML = new XML (event.target.data);
				var webServiceRegistry:WebServiceRegistry = WebServiceRegistry.getInstance();
				webServiceRegistry.WSRegistry.newElement = tempXML;
				bpelEditor.updateWSRegistryTree(webServiceRegistry.WSRegistry);
				
				// Should change
				VCRServletWSDLRetrieval();
			}
			catch (exception:*){
				Alert.show("Error in Parsing WSDL");
				trace(exception);
			}
			//wsRegistryTree			
		}
		
		// This method is used when VCR retrieves external WSDL
		private function VCRServletWSDLRetrieval():void {
			tempURLRequest = new URLRequest(servletURL);
			var operationString:String = "operation=getFromURL&";
			var fullPath:String = "fullPath="+dirName+"&";
			var WorkflowName:String = "workflowName=" + tempwsdlPopUp.workflowName + "&";			
			var WSName:String = "fileName=" + tempwsdlPopUp.webServiceName.text +".wsdl&";
			var WSDLURL:String = "url="+tempwsdlPopUp.wsdlURL.text;
			
			var tempWSDLLoader:URLLoader = new URLLoader();
			var tempVariables2:URLVariables = new URLVariables(operationString + fullPath + WorkflowName + WSName + WSDLURL);
			tempWSDLLoader.addEventListener(Event.COMPLETE, VCRServletWSDLRetrievalHandler);
			tempWSDLLoader.addEventListener(IOErrorEvent.IO_ERROR, catchIOError);			

			tempURLRequest.data = tempVariables2;
			tempWSDLLoader.load(tempURLRequest);
		}
		
		public function VCRServletWSDLRetrievalHandler(event:Event):void{
		
		}
		private function catchIOError(event:IOErrorEvent):void {
		    Alert.show("Error in Parsing WSDL "+ event.type);
		}
		
		public function addNewPartner(WSName:String, portName:String, WSNamespace:String, operationNames:Array):void {		
			
			if(bpelEditor.mainCanvas.numChildren == 1){
				if( bpelEditor.mainCanvas.getChildAt(0) is Process){				
				
					this.currentTarget = getPartnerLinksCanvas();
					trace("partnerLink");					     
				    var partnerLinkPopup:PartnerLinkWSDLPopup = new PartnerLinkWSDLPopup();
				    var partnerLinkDO:PartnerLinkDO = new PartnerLinkDO();
				    
				    var tempArray:Array = new Array();
		        	tempArray.push(["name", WSName + "_" + portName + "_PL"]);
		        	tempArray.push(["partnerLinkType", WSName + ":" + WSName + "_" + portName + "_PLT"]);
		        	tempArray.push(["myRole",""]);
		        	tempArray.push(["partnerRole",WSName + "_" + portName + "_Provider"]);
		        	
		        	partnerLinkDO.attributesArray = tempArray;            	
				    partnerLinkPopup.setPartnerLinkDO(partnerLinkDO);
				    
				    PopUpManager.addPopUp(partnerLinkPopup, bpelEditor, true);
				    
				    
				    processCreator.addNewNamespace(WSName, WSNamespace);
				    
				    WSDLCreator.getInstance().addNewNamespace(WSName, WSNamespace);
				    WSDLCreator.getInstance().addNewPartnerLink(WSName, WSNamespace,portName);
				    
				    PDDCreator.getInstance().createPartnerLink(WSName, false, false);
				    PDDCreator.getInstance().createWSDL(WSName, WSNamespace);
				    
				    SubmissionDocumentCreator.getInstance().createParameter("trigger", "xs:string", "Enter URL", WSName + "_PL");
				    
				    WSDLCatalogCreator.getInstance().createWSDLEntry(WSName);
				    
				    var plPortMapping:PLPortMapping	= PLPortMapping.getInstance();
				    plPortMapping.addNewMapping(WSName + "_" + portName + "_PL", WSName + ":" + portName, operationNames);
			 	}
		 	} else {
		 		Alert.show("Please First create BPEL Process");
		 	}
		}
		
		private function getPartnerLinksCanvas():Canvas {
			var tempProcess:Process = Process(bpelEditor.mainCanvas.getChildAt(0));
			var tempProcessCanvas:Canvas = Canvas(tempProcess.getChildAt(1));
			
			var tempPartnerLinks:PartnerLinks = PartnerLinks(tempProcessCanvas.getChildAt(0));
			
			var tempPartnerLinksCanvas:Canvas = Canvas(tempPartnerLinks.getChildAt(1));
			return tempPartnerLinksCanvas;
		}
		
		private function getVariablessCanvas():Canvas {
			var tempProcess:Process = Process(bpelEditor.mainCanvas.getChildAt(0));
			var tempProcessCanvas:Canvas = Canvas(tempProcess.getChildAt(1));
			
			var tempVariables:Variables = Variables (tempProcessCanvas.getChildAt(1));			
			var tempVariablesCanvas:Canvas = Canvas(tempVariables.getChildAt(1));
			
			return tempVariablesCanvas;
		}
		
		private function updateWSDL():void {
		
		}
		
		private function updateBPELProcess():void {
		
		}
		
		private function updatePDD():void {
		
		}
		
		private function updateWSDLCatalog():void {
		
		}
		
		private function updateSubDoc():void {
		
		}
		
		public function getMonitoringBPELArray():Array{
			var monitoringBPEL:MonitoringWorkflow = null;
			if(workflowID){				
				if(!retrieveWorkflow){
					retrieveWorkflow = RetrieveWorkflow.createInstance(uuid, fileName, servletURL, dirName);
				}
				retrieveWorkflow.retrieveMonitoringWorkflow(workflowID);
				monitoringBPEL = MonitoringWorkflow.getInstance(workflowID);							
			} else {
				monitoringBPEL = MonitoringWorkflow.getInstance(null);	
				
			}
			return monitoringBPEL.getMonitoringArray();
		}
		
		public function createQoSDocument(parametersArray:Array, aatributesArray:Array, type:String):void{
			QoSCreator.createInstance(parametersArray, aatributesArray, type);
		}
	}	
}