package bpel.editor.gridcc.data
{
	public class VariableDO implements DOInterface
	{			
		[Bindable]			
		public var name:String;
		[Bindable]
		public var variableType:String = "element";
		public var variableTypeValue:String = "";
		//[Bindable]
		//public var type:String;
		//[Bindable]
		//public var element:String;
		
		private var _attributesArray:Array;
		
		public function get attributesArray():Array {
			return _attributesArray;
		}
		
		public function set attributesArray(attribtesValue:Array):void {
			_attributesArray = attribtesValue;
			//printArray();
		}
		
		public function VariableDO(){
			//_attributesArray = new Array();			
			fillAttributes();
		}
		
		private function fillAttributes():void {
			_attributesArray = new Array();
			_attributesArray.push(["name", name, "mandatory"]);
			_attributesArray.push([variableType, variableTypeValue]);
			//_attributesArray.push(["type", type]);
			//_attributesArray.push(["element", element]);
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			
			//for ( var i:int = 0; i < _attributesArray.length; i++){
				switch(attributeName){
					case "name":
						//trace(attributeName + "  " + attributeValue);
						//_attributesArray[i][1] = attributeValue;
						name = attributeValue;
						//fillAttributes();
						break;
						
					case "messageType":
						//trace(attributeName + "  " + attributeValue);
						//_attributesArray[i][1] = attributeValue;
						variableType = attributeName
						variableTypeValue = attributeValue;
						//messageType = attributeValue;
						//setVariableType(attributeName);
						//fillAttributes();
						break;
						
					case "type":
						//trace(attributeName + "  " + attributeValue);
						//_attributesArray[i][1] = attributeValue;
						variableType = attributeName
						variableTypeValue = "" + attributeValue;					
						break;
						
					case "element":						
						//_attributesArray[i][1] = attributeValue;
						variableType = attributeName
						variableTypeValue = attributeValue;
						break;	
											
					default:
						trace("ooops ... Wrong Attribute Name Passed to Variable DO: " + attributeName);	
				}
			//}
			fillAttributes();
		}
		
		public function printArray():void {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				for (var j:int = 0; j < _attributesArray[i].length; j++){
					//trace(_attributesArray[i][j])
				}
			}
		}
		
		public function getName():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "name"){
					return _attributesArray[i][1]
				}				
			}
			return "variable";
		}
		
		public function getVariableType():String{			
			return variableType;
		}
		
		public function getVariableTypeValue():String {			
			return variableTypeValue;
		}
		
		/*
		public function setVariableType(variableType:String):void{
			// Starting with 1 to miss name attribute
			for ( var i:int = 1; i < _attributesArray.length; i++){
				
				if(_attributesArray[i][0] == variableType){
					_attributesArray[i][2] = "selected";
				} else {
					_attributesArray[i][2] = "notSelected";
				}				
			}			
		}
		*/
		public function populateAttributes(attNamesList:XMLList):String {
			var variableName:String = "";
			for (var i:int = 0; i < attNamesList.length(); i++) {				
				updateAttributesArray(attNamesList[i].name(),attNamesList[i]);  			
    			
    			if(attNamesList[i].name() == "name"){
    				variableName = attNamesList[i];
    			}
			}
			fillAttributes();			
			return variableName;			
		}
		
		public function updateSubActivitiesArray(type:String, activity:Object):void {}
	}
}