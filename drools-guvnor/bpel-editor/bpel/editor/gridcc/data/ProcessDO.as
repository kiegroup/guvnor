package bpel.editor.gridcc.data
{
	public class ProcessDO implements DOInterface {
		
		protected static var instance:ProcessDO; 
		
		private var name:String = "";
		private var targetNamespace:String = "";
		private var queryLanguage:String = "XPath";
		private var expressionLanguage:String = "";
		private var enableInstanceCompensation:Boolean = false;
		private var abstractProcess:Boolean = false;
				
		public var _attributesArray:Array;
		public var namespaceArray:Array;
		
		private var _subActivitiesArray:Array; 
		
		/**
         * Constructor
         * 
         * Instantiates Singleton instance of Process
         */        
        public function ProcessDO()
        {
            if (ProcessDO.instance == null) 
            {                
                ProcessDO.instance = this;                                                
            }            
        }
        
        /**
         * Determines if the singleton instance of Process
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of Process
         */        
        public static function getInstance():ProcessDO
        {
            if (instance == null) 
            {
               instance = new ProcessDO();   
            }            
            return instance;
        }
        
        public function removeInstance():void {
			if(instance){
				instance = null;
			}
		}
		
		public function get subActivitiesArray():Array {
			return _subActivitiesArray;
		}
				
		[Bindable]
		public function set subActivitiesArray(subActivitiesArrayValue:Array):void{
			subActivitiesArray = subActivitiesArrayValue;
		}
		
		// Here I should have any sort of event for Visualisation and XML generation
		public function updateSubActivitiesArray(type:String, activity:Object):void {
			if(!_subActivitiesArray){
				_subActivitiesArray = new Array();
			}
			_subActivitiesArray.push([type,activity]);
		}	
		 
		public function fillNamespaceArray(clientNamespace:String):void {			
			namespaceArray = new Array();
			var tempNamespace:Namespace; 
			
			tempNamespace = new Namespace("bpws","http://schemas.xmlsoap.org/ws/2003/03/business-process/");			
			namespaceArray.push(tempNamespace);	
							
			tempNamespace = new Namespace("xsd","http://www.w3.org/2001/XMLSchema");	
			namespaceArray.push(tempNamespace);			
					
			tempNamespace = new Namespace("wsa","http://schemas.xmlsoap.org/ws/2003/03/addressing");	
			namespaceArray.push(tempNamespace);	
			
			tempNamespace = new Namespace("client",clientNamespace);	
			namespaceArray.push(tempNamespace);	
		}		
		
		private function fillAttributes():void {
			_attributesArray = new Array();
			_attributesArray.push(["name", name, "mandatory"]);
			_attributesArray.push(["targetNamespace", targetNamespace, "mandatory"]);
			_attributesArray.push(["queryLanguage", queryLanguage, "optional"]);
			_attributesArray.push(["expressionLanguage", expressionLanguage, "optional"]);
			_attributesArray.push(["enableInstanceCompensation", enableInstanceCompensation, "optional"]);
			_attributesArray.push(["abstractProcess", abstractProcess, "optional"]);
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			//for ( var i:int = 0; i < _attributesArray.length; i++){
				switch(attributeName){					
					case "name":
						//_attributesArray[i][1] = attributeValue;
						name = attributeValue;
						break;
					case "targetNamespace":
						//_attributesArray[i][1] = attributeValue;
						targetNamespace = attributeValue;
						break;
					case "queryLanguage":
						//_attributesArray[i][1] = attributeValue;
						queryLanguage = attributeValue;
						break;
					case "expressionLanguage":
						//_attributesArray[i][1] = attributeValue;
						expressionLanguage = attributeValue;
						break;
					case "enableInstanceCompensation":
						//_attributesArray[i][1] = attributeValue;
						enableInstanceCompensation = Boolean (attributeValue);
						break;
					case "abstractProcess":
						//_attributesArray[i][1] = attributeValue;
						abstractProcess = Boolean (attributeValue);
						break;		
					default:
						trace("ooops ... Wrong Attribute Name Passed to Process Do: " + attributeName);		
				}
			//}
		}
		/*
		public function printArray():void {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				for (var j:int = 0; j < _attributesArray[i].length; j++){
					trace(_attributesArray[i][j])
				}
			}
		}	
		*/
		public function printNSArray():void {
			for ( var i:int = 0; i < namespaceArray.length; i++){				
				trace(Namespace(namespaceArray[i]).prefix + "  "+ Namespace(namespaceArray[i]).uri)
				
			}
		}	
		
		// Method takes an array of attributes 
		// returns the value of attribute "name"
		public function populateAttributes(attNamesList:XMLList):String {
			var processName:String = "";
			for (var i:int = 0; i < attNamesList.length(); i++) { 
				//trace(attNamesList[i].name() + "  " + attNamesList[i]);
				updateAttributesArray(attNamesList[i].name(),attNamesList[i]);
    			//trace (typeof (attNamesList[i])); // xml
    			//trace (attNamesList[i].nodeKind()); // attribute
    			//trace (attNamesList[i].name()); // id and color
    			//trace (attNamesList[i]);
    			
    			if(attNamesList[i].name() == "name"){
    				processName = attNamesList[i];
    			}
			}
			fillAttributes();			
			return processName;			
		}	
		
		public function populateNS(nsArray:Array):void {
			var tempNamespace:Namespace; 
			
			if(!namespaceArray){
				namespaceArray = new Array();
				for (var index:int = 0; index < nsArray.length; index++) {
					tempNamespace = nsArray[index];
					namespaceArray.push(tempNamespace);
					//trace(tempNamespace.prefix + "  " + tempNamespace.uri)				  			   			
				}
			} else{
				for (var j:int = 0; j < namespaceArray.length; j++){
					var originalNamespace:Namespace = Namespace(namespaceArray[j]);
					for (var k:int = 0; k < nsArray.length; k++) {
						tempNamespace = nsArray[k];
						if(originalNamespace.prefix == tempNamespace.prefix){
							originalNamespace = tempNamespace;							
						}
						else {
							// Adding new namespace in the array 
							//while looping array starts indefinate loop
							//namespaceArray.push(tempNamespace);
						}	
					}				
				}
			}									
		}
		
		public function getName():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "name"){
					if(_attributesArray[i][1]){
						return _attributesArray[i][1];
					}
				}				
			}
			return "process";
		}				
	}
}