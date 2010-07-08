package bpel.editor.gridcc.data
{
	public class PartnerLinkDO implements DOInterface
	{
		public var myRole:String;
		public var name:String;
		public var partnerLinkType:String;
		public var partnerRole:String;
		
		private var _attributesArray:Array;
		
		public function PartnerLinkDO(){
			fillAttributes();
		}
		
		private function fillAttributes():void {
			_attributesArray = new Array();
			_attributesArray.push(["name", name, "mandatory"]);
			_attributesArray.push(["partnerLinkType", partnerLinkType, "mandatory"]);
			_attributesArray.push(["myRole",myRole, "mandatory"]);			
			_attributesArray.push(["partnerRole", partnerRole, "optional"]);
		}
		public function get attributesArray():Array {
			return _attributesArray;
		}
		
		public function set attributesArray(attribtesValue:Array):void {
			_attributesArray = attribtesValue;
			//printArray();
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			for ( var i:int = 0; i < attributesArray.length; i++){
				switch(attributeName){
					case "name":
						//_attributesArray[0][1] = attributeValue;
						name = attributeValue;
						break;
					case "myRole":
						//_attributesArray[2][1] = attributeValue;
						myRole = attributeValue;
						break;
					case "partnerLinkType":
						//_attributesArray[1][1] = attributeValue;
						partnerLinkType = attributeValue;
						break;
					case "partnerRole":
						//_attributesArray[3][1] = attributeValue;
						partnerRole = attributeValue;
						break;						
					default:
						trace("ooops ... Wrong Attribute Name Passed to PartnerLink: " + attributeName);
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
		}
		*/
		public function getName():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "name"){
					if(_attributesArray[i][1]){
						return _attributesArray[i][1];
					}
				}				
			}
			return "partnerLink";
		}
		
		public function getPartnerLinkType():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "partnerLinkType"){
					return _attributesArray[i][1]
				}				
			}
			return null;
		}
		
		public function getPartnerRole():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "partnerRole"){
					return _attributesArray[i][1]
				}				
			}
			return null;
		}
		
		public function getMyRole():String {
			for ( var i:int = 0; i < _attributesArray.length; i++){
				if(_attributesArray[i][0] == "myRole"){
					return _attributesArray[i][1]
				}				
			}
			return null;
		}
		
		public function populateAttributes(attNamesList:XMLList):String {
			var processName:String = "";
			for (var i:int = 0; i < attNamesList.length(); i++) {				
				updateAttributesArray(attNamesList[i].name(),attNamesList[i]);  			
    			//trace(attNamesList[i].name() + " : " + attNamesList[i])
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