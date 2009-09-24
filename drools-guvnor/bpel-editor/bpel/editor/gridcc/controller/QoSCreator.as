package bpel.editor.gridcc.controller
{
	public class QoSCreator 	{
		
		/**
         * The Singleton instance of SubmissionDocumentCreator
         */        
        protected static var instance:QoSCreator;
        
        [Bindable]
        public var QoSDocument:XML;
        
        private var namespaceArray:Array;
        private var localParameterArray:Array;
        private var localAttributesArray:Array;
        
        private var localWSName:String;
        private var localType:String;        
        
        // Not Used Any More
        //private static const QoS_BASIC_NAMESPACE:String = "http://www.gridcc.org/qos/20070730/QoS";
        private static const QoS_NAMESPACE:String = "http://www.gridcc.org/qos/20070730/QoS";
        private static const XSI_NAMESPACE:String = "http://www.w3.org/2001/XMLSchema-instance";
        //private static WF_NAMESPACE:String = null;
        private static const WSDL_PORT_TYPE:String = "_PT";
        private static const WSDL_OPERATION_NAME:String = "process";
        
        
        /**
         * Constructor
         * 
         * Instantiates Singleton instance of QoSCreator
         */        
        public function QoSCreator()
        {
            if (QoSCreator.instance == null) 
            {                
                QoSCreator.instance = this;
            }
            localParameterArray = new Array();
            localAttributesArray = new Array();
        }
        
        public static function createInstance(parametersArray:Array, atributesArray:Array, type:String):QoSCreator
        {
        	// This function to be called only from Process Creator
            if (instance == null) 
            {
               instance = new QoSCreator();
            } 
            
            instance.localParameterArray = parametersArray;
            instance.localAttributesArray =    atributesArray;    
            instance.localType = type;    
            instance.createQoSDocument(type);   
            //instance.localWSName = workflowName;             
            return instance;
        }
        
        public static function getInstance():QoSCreator
        {
        	// This function to be called from all classes except Process Creator
            if (instance == null) 
            {
            	// Do Nothing               
            }            
            return instance;
        }
        
        private function createQoSDocument(type:String):void { 
        	if(!QoSDocument){  
				QoSDocument = new XML("<QoSRequirements/>");			
				instance.fillNamespaceArray();   
            	instance.addSDNamespaces(); 
         	}
         	
            // For Testing Purpose
            //QoSDocument.appendChild(new XML("<QoSRequirements/>"));
            
            
            switch(type){
				case "IE":
					createIE();					
				break;
				
				case "IER":
					createIER();
				break;
				
				case "CE":
				break;
				
				case "SE":
				break;
				
				default:
					trace("QoS of type " + type + " not yet supported")
			}			
        }
        
        	
		private function fillNamespaceArray():void {
			namespaceArray = new Array();
			var tempNamespace:Namespace; 	
			
			tempNamespace = new Namespace("qos",QoS_NAMESPACE);	
			namespaceArray.push(tempNamespace);
			
			// Not Used Any More
			//tempNamespace = new Namespace("qosBasic",QoS_BASIC_NAMESPACE);	
			//namespaceArray.push(tempNamespace);
			
			tempNamespace = new Namespace("xsi",XSI_NAMESPACE);	
			namespaceArray.push(tempNamespace);
			
			tempNamespace = new Namespace("wfns",ProcessCreator.getInstance().getTargetNamespace());	
			namespaceArray.push(tempNamespace);
				
			
		}
		
		private function addSDNamespaces():void {						
			for(var i:Number = 0; i < namespaceArray.length; i++){
				QoSDocument.addNamespace(Namespace(namespaceArray[i]));				
			}					
		}
		
		private function createIER(): void {
			var QoSConstraint:XML = new XML("<QoSConstraint/>");
			
			var partnerLinkXML:XML = new XML("<PartnerLinkReference>" + 
				"wfns:" + localParameterArray[0][1] + "</PartnerLinkReference>");
				
			// Not required for IER
			//partnerLinkXML.@methodName = localAttributesArray[0][1];
			QoSConstraint.appendChild(partnerLinkXML);
			
			var IEService:XML = new XML("<IEService/>");
			var IEPerformance:XML = new XML("<IEReservationRequired/>");
			
			//var qualifiedAttribute:String = "qosBasic:" + "ieEndpoint";			
			//IEPerformance.@[qualifiedAttribute] = localAttributesArray[1][1];
			
			//qualifiedAttribute ="qosBasic:" + "IM_ID";
			//IEPerformance.@[qualifiedAttribute] = localAttributesArray[2][1];
			populateElement(IEPerformance);
			
			IEService.appendChild(IEPerformance);
			
			QoSConstraint.appendChild(IEService);
			
			QoSDocument.appendChild(QoSConstraint);
			//return IEService;			
		}
		
		private function createIE(): void {
			// Parent of PartnerLink and IEService
			var QoSConstraint:XML = new XML("<QoSConstraint/>");
			
			var partnerLinkXML:XML = new XML("<PartnerLinkReference>" + 
				"wfns:" + localParameterArray[0][1] + "</PartnerLinkReference>");
			partnerLinkXML.@methodName = localAttributesArray[0][1];
			QoSConstraint.appendChild(partnerLinkXML);
			
			var IEService:XML = new XML("<IEService/>");
			var IEPerformance:XML = new XML("<IEPerformace/>");
			
			var qualifiedAttribute:String = "qos:" + "ieEndpoint";			
			IEPerformance.@[qualifiedAttribute] = localAttributesArray[1][1];
			
			qualifiedAttribute ="qos:" + "IM_ID";
			IEPerformance.@[qualifiedAttribute] = localAttributesArray[2][1];
			populateElement(IEPerformance);
			
			IEService.appendChild(IEPerformance);
			QoSConstraint.appendChild(IEService);
			
			QoSDocument.appendChild(QoSConstraint);
			//return IEService;			
		}
		
		private function populateElement(performanceXML:XML):void{
			var qosTemp:Namespace = new Namespace("http://www.gridcc.org/qos/20070730/QoS");	
			default xml namespace = qosTemp;
			for(var i:int = 1 ; i < localParameterArray.length; i++){
				if(String(localParameterArray[i][1])){
					if(String(localParameterArray[i][1])){
						var tempChildElement:String = "<" + localParameterArray[i][0] + ">";
						tempChildElement = tempChildElement +  localParameterArray[i][1];
						tempChildElement = tempChildElement + "</" + localParameterArray[i][0] + ">";	
						var tempChildXML:XML = new XML(tempChildElement);
						
						// parameters with attributes starts from 5
						if(i > 4){
							pouplateAttributes(tempChildXML, localParameterArray[i][0]);
						}
						performanceXML.appendChild(tempChildXML);
					}
				}
			}
		}
		
		private function pouplateAttributes(criteriaXML:XML, searchPattern:String):void {	
					
			var qosTemp:Namespace = new Namespace("http://www.gridcc.org/qos/20070730/QoS");	
			default xml namespace = qosTemp;
						
			for(var i:int = 1 ; i < localAttributesArray.length; i++) {
				trace("localAttributesArray[i][0]: " + localAttributesArray[i][0]);
				if(localAttributesArray[i][0].indexOf("operation_" + searchPattern) > -1) {
					var qualifiedOperation:String = "qos:" + "operation";	
					if(String(localAttributesArray[i][1])) {
						criteriaXML.@[qualifiedOperation] = localAttributesArray[i][1];
					} else {
						criteriaXML.@[qualifiedOperation] = "GE";
					}
				}
			
				if(localAttributesArray[i][0].indexOf("confidence_" + searchPattern) > -1){
					var qualifiedConfidence:String = "qos:" + "confidence";
					if(String(localAttributesArray[i][1])){
						criteriaXML.@[qualifiedConfidence] = localAttributesArray[i][1];
					} else {
						criteriaXML.@[qualifiedConfidence] = "100";
					}	
				}				
			}
		}
	}	
}