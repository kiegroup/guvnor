package bpel.editor.gridcc.data
{
	public class ToDO implements DOInterface
	{
		//[Bindable]			
		//public var name:String;
		
		[Bindable]
		public var variable:String = "variable";
		public var variableValue:String = "";
		public var queryType:String = "empty";
		
		[Bindable]
		public var toValue:String = " ";
				
		private var _attributesArray:Array;
		
		public function get attributesArray():Array {
			return _attributesArray;
		}
		
		public function set attributesArray(attribtesValue:Array):void {
			_attributesArray = attribtesValue;
			//printArray();
		}
		
		public function ToDO(){
			//_attributesArray = new Array();			
			fillAttributes();
		}
		
		public function fillAttributes():void {
			_attributesArray = new Array();
			_attributesArray.push(["variable", variableValue, "mandatory"]);
			_attributesArray.push([queryType, toValue, "mandatory"]);			
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			//trace("To Do: " + attributeName + "  :  " + attributeValue)
			//for ( var i:int = 0; i < _attributesArray.length; i++){
				switch(attributeName){
					case "variable":						
						variableValue = attributeValue;		
						//trace(variableValue);			
						break;
						
					case "query":						
						queryType = attributeName
						toValue = attributeValue;						
						break;
						
					case "part":						
						queryType = attributeName
						toValue = attributeValue;					
						break;
						
					case "empty":											
						//_User is assigning the value directly to any String
						//queryType = attributeName
						toValue = attributeValue;
						break;	
											
					default:
						trace("ooops ... Wrong Attribute Name Passed to To DO: " + attributeName);			
				}
			//}
			fillAttributes();
			//printArray();
		}
		
		public function printArray():void {
			
			for ( var i:int = 0; i < _attributesArray.length; i++){
				var tempString:String = " "
				for (var j:int = 0; j < _attributesArray[i].length; j++){
					tempString = tempString + "  " + _attributesArray[i][j];					
				}
				//trace(tempString);
			}
		}
		
		
		// From has no Name will always return NULL
		public function getName():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "name"){
					return _attributesArray[i][1]
				}				
			}
			return null;
		}
		
		public function getQueryType():String{			
			return queryType;
		}
		
		public function getFromValue():String {			
			return toValue;
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