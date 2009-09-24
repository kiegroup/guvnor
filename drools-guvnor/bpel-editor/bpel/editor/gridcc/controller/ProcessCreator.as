package bpel.editor.gridcc.controller
{
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.*;
	import bpel.editor.gridcc.utility.*;
	
	import flash.net.URLLoader;
	import flash.events.Event;
	import flash.net.URLLoaderDataFormat;
	import flash.net.URLRequest;
	import mx.controls.Alert;
	
	//import mx.charts.chartClasses.StackedSeries;
	
	public class ProcessCreator 	{		
		/**
         * The Singleton instance of ProcessCreator
         */        
        protected static var instance:ProcessCreator;
        
        [Bindable]
        public var BPELProcess:XML;  
        
        [Bindable]
        public var BPELPDD:XML;      
        
        [Bindable]
        public var BPELWSDL:XML; 
        
        private var process:ProcessDO; 
        
        private var localWorkflowName:String;       
        
        /**
         * Constructor
         * 
         * Instantiates Singleton instance of ProcessCreator
         */        
        public function ProcessCreator()
        {
            if (ProcessCreator.instance == null) 
            {                
                ProcessCreator.instance = this;
            }
        }
        
        /**
         * Determines if the singleton instance of ProcessCreator
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of ProcessCreator
         */        
        public static function getInstance():ProcessCreator
        {
            if (instance == null) 
            {
               instance = new ProcessCreator();
            }            
            return instance;
        }
        
		public function creatProcess(workflowName:String, targetNamespace:String, type:String):XML {
			localWorkflowName = workflowName;
			var tempAttributeArray:Array; 
			var childXML:XML;
			loadWSDL(type, workflowName);
			switch(type){
				
				case "synchronous":					
					createProcess(workflowName,targetNamespace);
					
					createPartnerLinks();
					createPartnerLink(workflowName, true);									
					
					createVariables();
					createInputVariable(workflowName);
					createOutputVariable(workflowName);
					
					createSequence("default", true);					
					createReceive(WorkflowActivities.SEQUENCE, "default", workflowName);
					
					createSequence("main", false);
					createAssign(WorkflowActivities.SEQUENCE, "main", workflowName);
					
					createReply(WorkflowActivities.SEQUENCE, "default", workflowName);
					
					break;
					
				case "asynchronous":
					createProcess(workflowName,targetNamespace);
					
					createPartnerLinks();	
					createPartnerLink(workflowName, false);					
					
					createVariables();					
					createInputVariable(workflowName);
					createOutputVariable(workflowName);
					
					createSequence("default", true);
					createReceive(WorkflowActivities.SEQUENCE, "default", workflowName);
					
					// It should not be receive .. should be reply
					createReply(WorkflowActivities.SEQUENCE, "default", workflowName);					
					break;
				
				case "empty":
					createProcess(workflowName,targetNamespace);
					
					createPartnerLinks();
					createPartnerLink(workflowName, true);
					
					createVariables();
					createInputVariable(workflowName);
					createOutputVariable(workflowName);
					break;				
			}
			PDDCreator.createInstance(workflowName,targetNamespace);
			WSDLCatalogCreator.createInstance(workflowName);
			SubmissionDocumentCreator.createInstance(workflowName,targetNamespace);	
			
			var plPortMapping:PLPortMapping	= PLPortMapping.getInstance();
			var tempOperationsArray:Array = new Array();
			tempOperationsArray.push("process");
			plPortMapping.addNewMapping(workflowName + "_PL", "client:" + workflowName + "_PT", tempOperationsArray);
			
			trace(plPortMapping.addOperationMessage("process", 
				"client:" + workflowName + "RequestMessage","client:" + workflowName + "ResponseMessage",""));
				
			return BPELProcess;
		}
		
		private function loadWSDL(processType:String, workflowName:String):void {
			var wsdlCreator:WSDLCreator = WSDLCreator.getInstance();
			wsdlCreator.loadWSDL(processType,workflowName);
			//trace(wsdlCreator.WorkflowWSDL);
		}
		
		private function handleComplete(event:Event):void{
			try{
				//trace("File Loaded");
				BPELWSDL = new XML(event.target.data);
				//trace(BPELWSDL.toXMLString().replace(/DUMMY_PROCESS/g,localWorkflowName));
				BPELWSDL = new XML(BPELWSDL.toXMLString().replace(/DUMMY_PROCESS/g,localWorkflowName));
				//trace(BPELWSDL);
			}catch (e:TypeError){
				trace(e.message);
			}
		}
		
		private function createProcess(workflowName:String, targetNamespace:String):void {	
			process = ProcessDO.getInstance();
			process.fillNamespaceArray("http://gridcc.org/workflows/" + workflowName);
			
			//var bpws:Namespace = new Namespace("http://schemas.xmlsoap.org/ws/2003/03/business-process/");
			//default xml namespace = bpws;		
			BPELProcess = new XML("<process/>");
			BPELProcess.@name=workflowName;
			BPELProcess.@targetNamespace=targetNamespace;
			//BPELProcess.
			addProcessNamespaces(process.namespaceArray);								
		}
		
		private function createPartnerLinks():void {			
			var tempPartnerLinks:XML = this.createChild(WorkflowActivities.PARTNERLINKS, null);	
			this.findParentElement(WorkflowActivities.PROCESS, null, tempPartnerLinks);	
						
		}
		
		private function createPartnerLink(workflowName:String, synchronous:Boolean):void {
			var tempOperationNameArray:Array = new Array();			
			tempOperationNameArray.push("process");	
			this.createPartnerLinkType(workflowName,workflowName,"client",tempOperationNameArray,workflowName+"Provider");
			var tempAttributeArray:Array = new Array();
			tempAttributeArray.push(["name", workflowName + "_PL"]);						
			tempAttributeArray.push(["partnerLinkType","client:" + workflowName + "_PLT"]);
			tempAttributeArray.push(["myRole",workflowName + "Provider"]);
			
			if(!synchronous)
				tempAttributeArray.push(["partnerRole", workflowName + "Requester"]);
			var childXML:XML = createChild(WorkflowActivities.PARTNERLINK, tempAttributeArray);
			
			this.findParentElement(WorkflowActivities.PARTNERLINKS, null, childXML);						
		}
		
		
		private function createVariables():void {
			var tempVariables:XML = this.createChild(WorkflowActivities.VARIABLES, null);	
			this.findParentElement(WorkflowActivities.PROCESS, null, tempVariables);				
		}
		
		private function createInputVariable(workflowName:String):void {
			var tempAttributeArray:Array = new Array();				
			
			tempAttributeArray.push(["name", "inputVariable"]);
			tempAttributeArray.push(["messageType","client:" + workflowName + "RequestMessage"]);										
			var childXML:XML = createChild(WorkflowActivities.VARIABLE, tempAttributeArray);			
			
			this.findParentElement(WorkflowActivities.VARIABLES, null, childXML);		
		}
		
		private function createOutputVariable(workflowName:String):void {
			var tempAttributeArray:Array = new Array();				
			
			tempAttributeArray.push(["name", "outputVariable"]);
			tempAttributeArray.push(["messageType","client:" + workflowName + "ResponseMessage"]);										
			var childXML:XML = createChild(WorkflowActivities.VARIABLE, tempAttributeArray);			
			
			this.findParentElement(WorkflowActivities.VARIABLES, null, childXML);		
		}
		
		private function createSequence(name:String, process:Boolean):void {	
			var tempAttributeArray:Array = new Array();				
			
			tempAttributeArray.push(["name", name]);
			var childXML:XML = createChild(WorkflowActivities.SEQUENCE, tempAttributeArray);
			if(process){
				this.findParentElement(WorkflowActivities.PROCESS, null, childXML);	
			} else {
				this.findParentElement(WorkflowActivities.SEQUENCE, "default", childXML);	
			}
		}
		
		private function createReply(parentType:String, parentName:String, workflowName:String):void {			
			var tempAttributeArray:Array = new Array();	
			tempAttributeArray.push(["name", "replyOutput"]);	
			tempAttributeArray.push(["partnerLink", workflowName + "_PL"]);
			tempAttributeArray.push(["portType", "client:" + workflowName + "_PT"]);
			tempAttributeArray.push(["operation","process"]);			
			tempAttributeArray.push(["variable","outputVariable"]);				
			
			var childXML:XML = createChild(WorkflowActivities.REPLY, tempAttributeArray);
			
			this.findParentElement(parentType, parentName, childXML);				
		}
		
		private function createReceive(parentType:String, parentName:String, workflowName:String):void {
			var tempAttributeArray:Array = new Array();	
			tempAttributeArray.push(["name", "receiveInput"]);	
			tempAttributeArray.push(["partnerLink", workflowName + "_PL"]);
			tempAttributeArray.push(["portType", "client:" + workflowName + "_PT"]);
			tempAttributeArray.push(["operation","process"]);			
			tempAttributeArray.push(["variable","inputVariable"]);
			tempAttributeArray.push(["createInstance", "yes"]);
			
			var childXML:XML = createChild(WorkflowActivities.RECEIVE, tempAttributeArray);
			
			this.findParentElement(parentType, parentName, childXML);							
		}
		
		private function createAssign(parentType:String, parentName:String, workflowName:String):void {
			var tempAttributeArray:Array = new Array();	
			tempAttributeArray.push(["name", "defaultAssign"]);	
			
			var childXML:XML = createChild(WorkflowActivities.ASSIGN, tempAttributeArray);
			this.findParentElement(parentType, parentName, childXML);
			this.createCopy(WorkflowActivities.ASSIGN, "defaultAssign", workflowName);			
		}
		
		private function createCopy(parentType:String, parentName:String, workflowName:String):void {
			var tempAttributeArray:Array = new Array();	
			//tempAttributeArray.push(["name", "defaultAssign"]);	
			
			var childXML:XML = createChild(WorkflowActivities.COPY, tempAttributeArray);			
			
			tempAttributeArray = new Array();	
			tempAttributeArray.push(["part", "payload"]);
			tempAttributeArray.push(["variable", "inputVariable"]);
			var fromXML:XML = createChild(WorkflowActivities.FROM, tempAttributeArray);		
			childXML.appendChild(fromXML);
			
			tempAttributeArray = new Array();	
			tempAttributeArray.push(["part", "payload"]);
			tempAttributeArray.push(["variable", "outputVariable"]);
			var toXML:XML = createChild(WorkflowActivities.TO, tempAttributeArray);	
			childXML.appendChild(toXML);
			
			this.findParentElement(parentType, parentName, childXML);
		}
		// This will not be used and should not be used 
		// unless I change the format of initial BPEL workflow
		// Should Be used for Asynch BPEL Process ...!
		private function createInvoke(name:String, parent:XML):void {
			//var bpws:Namespace = new Namespace("http://schemas.xmlsoap.org/ws/2003/03/business-process/");
			//default xml namespace = bpws;			
			parent.newElement = <invoke/>;	
			parent.invoke.@name=name;	
			/*
			var tempAttributeArray:Array = new Array();	
			tempAttributeArray.push(["name", "receiveInput"]);	
			tempAttributeArray.push(["partnerLink", "client"]);
			tempAttributeArray.push(["portType", "client:" +workflowName]);
			tempAttributeArray.push(["operation","initiate"]);			
			tempAttributeArray.push(["variable","inputVariable"]);
			tempAttributeArray.push(["createInstance", "yes"]);
			
			var childXML:XML = createChild(WorkflowActivities.RECEIVE, tempAttributeArray);
			
			this.findParentElement(parentType, parentName, childXML);
			*/	
		}
		
		private function addProcessNamespaces(namespacesArray:Array):void{
			//trace("ProcessCreator addProcessNamespaces");
			for(var i:Number = 0; i < namespacesArray.length; i++){
				BPELProcess.addNamespace(Namespace(namespacesArray[i]));				
			}
			
			// Hard Coded default Namespace
			BPELProcess.@xmlns="http://schemas.xmlsoap.org/ws/2003/03/business-process/";			
		}
		
		public function printNameSpaceArray():void {
			var tempNamespace:Array = BPELProcess.namespaceDeclarations();
			for(var i:int = 0; i < tempNamespace.length; i++){
				if( tempNamespace[i] is Namespace){
					trace(tempNamespace[i].prefix + "  " + tempNamespace[i].toString());
				}
			}			
		}
		
		public function getTargetNamespace():String{
			var attList:XMLList = BPELProcess.attributes();
			//trace(BPELProcess.@targetNamespace);
			for (var i:int = 0; i < attList.length(); i++) { 			    
			    if(attList[i].name() == "targetNamespace"){
			    	//trace (attList[i].name() + " " + attList[i]);
			    	return attList[i];
			    }
			    
			}
			return null;
		}
		
		private function findParentElement(parentType:String, parentName:String, childXML:XML ):Boolean {
			//trace("ProcessCreator findParentElement");
			//trace( parentType + "   " + parentName);
			//trace( childXML);
			//trace("Target Namespace" + BPELProcess.@targetNamespace)
			
			var bpws:Namespace = new Namespace("http://schemas.xmlsoap.org/ws/2003/03/business-process/");
			
			var tempParentXML:XML;
			var tempXMLList:XMLList;
			var item:XML;
			var tempString:String;
			var returnBoolean:Boolean = true;
			
			switch(parentType) {
				
				case WorkflowActivities.PROCESS:
					//trace("process");							
					BPELProcess.appendChild(childXML);										
					break;
					
				case WorkflowActivities.PARTNERLINKS:
					//trace("partnerLinks");
					tempXMLList = BPELProcess..bpws::partnerLinks;
					
					// There can be only one PartnerLinks sub element of Process					
					item = tempXMLList[0];		
					item.appendChild(childXML);					
					break;
					
				case WorkflowActivities.VARIABLES:
					//trace("variables");
					tempXMLList= BPELProcess..bpws::variables;					
					// There can be only one Variables sub element of Process
					// Similar to PartnerLinks but in different format
					for each (item in tempXMLList){												
						item.appendChild(childXML);						
					}					
					break;
					
				case WorkflowActivities.SEQUENCE:
					//trace("sequence");
					tempXMLList = BPELProcess..bpws::sequence;					
					//trace("tempXMLList.length(): " + tempXMLList.length())
					for each (item in tempXMLList){
						tempString = item.@name;						
						
						// There can be multiple Sequence and can be anywhere
						if(tempString == parentName){		
							//trace("Sequence item.@name: " + item.toXMLString());												
							item.appendChild(childXML);	
							break;						
						}						
					}					
					break;
					
				case WorkflowActivities.WHILE:
					//trace("while");
					
					// Can't use ..bpws::while; because while is keyWord
					//BPELProcess.
					tempXMLList = BPELProcess.descendants(); 					
					//trace("tempXMLList.length(): " + tempXMLList.length())
					for each (item in tempXMLList){
						//trace("Local Names Proces Creator: " + item.localName());
						if(item.localName() == "while"){
							tempString = item.@name;						
							
							// There can be multiple Sequence and can be anywhere
							if(tempString == parentName){		
								//trace("Sequence item.@name: " + item.toXMLString());
								//trace("item.length(): " + item.children().length());
								if(item.children().length() < 1)												
									item.appendChild(childXML);	
								else return false;
								break;						
							}
						}						
					}					
					break;
				
				case WorkflowActivities.ASSIGN:
					//trace("sequence");
					tempXMLList = BPELProcess..bpws::assign;					
					
					for each (item in tempXMLList){
						tempString = item.@name;							
						
						if(tempString == parentName){		
																			
							item.appendChild(childXML);	
							break;						
						}						
					}					
					break;				
				default:
					trace("ProcessCreator.findParentElement: Not Yet Implemented: " + parentType);
			}
			
			return returnBoolean;							
		}
		
		private function isUnique(activityType:String, activityName:String):Boolean {
						
			var bpws:Namespace = new Namespace("http://schemas.xmlsoap.org/ws/2003/03/business-process/");
			
			var tempParentXML:XML;
			var tempXMLList:XMLList;
			var item:XML;
			var tempString:String;
			var returnBoolean:Boolean = true;
			
			switch(activityType) {				
					
				case WorkflowActivities.PARTNERLINK:
					//trace("partnerLink");
					tempXMLList = BPELProcess..bpws::partnerLink;					
										
					for each (item in tempXMLList){												
						if(item.@name == activityName)
							return false;					
					}																
					break;
					
					case WorkflowActivities.VARIABLE:
					//trace("variable");
					tempXMLList= BPELProcess..bpws::variable;					
					
					for each (item in tempXMLList){												
						if(item.@name == activityName)
							return false;					
					}			
					break;
					
				case WorkflowActivities.SEQUENCE:
					//trace("sequence");
					tempXMLList = BPELProcess..bpws::sequence;					
					//trace("tempXMLList.length(): " + tempXMLList.length())
					for each (item in tempXMLList){
						tempString = item.@name;						
						
						// There can be multiple Sequence and can be anywhere
						if(tempString == activityName){						
							return false;			
						}						
					}					
					break;
					
				case WorkflowActivities.WHILE:
					//trace("while");
					
					// Can't use ..bpws::while; because while is keyWord					
					tempXMLList = BPELProcess.descendants(); 					
					//trace("tempXMLList.length(): " + tempXMLList.length())
					for each (item in tempXMLList){
						//trace("Local Names Proces Creator: " + item.localName());
						if(item.localName() == "while"){
							tempString = item.@name;	
							if(tempString == activityName){						
								return false;														
							}
						}						
					}					
					break;
				
				case WorkflowActivities.ASSIGN:
					//trace("sequence");
					tempXMLList = BPELProcess..bpws::assign;					
					
					for each (item in tempXMLList){
						tempString = item.@name;							
						
						if(tempString == activityName){						
							return false;											
						}						
					}					
					break;				
				default:
					trace("ProcessCreator.isUnique: Not Yet Implemented: " + activityType);
			}
			
			return returnBoolean;							
		}
		
		private function createChild(childType:String, attributesArray:Array):XML{
			//trace("ProcessCreator createChild " + childType);
			
			var bpws:Namespace = new Namespace("http://schemas.xmlsoap.org/ws/2003/03/business-process/");
			default xml namespace = bpws;
			var tempChildElement:String = "<" + childType + "/>"			
			var tempChildXML:XML = new XML(tempChildElement);
			
			//trace("tempChildXML without Attribute: " + tempChildXML.toXMLString())
			//trace("tempChildXML without Attribute: " + tempChildXML.toString())
			if(attributesArray){
				for(var i:Number = 0; i < attributesArray.length; i++){
					if(attributesArray[i][0] != ""){
						if(attributesArray[i][0] != undefined){
							if(attributesArray[i][0] != "empty"){
							//trace(attributesArray[i][1] + "  " + attributesArray[i][0])
								tempChildXML.@[attributesArray[i][0]] = attributesArray[i][1];
							} else {
								tempChildXML.appendChild(attributesArray[i][1]);
							}
						}
					}
					//trace("tempChildXML adding  Attribute: " + tempChildXML.toXMLString())
				}
			}			
			return tempChildXML;
		}
		// Function creates new activity only if it is unique otherwise it assumes that
		// it is same activity
		public function createNewActivity(parentType:String, parentName:String, newActivityType:String, 
			attributeArray:Array):Boolean {
			if(attributeArray){
				for(var i:Number = 0; i < attributeArray.length; i++){
					if(attributeArray[i][0] == "name"){							
						if(isUnique(newActivityType, attributeArray[i][1])){
							var childXML:XML = createChild(newActivityType, attributeArray);			
							return this.findParentElement(parentType, parentName, childXML);
						}
					}
				}
			}
			
			//
			//var childXML:XML = createChild(newActivityType, attributeArray);			
			//return this.findParentElement(parentType, parentName, childXML);	
			return false;						
		}
		
		public function modifyNameForMonitoring(activityType:String, activityOldName:String, activityNewName:String):Boolean{
			var bpws:Namespace = new Namespace("http://schemas.xmlsoap.org/ws/2003/03/business-process/");			
			
			var tempXMLList:XMLList;
			var item:XML;
			
			var returnBoolean:Boolean = false;
			
			switch(activityType) {				
					
				case WorkflowActivities.RECEIVE:
					//trace("receive");
					tempXMLList = BPELProcess..bpws::receive;					
										
					for each (item in tempXMLList){												
						if(item.@name == activityOldName){
							item.@name = activityNewName;
							return true;
						}
					}																
					break;
					
				case WorkflowActivities.REPLY:
					//trace("reply");
					tempXMLList= BPELProcess..bpws::reply;					
					
					for each (item in tempXMLList){												
						if(item.@name == activityOldName){
							item.@name = activityNewName;
							return true;
						}												
					}			
					break;
					
				case WorkflowActivities.INVOKE:
					//trace("invoke");
					tempXMLList = BPELProcess..bpws::invoke;					
					
					for each (item in tempXMLList){											
						if(item.@name == activityOldName){						
							item.@name = activityNewName;
							return true;			
						}						
					}					
					break;
					
						
				default:
					trace("ProcessCreator.modifyNameForMonitoring: Not Yet Implemented: " + activityType);
			}
			
			return returnBoolean;			
		}
		
		//private function updateName()
		public function createNewCopy(parentType:String, parentName:String, copyDOValue:CopyDO):void {
			var tempAttributeArray:Array = new Array();	
			//tempAttributeArray.push(["name", "defaultAssign"]);	
			
			var childXML:XML = createChild(WorkflowActivities.COPY, tempAttributeArray);
			
			var fromXML:XML = createChild(WorkflowActivities.FROM, copyDOValue.fromDO.attributesArray);		
			childXML.appendChild(fromXML);			
			
			var toXML:XML = createChild(WorkflowActivities.TO, copyDOValue.toDO.attributesArray);	
			childXML.appendChild(toXML);
			
			this.findParentElement(parentType, parentName, childXML);
		}
		
		public function createPartnerLinkType(name:String, portType:String,
				namespacePrefix:String, operationNames:Array,
				role:String):void {
			PartnerLinkTypeWrapperDO.getInstance().createPartnerLinkType(name, portType,
						namespacePrefix, operationNames, role);
		}
		
		// This operation used only for Testing ....!
		public function sequenceCreation(parentType:String, parentName:String, name:String):void{
			//trace(parentType + " " + parentName + " "+ name);
			var tempAttributeArray:Array = new Array();				
			
			tempAttributeArray.push(["name", name]);
			var childXML:XML = createChild(WorkflowActivities.SEQUENCE, tempAttributeArray);			
			
			this.findParentElement(parentType, parentName, childXML);			
		}
		
		public function addNewNamespace(WSName:String, WSNamespace:String):void {
			var tempNamespace:Namespace = new Namespace(WSName, WSNamespace);
			BPELProcess.addNamespace(tempNamespace);
			if(process)
				if(process.namespaceArray)
					process.namespaceArray.push(tempNamespace);			
		}
		
		// This should not be used directly ... made it PRIVATE
		private function addNewPartnerLink(WSName:String):void {		
			
			// Retrieve the "bpws" namespace from the Process
			//var bpws:Namespace = BPELProcess.namespace("bpws");
			
			var tempChildElement:String = "<partnerLink/>"	
			
			// Make the wsdl namespace as default namespace before
			// creating XML node
			//default xml namespace = bpws;		
			var tempChildXML:XML = new XML(tempChildElement);
			
			tempChildXML.@name = WSName + "_PL";
			tempChildXML.@partnerLinkType = "clinet:" + WSName + "_PLT";
			tempChildXML.@partnerRole = WSName + "_role";
			
			// searching element with qualified name
			// will return two messages		
			var tempPartnerLinksList:XMLList =  BPELProcess..partnerLinks
			
			var itemPartnerLinks:XML = tempPartnerLinksList[0];
			
			itemPartnerLinks.appendChild(tempChildXML);
		}		
	}
}