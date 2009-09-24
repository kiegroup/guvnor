package bpel.editor.gridcc.controller
{
	public class SubmissionDocumentCreator
	{
		/**
         * The Singleton instance of SubmissionDocumentCreator
         */        
        protected static var instance:SubmissionDocumentCreator;
        
        [Bindable]
        public var SubmissionDocument:XML;
        
        private var namespaceArray:Array;
        
        private var localWSName:String;
        
        private static const WSDL_NAMESPACE:String = "http://gridcc.org/workflows/";
        private static const WSDL_PORT_TYPE:String = "_PT";
        private static const WSDL_OPERATION_NAME:String = "process";
        
        /**
         * Constructor
         * 
         * Instantiates Singleton instance of SubmissionDocumentCreator
         */        
        public function SubmissionDocumentCreator()
        {
            if (SubmissionDocumentCreator.instance == null) 
            {                
                SubmissionDocumentCreator.instance = this;
            }
        }
        
        public static function createInstance(workflowName:String, workflowNamespace:String):SubmissionDocumentCreator
        {
        	// This function to be called only from Process Creator
            if (instance == null) 
            {
               instance = new SubmissionDocumentCreator();
            }            
            instance.createSubmissionDocument(workflowName, workflowNamespace);   
            instance.localWSName = workflowName;             
            return instance;
        }
        
        public static function getInstance():SubmissionDocumentCreator
        {
        	// This function to be called from all classes except Process Creator
            if (instance == null) 
            {
            	// Do Nothing               
            }            
            return instance;
        }
        
        private function createSubmissionDocument(workflowName:String, workflowNamespace:String):void {        	
					
			SubmissionDocument = new XML("<wfSubmitRequest/>");
			SubmissionDocument.@language="BPEL"
			SubmissionDocument.@engine="active-BPEL";
			SubmissionDocument.@actionCycle="deploy-and-trigger";
			instance.fillNamespaceArray(workflowNamespace);   
            instance.addSDNamespaces(); 
            instance.createWorkflow();
            instance.createDeploy(workflowName);
            instance.createTrigger(workflowName);
            instance.createQoSRequirements();		
			
        }
        
        private function fillNamespaceArray(workflowNamespace:String):void {			
			namespaceArray = new Array();
			var tempNamespace:Namespace; 	
					
			tempNamespace = new Namespace("qos","http://www.gridcc.org/qos/20070223/2");	
			namespaceArray.push(tempNamespace);				
						
			tempNamespace = new Namespace("xs","http://www.w3.org/2001/XMLSchema-instance");	
			namespaceArray.push(tempNamespace);
			
			tempNamespace = new Namespace("wfms",workflowNamespace);	
			namespaceArray.push(tempNamespace);	
		}
		
		private function addSDNamespaces():void {						
			for(var i:Number = 0; i < namespaceArray.length; i++){
				SubmissionDocument.addNamespace(Namespace(namespaceArray[i]));				
			}			
			// Hard Coded default Namespace
			//Not used in Submission Document
			//WorkflowPDD.@xmlns="http://XXXXXX";	
		}
		private function createWorkflow():void {
			var tempChildElement:String = "<workflow/>"			
			var tempChildXML:XML = new XML(tempChildElement);
			
			tempChildXML.@encoding="Base64";
			tempChildXML.@packageFormat="bpr";
			
			findParentElement("wfSubmitRequest",tempChildXML);
		}
		
		private function createDeploy(workflowName:String):void {
			var tempChildElement:String = "<deploy/>"			
			var tempChildXML:XML = new XML(tempChildElement);
			
			tempChildXML.@inputParameters="0";				
			
			findParentElement("wfSubmitRequest",tempChildXML);
			
			createParameter("deploy", "xs:string", workflowName+".bpr", null);
		}
		
		private function createTrigger(workflowName:String):void {
			var tempChildElement:String = "<trigger/>"			
			var tempChildXML:XML = new XML(tempChildElement);
			
			tempChildXML.@inputParameters="0";
			
			findParentElement("wfSubmitRequest",tempChildXML);
			
			createTriggerServiceCompletion(workflowName);
			createTriggerServiceWSDLTNS(workflowName);
			createTriggerServiceName(workflowName);
			createTriggerPortName(workflowName);
			createTriggerOperationName();
			
			createParameter("trigger", "xs:string", "Free Part", null);
		}
		
		private function createTriggerServiceCompletion(value:String):void{
			var tempChildElement:String = "<serviceCompletion>" + value + "</serviceCompletion>"			
			var tempChildXML:XML = new XML(tempChildElement);
			findParentElement("trigger",tempChildXML);
			
		}
		
		private function createTriggerServiceWSDLTNS(value:String):void{
			var tempChildElement:String = "<wsdlTargetNamespace>" + "http://gridcc.org/workflows/" + value + "</wsdlTargetNamespace>"			
			var tempChildXML:XML = new XML(tempChildElement);
			findParentElement("trigger",tempChildXML);
			
		}
		
		private function createTriggerServiceName(value:String):void{
			var tempChildElement:String = "<serviceName>" + value + "</serviceName>"			
			var tempChildXML:XML = new XML(tempChildElement);
			findParentElement("trigger",tempChildXML);
			
		}
		
		private function createTriggerPortName(value:String):void{
			var tempChildElement:String = "<portName>" + value + "_PT" + "</portName>"			
			var tempChildXML:XML = new XML(tempChildElement);
			findParentElement("trigger",tempChildXML);
			
		}
		
		private function createTriggerOperationName():void{
			var tempChildElement:String = "<operationName>" + "process" + "</operationName>"			
			var tempChildXML:XML = new XML(tempChildElement);
			findParentElement("trigger",tempChildXML);
			
		}
		
		private function createQoSRequirements():void {
			trace("createQoSRequirements");
			var tempChildElement:String = "<QoSRequirements/>"		
			var tempNamespace:Namespace = new Namespace("qos","http://www.gridcc.org/qos/20070223/QoS");
			
			var tempChildXML:XML = new XML(tempChildElement);			
			//tempChildXML.addNamespace(tempNamespace);			
			
			findParentElement("wfSubmitRequest",tempChildXML);
		}
		
		public function createParameter(parent:String, type:String, value:String, partnerLinkRef:String):void {
			var tempChildElement:String = "<parameter>" + value + "</parameter>"	
			var tempChildXML:XML = new XML(tempChildElement);
			
			tempChildXML.@order = updateInputParameters(parent);
			tempChildXML.@type = type;
			if(partnerLinkRef){
				tempChildXML.@PartnerLinkReference = localWSName + ":" +partnerLinkRef;
			}
			findParentElement(parent,tempChildXML);
		}
		
		private function updateInputParameters(parentType:String):Number {
			var tempXMLList:XMLList;
			var item:XML
			var counter:Number = 0;
			
			switch(parentType) {
				
				case "deploy":
					//trace("process");							
					tempXMLList = SubmissionDocument..deploy;	
					
					// There can be only one deploy sub element of wfSubmitRequest	
					item = tempXMLList[0];
					counter = item.@inputParameters;
					
					item.@inputParameters = ++(item.@inputParameters);					
					return counter;														
					break;
					
				case "trigger":
					//trace("partnerLinks");
					tempXMLList = SubmissionDocument..trigger;
					
					// There can be only one trigger sub element of wfSubmitRequest					
					item = tempXMLList[0];
					
					counter = item.@inputParameters;
					item.@inputParameters = ++(item.@inputParameters);
					
					return counter;										
					break;				
				
					
				case "MayBEUsedLater":
					break;			
						
				default:
					trace("SD.updateInputParameters(" + parentType + "): Not Yet Implemented");
			}
			//tempXMLList = SubmissionDocument..
		return 0;
		}
		
		private function findParentElement(parentType:String, childXML:XML ):void {
			//trace("ProcessCreator findParentElement");
			//trace( parentType + "   " + parentName);
			
			var tempXMLList:XMLList;
			var item:XML;			
			
			switch(parentType) {
				
				case "wfSubmitRequest":
					//trace("process");							
					SubmissionDocument.appendChild(childXML);										
					break;
					
				case "deploy":
					//trace("partnerLinks");
					tempXMLList = SubmissionDocument..deploy;
					
					// There can be only one partnerLinks sub element of process					
					item = tempXMLList[0];																	
					item.appendChild(childXML);											
					break;				
					
				case "trigger":
					//trace("variables");
					tempXMLList= SubmissionDocument..trigger;
										
					// There can be only one wsdlReferences sub element of process
					// Similar to PartnerLinks but in different format
					for each (item in tempXMLList){												
						item.appendChild(childXML);						
					}					
					break;
					
				case "MayBEUsedLater":
							
					break;			
						
				default:
					trace("SDCreator.findParentElement(" + parentType + "): Not Yet Implemented");
			}							
		}
	}
}