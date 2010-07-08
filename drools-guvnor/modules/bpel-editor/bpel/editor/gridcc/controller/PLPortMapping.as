package bpel.editor.gridcc.controller
{
	public class PLPortMapping
	{
		
	/**
     * The Singleton instance of PLPortMapping
     */        
    protected static var instance:PLPortMapping;  
    
    private var _plPortMapping:Array;
    
    private var _operationMessageMapping:Array;
    
    private var arrayCounter:Number = 0;             
    
	/**
     * Constructor
     * 
     * Instantiates Singleton instance of PLPortMapping
     */        
    public function PLPortMapping()
    {
        if (PLPortMapping.instance == null) 
        {                
            PLPortMapping.instance = this;                 
        }    
        _plPortMapping = new Array();  
        _operationMessageMapping =  new Array();     
    }
    
		/**
		 * Determines if the singleton instance of BPELLoader
		 * has been instantiated, if not an instance is instantiated
		 * and returned in subsequent calls to getInstance();
		 * 
		 * @return Singleton instance of BPELLoader
		 */        
		public static function getInstance():PLPortMapping
		{
		    if (instance == null) 
		    {
		       instance = new PLPortMapping();   
		    }            
		    return instance;
		}	
		
		public function addNewMapping(partnerLink:String, portType:String, 
			operations:Array):void{
				
				var tempCounter:Number = _plPortMapping.push([partnerLink, portType]);
				//trace("tempCounter in PL-PT mapping: " + tempCounter);
				
				if(operations){
					for(var i:Number = 0; i < operations.length; i++){
						_plPortMapping[tempCounter -1][2+i] = String (operations[i]);
						_operationMessageMapping.push([String (operations[i]), " - - - - - - - ",
							" - - - - - - - ", " - - - - - - - "]);
						
					}
				}			
		}
		
		public function getPort(partnerLink:String):String {
			for(var i:Number= 0; i < _plPortMapping.length; i++){
				var tempPL:String = _plPortMapping[i][0];
				
				if(tempPL == partnerLink){
					return _plPortMapping[i][1];
				}
			}
			return "No_portType_Found";
		}
		
		public function getOperations(partnerLink:String, portType:String):Array {
			//trace("partnerLink: " + partnerLink + "portType: " + portType);
			var tempOperationArray:Array = new Array();
			tempOperationArray.push("No_Operation_Selected");
			
			for(var i:Number = 0; i < _plPortMapping.length; i++){
				var tempPL:String = _plPortMapping[i][0];
				var tempPT:String = _plPortMapping[i][1];
				
				if(tempPL == partnerLink && tempPT == portType){
					//trace("partnerLink: " + "portType: " + "found: "+ _plPortMapping[i].length);
					for(var j:int = 2; j < _plPortMapping[i].length; j++)					
						tempOperationArray.push(_plPortMapping[i][j]);
				}
			}
			return tempOperationArray;
		}
		
		public function addOperationMessage(operation:String, input:String, output:String,
			fault:String):Boolean {
			for(var counter:Number = 0 ; counter < _operationMessageMapping.length; counter++){
				var tempOperationName:String = String (_operationMessageMapping[counter][0]);
				if(tempOperationName == operation){
					_operationMessageMapping[counter][1] = input;
					_operationMessageMapping[counter][2] = output;
					_operationMessageMapping[counter][3] = output;
					
					return true;
				}
			}
			return false;			
		}
		
	}
}