package bpel.editor.gridcc.controller
{
		
	import flash.net.URLLoader;
	import flash.events.Event;
	import flash.net.URLLoaderDataFormat;
	import flash.net.URLRequest;
	
	public class WSDLCreator
	{
		/**
         * The Singleton instance of WSDLCreator
         */        
        protected static var instance:WSDLCreator;        
        
        [Bindable]
        public var WorkflowWSDL:XML;
        
        private var localWorkflowName:String;
              
        
		/**
         * Constructor
         * 
         * Instantiates Singleton instance of WSDLCreator
         */        
        public function WSDLCreator()
        {
            if (WSDLCreator.instance == null) 
            {                
                WSDLCreator.instance = this;                 
            }            
        }
        
        /**
         * Determines if the singleton instance of WSDLCreator
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of WSDLCreator
         */        
        public static function getInstance():WSDLCreator
        {
            if (instance == null) 
            {
               instance = new WSDLCreator();   
            }            
            return instance;
        }
        
		public function loadWSDL(processType:String, workflowName:String):void {
			localWorkflowName = workflowName;
			
			var loader:URLLoader = new URLLoader();
			loader.dataFormat = URLLoaderDataFormat.TEXT;
			loader.addEventListener(Event.COMPLETE, handleComplete);
						
			switch(processType){
				
				case "synchronous":
				loader.load(new URLRequest("wsdl/Synchronous.wsdl"));
				break;
				
				case "asynchronous":
				loader.load(new URLRequest("wsdl/Asynchronous.wsdl"));
				break;
				
				case "empty":
				break;
				
				default:
				trace ("What are you passing to WSDL Loader in Process Creator?");
			}
  		}
  
  		private function handleComplete(event:Event):void{
			try{
				//trace("WSDL Creator File Loaded");
				WorkflowWSDL = new XML(event.target.data);
				//trace(BPELWSDL.toXMLString().replace(/DUMMY_PROCESS/g,localWorkflowName));
				WorkflowWSDL = new XML(WorkflowWSDL.toXMLString().replace(/DUMMY_PROCESS/g,localWorkflowName));
				//trace(WorkflowWSDL);
			}catch (e:TypeError){
				trace(e.message);
			}
		}
		
		private function addNewImport(WSName:String, WSNamespace:String):void{
			
			// Retrieve the "wsdl" namespace from the WSDL
			var wsdl:Namespace = WorkflowWSDL.namespace("wsdl");
			
			var tempChildElement:String = "<import/>"	
			
			// Make the wsdl namespace as default namespace before
			// creating XML node
			default xml namespace = wsdl;		
			var tempChildXML:XML = new XML(tempChildElement);			
			
			// adding attributes
			tempChildXML.@namespace = WSNamespace;
			tempChildXML.@location = "project:/" + localWorkflowName + "/wsdl/" + WSName + ".wsdl";			
			
			// searching element with qualified name		
			var tempTypesList:XMLList =  WorkflowWSDL..wsdl::types
			
			// Only for testing if import is fully qualified
			var tempTypesList2:XMLList = WorkflowWSDL.elements();			
			var itemTypes:XML = tempTypesList2[0];	
			//trace(itemTypes.name());			
			
			WorkflowWSDL.insertChildBefore(itemTypes, tempChildXML);					
		}
		
		public function addNewNamespace(WSName:String, WSNamespace:String):void {
			var tempNamespace:Namespace = new Namespace(WSName, WSNamespace);
			WorkflowWSDL.addNamespace(tempNamespace);
			if(!importExists(WSName, WSNamespace)){
				addNewImport(WSName, WSNamespace);
			}			
		}
		
		public function addNewPartnerLink(WSName:String, WSNamespace:String, portName:String):void{
			
			if(!partnerLinkTypeExists(WSName, portName)) {
				var plnk:Namespace = WorkflowWSDL.namespace("plnk");
				
				var tempPLTElement:String = "<plnk:partnerLinkType/>"
				
				default xml namespace = plnk;		
				var tempPLTXML:XML = new XML(tempPLTElement);
				
				tempPLTXML.@name = WSName + "_"+ portName +"_PLT";
				tempPLTXML.addNamespace(plnk);
				
				var tempRoleElement:String = "<role/>"
				
				default xml namespace = plnk;		
				var tempRoleXML:XML = new XML(tempRoleElement);
				 
				tempRoleXML.@name = WSName + "_" + portName + "_Provider";
				
				var tempPTlement:String = "<portType/>"
				
				default xml namespace = plnk;		
				var tempPTXML:XML = new XML(tempPTlement);
				
				tempPTXML.@name = WSName + ":" + portName;
				
				tempRoleXML.appendChild(tempPTXML);
				tempPLTXML.appendChild(tempRoleXML);
				WorkflowWSDL.appendChild(tempPLTXML);
			}
		}
		
		public function addNewRequestMessagePart(partName:String, partType:String):void {
			// Retrieve the "wsdl" namespace from the WSDL
			var wsdl:Namespace = WorkflowWSDL.namespace("wsdl");
			
			var tempChildElement:String = "<part/>"	
			
			// Make the wsdl namespace as default namespace before
			// creating XML node
			default xml namespace = wsdl;		
			var tempChildXML:XML = new XML(tempChildElement);
			
			tempChildXML.@name = partName;
			tempChildXML.@type = partType;
			
			// searching element with qualified name
			// will return two messages		
			var tempMessageList:XMLList =  WorkflowWSDL..wsdl::message
			var itemMessage:XML;
			//trace("WSDL Creator: " + tempMessageList.length());
			for(var counter:Number = 0; counter < tempMessageList.length(); counter++){				
				var itemTempMessage:XML = tempMessageList[counter];	
				
				//trace("Messages: " + itemTempMessage.toXMLString());
				//trace(String (itemTempMessage.@name).search("RequestMessage"));
				
				if(String (itemTempMessage.@name).search("RequestMessage") > 0){
				//if(String (itemTempMessage.@name) == localWorkflowName + "RequestMessage"){
					//trace("Respnse Message found");
					itemMessage = itemTempMessage;
				}			
			}
			if(itemMessage){			
				itemMessage.appendChild(tempChildXML);
			}
		}
		
		private function importExists(WSName:String, WSNamespace:String):Boolean {
			var wsdl:Namespace = WorkflowWSDL.namespace("wsdl");
			
			var tempTypesList:XMLList =  WorkflowWSDL.children();
			for each (var item:XML in tempTypesList){
				if(item.@namespace == WSNamespace){
					//trace("import found");
					return true;
				}
			}			
			return false;
		}
		
		private function partnerLinkTypeExists(WSName:String, portName:String):Boolean {
			var plnk:Namespace = WorkflowWSDL.namespace("plnk");
			
			var tempTypesList:XMLList =  WorkflowWSDL..plnk::partnerLinkType
			//trace("tempTypesList in partner link type search: " + tempTypesList.length())
			for each (var item:XML in tempTypesList){
				if(item.@name == WSName + "_"+ portName +"_PLT"){
					//trace("Partner Link Type found");
					return true;
				}
			}			
			return false;
		}
		
	}
}