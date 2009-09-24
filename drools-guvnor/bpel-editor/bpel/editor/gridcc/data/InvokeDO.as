package bpel.editor.gridcc.data
{
	public class InvokeDO extends ActivityDO implements DOInterface
	{	
		public var partnerLink:String;
		public var portType:String;
		public var operation:String;
		public var inputVariable:String;
		public var outputVariable:String;	
		
		public function InvokeDO(){
			super();			
			fillAttributes();
		}
		
		private function fillAttributes():void {
			_attributesArray.push(["name", name, "mandatory"]);			
			_attributesArray.push(["partnerLink", partnerLink, "mandatory"]);
			_attributesArray.push(["portType", portType, "mandatory"]);
			_attributesArray.push(["operation", operation, "mandatory"]);			
			_attributesArray.push(["inputVariable", inputVariable, "mandatory"]);
			_attributesArray.push(["outputVariable",outputVariable, "mandatory"]);
			_attributesArray.push(["joinCondition", joinCondition, "optional"]);
			_attributesArray.push(["suppressJoinFailure", suppressJoinFailure, "optional"]);
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			for ( var i:int = 0; i < attributesArray.length; i++){
				switch(attributeName){
					case "name":
						_attributesArray[0][1] = attributeValue;
						name = attributeValue;
						break;
					case "partnerLink":
						_attributesArray[1][1] = attributeValue;
						partnerLink = attributeValue;
						break;
					case "portType":
						_attributesArray[2][1] = attributeValue;
						portType = attributeValue;
						break;
					case "operation":
						_attributesArray[3][1] = attributeValue;
						operation = attributeValue;
						break;
					case "inputVariable":
						_attributesArray[4][1] = attributeValue;
						inputVariable = attributeValue;
						break;
					case "outputVariable":
						_attributesArray[5][1] = attributeValue;
						outputVariable = attributeValue;
						break;
					case "joinCondition":
						_attributesArray[6][1] = attributeValue;
						joinCondition = Boolean (attributeValue);
						break;
					case "suppressJoinFailure":
						_attributesArray[7][1] = attributeValue;
						suppressJoinFailure = Boolean (attributeValue);
						break;		
					default:
						trace("ooops ... Wrong Attribute Name Passed to Invoke: " + attributeName);		
				}				
			}
			fillAttributes();
		}
		
		public function getName():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "name"){
					if(_attributesArray[i][1]){
						return _attributesArray[i][1];
					}
				}				
			}
			return "invoke";
		}
		
		public function populateAttributes(attNamesList:XMLList):String {
			var processName:String = "";
			for (var i:int = 0; i < attNamesList.length(); i++) {	
				//trace(attNamesList[i].name() + "  " + attNamesList[i])			
				updateAttributesArray(attNamesList[i].name(),attNamesList[i]);  			
    			
    			if(attNamesList[i].name() == "name"){
    				processName = attNamesList[i];
    			}
			}
			fillAttributes();		
			//super.printArray();	
			return processName;			
		}
		
		public function updateSubActivitiesArray(type:String, activity:Object):void{}
		
		public function printArray():void {
			for ( var i:int = 0; i < attributesArray.length; i++){
				for (var j:int = 0; j < attributesArray[i].length; j++){
					trace(attributesArray[i][j])
				}
			}
		}
	}
}