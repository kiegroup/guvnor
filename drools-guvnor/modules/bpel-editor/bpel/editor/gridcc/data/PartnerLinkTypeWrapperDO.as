package bpel.editor.gridcc.data
{
	public class PartnerLinkTypeWrapperDO
	{
		
		/**
         * The Singleton instance of PartnerLinkTypeWrapperDO
         */        
        protected static var instance:PartnerLinkTypeWrapperDO;
        
        /**
         * Constructor
         * 
         * Instantiates Singleton instance of ProcessCreator
         */        
        public function PartnerLinkTypeWrapperDO()
        {
            if (PartnerLinkTypeWrapperDO.instance == null) 
            {                
                PartnerLinkTypeWrapperDO.instance = this;
            }
        }
        
        /**
         * Determines if the singleton instance of ProcessCreator
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of ProcessCreator
         */        
        public static function getInstance():PartnerLinkTypeWrapperDO
        {
            if (instance == null) 
            {
               instance = new PartnerLinkTypeWrapperDO();
            }            
            return instance;
        }
        
		private var partnerLinkTypeArray:Array = new Array();
		
		public function createPartnerLinkType(name:String, portType:String, namespacePrefix:String, 
			operationNames:Array, role:String):void {
			//trace("PartnerLinkTypeWrapperDO.createPartnerLinkType");
			var tempPartnerLinkType:PartnerLinkTypeDO = new PartnerLinkTypeDO();
			tempPartnerLinkType.Name = name;
			tempPartnerLinkType.NamespacePrefix = namespacePrefix;
			tempPartnerLinkType.PortType = portType;
			tempPartnerLinkType.Role = role;
			tempPartnerLinkType.OperationNames = operationNames;
			
			partnerLinkTypeArray.push(tempPartnerLinkType);
		}
		
		public function findPartnerLinkType(name:String):PartnerLinkTypeDO{
			for(var i:int = 0; i < partnerLinkTypeArray.length; i++){
				var tempPartnerLinkType:PartnerLinkTypeDO = 
														PartnerLinkTypeDO (partnerLinkTypeArray[i]);
				if(tempPartnerLinkType.Name  == name){
					return tempPartnerLinkType;
				}			
			}
			return null;
		}
		
		public function partnerLinkTypeNamesArray():Array{
			var tempPartnerLinkTypeNamesArray:Array = new Array();
				for(var i:int = 0; i < partnerLinkTypeArray.length; i++){
					var tempPartnerLinkType:PartnerLinkTypeDO = 
														PartnerLinkTypeDO (partnerLinkTypeArray[i]);
						var tempPartnerLinkTypeName:String = 	tempPartnerLinkType.Name;		
						
						tempPartnerLinkTypeNamesArray.push(tempPartnerLinkTypeName);
				}
				return tempPartnerLinkTypeNamesArray;
		}
	}
}