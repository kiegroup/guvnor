package bpel.editor.gridcc.data
{
	public class FromDO implements DOInterface
	{
		//[Bindable]			
		//public var name:String;
		
		[Bindable]
		public var variable:String = "variable";
		public var variableValue:String = "";
		public var queryType:String = "empty";
		
		[Bindable]
		public var fromValue:String = " ";
				
		private var _attributesArray:Array;
		
		public function get attributesArray():Array {
			return _attributesArray;
		}
		
		public function set attributesArray(attribtesValue:Array):void {
			_attributesArray = attribtesValue;
			//printArray();
		}
		
		public function FromDO(){
			//_attributesArray = new Array();			
			fillAttributes();
		}
		
		private function fillAttributes():void {
			_attributesArray = new Array();
			_attributesArray.push(["variable", variableValue, "mandatory"]);
			_attributesArray.push([queryType, fromValue]);			
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			
			//for ( var i:int = 0; i < _attributesArray.length; i++){
				switch(attributeName){
					case "variable":						
						variableValue = attributeValue;						
						break;
						
					case "query":						
						queryType = attributeName
						fromValue = attributeValue;						
						break;
						
					case "part":						
						queryType = attributeName
						fromValue = attributeValue;					
						break;
						
					case "empty":											
						//_User is assigning the value directly to any String
						queryType = attributeName
						fromValue = attributeValue;
						break;
						
					case "expression":											
						//_User is assigning the value directly to any String
						queryType = attributeName
						fromValue = attributeValue;
						break;		
											
					default:
						trace("ooops ... Wrong Attribute Name Passed to From DO: " + attributeName);			
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
		
		
		// From has no Name will always return NULL
		public function getName():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "name"){
					if(_attributesArray[i][1]){
							return _attributesArray[i][1]
					}
				}				
			}
			return "from";
		}
		
		public function getQueryType():String{			
			return queryType;
		}
		
		public function getFromValue():String {			
			return fromValue;
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