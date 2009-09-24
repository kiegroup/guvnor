package bpel.editor.gridcc.controller
{
	import mx.charts.chartClasses.StackedSeries;
	
	public class WSDLCatalogCreator
	{
		/**
         * The Singleton instance of ProcessCreator
         */        
        protected static var instance:WSDLCatalogCreator;
        
        [Bindable]
        public var WSDLCatalog:XML;
        
        private var namespaceArray:Array;
        private var localWorkflowName:String;
        
        /**
         * Constructor
         * 
         * Instantiates Singleton instance of ProcessCreator
         */        
        public function WSDLCatalogCreator()
        {
            if (WSDLCatalogCreator.instance == null) 
            {                
                WSDLCatalogCreator.instance = this;
            }
        }
        
        public static function createInstance(workflowName:String):WSDLCatalogCreator
        {
        	// This function to be called only from Process Creator
            if (instance == null) 
            {
               instance = new WSDLCatalogCreator();
            }  
            instance.localWorkflowName = workflowName;
            instance.createInitialWSDLCatalog(workflowName);                
            return instance;
        }
        
        public static function getInstance():WSDLCatalogCreator
        {
        	// This function to be called from all classes except Process Creator
            if (instance == null) 
            {
            	// Do Nothing               
            }            
            return instance;
        }
        
        private function createInitialWSDLCatalog(workflowName:String):void {        	
					
			WSDLCatalog = new XML("<wsdlCatalog/>");			
			WSDLCatalog.@xmlns="http://schemas.active-endpoints.com/wsdl-catalog/2005/09/wsdlCatalog.xml"; 
            instance.createWSDLEntry (workflowName);			
        }
        
        public function createWSDLEntry(workflowName:String):void {
			var tempChildElement:String = "<wsdlEntry/>"			
			var tempChildXML:XML = new XML(tempChildElement);
			
			tempChildXML.@location = "project:/" + localWorkflowName + "/wsdl/" + workflowName + ".wsdl";
			tempChildXML.@classpath = "wsdl/" + localWorkflowName + "/wsdl/" + workflowName + ".wsdl";
			
			findParentElement("wsdlCatalog",tempChildXML);
		}
		
		private function findParentElement(parentType:String, childXML:XML ):void {		
			
			var tempXMLList:XMLList;
			var item:XML;			
			
			switch(parentType) {
				
				case "wsdlCatalog":
					//trace("process");	
					WSDLCatalog.appendChild(childXML);										
					break;			
					
				case "MayBEUsedLater":							
					break;			
				
				default:
					trace("WSDLCatalogCreator.findParentElement: Not Yet Implemented");
			}							
		}		
	}
}