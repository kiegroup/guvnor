package bpel.editor.gridcc.data
{
	public class ReplyDO extends ActivityDO implements DOInterface
	{
		public var partnerLink:String;
		public var portType:String;
		public var operation:String;
		public var variable:String;
		public var faultName:String;
		
		public function ReplyDO(){			
			super();
			fillAttributes();
		}
		
		private function fillAttributes():void {
			_attributesArray = new Array();
			_attributesArray.push(["name", name, "mandatory"]);	
			_attributesArray.push(["partnerLink", partnerLink, "mandatory"]);
			_attributesArray.push(["portType", portType, "mandatory"]);
			_attributesArray.push(["operation", operation, "mandatory"]);			
			_attributesArray.push(["variable", variable, "mandatory"]);
			_attributesArray.push(["faultName", faultName, "optional"]);
			_attributesArray.push(["joinCondition", joinCondition, "optional"]);
			_attributesArray.push(["suppressJoinFailure", suppressJoinFailure, "optional"]);
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			for ( var i:int = 0; i < attributesArray.length; i++){
				switch(attributeName){
					case "name":
						_attributesArray[i][1] = attributeValue;
						name = attributeValue;
						break;
					case "partnerLink":
						_attributesArray[i][1] = attributeValue;
						partnerLink = attributeValue;
						break;
					case "portType":
						_attributesArray[i][1] = attributeValue;
						portType = attributeValue;
						break;
					case "operation":
						_attributesArray[i][1] = attributeValue;
						operation = attributeValue;
						break;
					case "variable":
						_attributesArray[i][1] = attributeValue;
						variable = attributeValue;
						break;
					case "faultName":
						_attributesArray[i][1] = attributeValue;
						faultName = (attributeValue);
						break;
					case "joinCondition":
						_attributesArray[i][1] = attributeValue;
						joinCondition = Boolean (attributeValue);
						break;
					case "suppressJoinFailure":
						_attributesArray[i][1] = attributeValue;
						suppressJoinFailure = Boolean (attributeValue);
						break;		
					default:
						trace("ooops ... Wrong Attribute Name Passed to Reply: " + attributeName);		
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
		}*/
		
		public function getName():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "name"){
					if(_attributesArray[i][1]){
						return _attributesArray[i][1];
					}
				}				
			}
			return "reply";
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