package bpel.editor.gridcc.controller
{
	public class PDDCreator
	{		
		/**
         * The Singleton instance of ProcessCreator
         */        
        protected static var instance:PDDCreator;
        
        [Bindable]
        public var WorkflowPDD:XML;
        
        private var namespaceArray:Array;
        private var localWSName:String;
        
        /**
         * Constructor
         * 
         * Instantiates Singleton instance of ProcessCreator
         */        
        public function PDDCreator()
        {
            if (PDDCreator.instance == null) 
            {                
                PDDCreator.instance = this;
            }
        }        
        /**
         * Determines if the singleton instance of ProcessCreator
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of ProcessCreator
         */        
        public static function createInstance(workflowName:String, workflowNamespace:String):PDDCreator
        {
        	// This function to be called only from Process Creator
            if (instance == null) 
            {
               instance = new PDDCreator();
            }  
            
            instance.localWSName = workflowName;
            instance.createInitialPDD(workflowName, workflowNamespace);    
            
            return instance;
        }
        
        public static function getInstance():PDDCreator
        {
        	// This function to be called from all classes except Process Creator
            if (instance == null) 
            {
            	// Do Nothing
               
            }            
            return instance;
        }
        
        private function createInitialPDD(workflowName:String, workflowNamespace:String):void {        	
					
			WorkflowPDD = new XML("<process/>");
			WorkflowPDD.@name="bpelns:"+workflowName;
			WorkflowPDD.@location="bpel/" + workflowName + "/" + workflowName + ".bpel";
			instance.fillNamespaceArray(workflowNamespace);   
            instance.addPDDNamespaces(); 
            instance.createPartnerLinks();
            instance.createPartnerLink(workflowName, true, true);
            instance.createWSDLReferences();
            instance.createWSDL(workflowName, workflowNamespace);		
			
        }
        
        private function fillNamespaceArray(workflowNamespace:String):void {			
			namespaceArray = new Array();
			var tempNamespace:Namespace; 	
					
			tempNamespace = new Namespace("wsa","http://schemas.xmlsoap.org/ws/2003/03/addressing");	
			namespaceArray.push(tempNamespace);	
			
			tempNamespace = new Namespace("bpelns",workflowNamespace);	
			namespaceArray.push(tempNamespace);	
		}
		
		private function addPDDNamespaces():void {						
			for(var i:Number = 0; i < namespaceArray.length; i++){
				WorkflowPDD.addNamespace(Namespace(namespaceArray[i]));				
			}			
			// Hard Coded default Namespace
			WorkflowPDD.@xmlns="http://schemas.active-endpoints.com/pdd/2005/09/pdd.xsd";	
		}
		
		private function createPartnerLinks():void {
			var tempChildElement:String = "<partnerLinks/>"			
			var tempChildXML:XML = new XML(tempChildElement);
			findParentElement("process",tempChildXML);
		}
		
		public function createPartnerLink(workflowName:String, myRole:Boolean, 
			staticPartnerLink:Boolean):void {
			var tempChildElement:String = "<partnerLink/>";			
			var tempChildXML:XML = new XML(tempChildElement);
			tempChildXML.@name = workflowName + "_PL";
			if(myRole) {
				createMyRole(workflowName, tempChildXML);
			} else { 
				if(staticPartnerLink){
					createStaticPartnerRole();
				} else {
					createDynamicPartnerRole(workflowName, tempChildXML)
				}
				
			}
			findParentElement("partnerLinks",tempChildXML);
		}	
		
		private function createMyRole(workflowName:String, parent:XML):void {
			var tempChildElement:String = "<myRole/>";
			var tempChildXML:XML = new XML(tempChildElement);
			tempChildXML.@allowedRoles = "";
			tempChildXML.@binding = "RPC";
			tempChildXML.@service = workflowName + "Service";
			parent.appendChild(tempChildXML);
		}	
		
		private function createStaticPartnerRole():void{
			// Not Yet Done ...!
		}
		
		private function createDynamicPartnerRole(workflowName:String, parent:XML):void {
			var tempChildElement:String = "<partnerRole/>";
			var tempChildXML:XML = new XML(tempChildElement);
			tempChildXML.@endpointReference = "dynamic";
			tempChildXML.@invokeHandler = "default:Address";
			parent.appendChild(tempChildXML);
		}
		
		private function createWSDLReferences():void {
			var tempChildElement:String = "<wsdlReferences/>"			
			var tempChildXML:XML = new XML(tempChildElement);
			findParentElement("process",tempChildXML);
		}
		
		public function createWSDL(workflowName:String, workflowNamespace:String):void {
			var tempChildElement:String = "<wsdl/>"			
			var tempChildXML:XML = new XML(tempChildElement);
			tempChildXML.@location = "project:/" + localWSName + "/wsdl/" + workflowName + ".wsdl";
			tempChildXML.@namespace = workflowNamespace;
			findParentElement("wsdlReferences",tempChildXML);
		}
		
		public function createNewActivity(parentType:String, parentName:String, 
			newActivityType:String, attributeArray:Array):void {
						
			//var childXML:XML = createChild(newActivityType, attributeArray);			
			//this.findParentElement(parentType, parentName, childXML);							
		}
		
		private function findParentElement(parentType:String, childXML:XML ):void {
			//trace("ProcessCreator findParentElement");
			//trace( parentType + "   " + parentName);
			
			var tempXMLList:XMLList;
			var item:XML;			
			
			switch(parentType) {
				
				case "process":
					//trace("process");							
					WorkflowPDD.appendChild(childXML);										
					break;
					
				case "partnerLinks":
					//trace("partnerLinks");
					tempXMLList = WorkflowPDD..partnerLinks;
					
					// There can be only one partnerLinks sub element of process					
					item = tempXMLList[0];
					if(item)
						item.appendChild(childXML);
					break;				
					
				case "wsdlReferences":
					//trace("variables");
					tempXMLList= WorkflowPDD..wsdlReferences;
										
					// There can be only one wsdlReferences sub element of process
					// Similar to PartnerLinks but in different format
					for each (item in tempXMLList) {												
						item.appendChild(childXML);						
					}
					break;
					
				case "MayBEUsedLater":
							
					break;			
						
				default:
					trace("PDDCreator.findParentElement(" + parentType + "): Not Yet Implemented");
			}							
		}		
	}
}