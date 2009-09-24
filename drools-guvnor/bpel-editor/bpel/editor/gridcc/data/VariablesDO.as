package bpel.editor.gridcc.data
{
	public class VariablesDO implements DOInterface
	{
		
		protected static var instance:VariablesDO; 
		private var _subActivitiesArray:Array; 
		
		public function get subActivitiesArray():Array {
			return _subActivitiesArray;
		}
				
		[Bindable]
		public function set subActivitiesArray(subActivitiesArrayValue:Array):void{
			subActivitiesArray = subActivitiesArrayValue;
		}
		
		public function updateSubActivitiesArray(type:String, variable:Object):void {
			if(!_subActivitiesArray){
				_subActivitiesArray = new Array();
			}
			_subActivitiesArray.push([type,VariableDO(variable)]);
		}
		
		/**
         * Constructor
         * 
         * Instantiates Singleton instance of Variables
         */        
        public function VariablesDO()
        {
            if (VariablesDO.instance == null) 
            {                
                VariablesDO.instance = this;
                 _subActivitiesArray = new Array();                                  
            }            
        }
        
        /**
         * Determines if the singleton instance of Variables
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of Variables
         */        
        public static function getInstance():VariablesDO
        {
            if (instance == null) 
            {
               instance = new VariablesDO();   
            }            
            return instance;
        }
        
        /**
        * Delete the Singleton instance of Variables
        */
        public function removeInstance():void {
			if(instance){
				instance = null;
			}
		}
		
		public function getName():String {			
			return "variables";
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {}		
		
		public function populateAttributes(attNamesList:XMLList):String {
			return null;
		}
	}
}