package bpel.editor.gridcc.data
{
	public class WaitDO extends ActivityDO implements DOInterface {
		
		public static var counter:int = 1;

		[Bindable]		
		public var waitValue:String;
		public var waitType:String = "for";		
		
		public function WaitDO(){
			//_attributesArray = new Array();			
			fillAttributes();
		}
		
		private function fillAttributes():void {
			_attributesArray = new Array();
			_attributesArray.push(["name", name, "mandatory"]);
			_attributesArray.push([waitType, waitValue, "mandatory"]);			
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			
			//for ( var i:int = 0; i < _attributesArray.length; i++){
				switch(attributeName){
					case "name":						
						name = attributeValue;						
						break;
						
					case "for":						
						waitType = attributeName
						waitValue = attributeValue;						
						break;
						
					case "until":						
						waitType = attributeName
						waitValue = attributeValue;					
						break;	
											
					default:
						trace("ooops ... Wrong Attribute Name Passed to WaitDO: " + attributeName);			
				}
			//}
			fillAttributes();
			//printArray();
		}
		
		public function printArray():void {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				for (var j:int = 0; j < _attributesArray[i].length; j++){
					trace(_attributesArray[i][j])
				}
			}
		}
						
		public function getName():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "name"){
					if(_attributesArray[i][1])
						return _attributesArray[i][1]
				}				
			}
			return "wait-" + counter++;
		}
		
		public function getWaitType():String{			
			return waitType;
		}
		
		public function getWaitValue():String {			
			return waitValue;
		}		
		
		// What this function is doing .. why returning name 
		public function populateAttributes(attNamesList:XMLList):String {
			//var variableName:String = "";
			for (var i:int = 0; i < attNamesList.length(); i++) {				
				updateAttributesArray(attNamesList[i].name(),attNamesList[i]);    			
			}
			fillAttributes();			
			return null;			
		}
		
		public function updateSubActivitiesArray(type:String, activity:Object):void {}
	}
}