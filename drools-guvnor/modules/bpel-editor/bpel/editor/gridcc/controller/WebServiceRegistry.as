package bpel.editor.gridcc.controller
{
	public class WebServiceRegistry
	{		
		/**
         * The Singleton instance of WebServiceRegistry
         */        
        protected static var instance:WebServiceRegistry;  
        
        [Bindable]
        public var WSRegistry:XML; 
        
        /**
         * Constructor
         * 
         * Instantiates Singleton instance of WebServiceRegistry
         */        
        public function WebServiceRegistry()
        {
            if (WebServiceRegistry.instance == null) 
            {                
                WebServiceRegistry.instance = this;                 
            }
            initializeRegistry();            
        }
        
        /**
         * Determines if the singleton instance of BPELLoader
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of BPELLoader
         */        
        public static function getInstance():WebServiceRegistry
        {
            if (instance == null) 
            {
               instance = new WebServiceRegistry();   
            }            
            return instance;
        }
        
        private function initializeRegistry():void {
        	WSRegistry = new XML("<node label='Web Service Registry'/>");
        }
	}
}