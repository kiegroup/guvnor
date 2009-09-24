package bpel.editor.gridcc.data
{
	public class TerminateDO extends ActivityDO implements DOInterface
	{	
		private static var counter:int = 1;
		
		public function TerminateDO(){			
			super();
			fillAttributes();
		}
		private function fillAttributes():void {
			_attributesArray = new Array();
			_attributesArray.push(["name", name, "mandatory"]);			
			_attributesArray.push(["suppressJoinFailure", suppressJoinFailure, "optional"]);
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			for ( var i:int = 0; i < attributesArray.length; i++){
				switch(attributeName){
					case "name":
						//_attributesArray[i][1] = attributeValue;
						name = attributeValue;
						break;					
					case "suppressJoinFailure":
						//_attributesArray[i][1] = attributeValue;
						suppressJoinFailure = Boolean (attributeValue);
						break;		
					default:
						trace("ooops ... Wrong Attribute Name Passed to Terminate: " + attributeName);	
				}
			}
			fillAttributes();
		}
		
		/*
		public function printArray():void {
			for ( var i:int = 0; i < attributesArray.length; i++){
				for (var j:int = 0; j < attributesArray[i].length; j++){
					trace(attributesArray[i][j])
				}
			}
		} */
		
		public function getName():String {
			
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "name"){
					if(_attributesArray[i][1]){
						return _attributesArray[i][1]
					}
				}				
			}
			return "terminate" + counter++;
		}
		
		public function populateAttributes(attNamesList:XMLList):String {
			var processName:String = "";
			for (var i:int = 0; i < attNamesList.length(); i++) {				
				updateAttributesArray(attNamesList[i].name(),attNamesList[i]);  			
    			
    			if(attNamesList[i].name() == "name"){
    				processName = attNamesList[i];
    			}
			}
			fillAttributes();			
			return processName;			
		}
		
		public function updateSubActivitiesArray(type:String, activity:Object):void {}
	}
}