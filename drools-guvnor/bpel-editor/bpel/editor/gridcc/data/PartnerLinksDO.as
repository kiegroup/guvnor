package bpel.editor.gridcc.data
{
	public class PartnerLinksDO implements DOInterface
	{
		protected static var instance:PartnerLinksDO; 
		
		private var _subActivitiesArray:Array; 
		
		public function get subActivitiesArray():Array {
			return _subActivitiesArray;
		}
				
		[Bindable]
		public function set subActivitiesArray(subActivitiesArrayValue:Array):void{
			_subActivitiesArray = subActivitiesArrayValue;
		}
		
		
		public function updateSubActivitiesArray(type:String, partnerLink:Object):void {
			if(!_subActivitiesArray){
				_subActivitiesArray = new Array();
			}
			_subActivitiesArray.push([type,PartnerLinkDO(partnerLink)]);			
		}		
		
		
		/**
         * Constructor
         * 
         * Instantiates Singleton instance of PartnerLinks
         */        
        public function PartnerLinksDO()
        {
            if (PartnerLinksDO.instance == null) 
            {                
                PartnerLinksDO.instance = this;
                _subActivitiesArray = new Array();                                 
            }            
        }
        
        /**
         * Determines if the singleton instance of PartnerLinks
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of PartnerLinks
         */        
        public static function getInstance():PartnerLinksDO
        {
            if (instance == null) 
            {
               instance = new PartnerLinksDO();   
            }            
            return instance;
        }
        
        /**
        * Delete the Singleton instance of PartnerLinks
        */
        public function removeInstance():void {
			if(instance){
				instance = null;
			}
		}
		
		public function getName():String {			
			return "partnerLinks";
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {}		
		
		public function populateAttributes(attNamesList:XMLList):String {
			return null;
		}
		//public function updateSubActivitiesArray(type:String, activity:Object):void {}
	}
}