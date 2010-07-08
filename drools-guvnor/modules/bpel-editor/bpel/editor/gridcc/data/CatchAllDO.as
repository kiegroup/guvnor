package bpel.editor.gridcc.data
{
	public class CatchAllDO extends ActivityDO implements DOInterface
	{
		private static var counter:int = 1;
		private var childCounter:int = 1;
		private var _subActivitiesArray:Array; 
		
		public function get subActivitiesArray():Array {
			return _subActivitiesArray;
		}
				
		[Bindable]
		public function set subActivitiesArray(subActivitiesArrayValue:Array):void{
			subActivitiesArray = subActivitiesArrayValue;
		}
		
		public function updateSubActivitiesArray(type:String, activity:Object):void {
			if(!_subActivitiesArray){
				_subActivitiesArray = new Array();
			}
			_subActivitiesArray.push([type,ActivityDO(activity)]);			
		}
		
		public function CatchAllDO(){
			super();
			//fillAttributes();
		}
		private function fillAttributes():void {	
			_attributesArray = new Array();		
			_attributesArray.push(["name", name, "mandatory"]);
			_attributesArray.push(["joinCondition", joinCondition, "optional"]);
			_attributesArray.push(["suppressJoinFailure", suppressJoinFailure, "optional"]);
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			//trace("attributeName: " + attributeName + " attributeValue: " + attributeValue);
			for ( var i:int = 0; i < attributesArray.length; i++){
				switch(attributeName){
					case "name":
						//_attributesArray[i][1] = attributeValue;
						name = attributeValue;
						break;					
					case "joinCondition":
						//_attributesArray[i][1] = attributeValue;
						joinCondition = Boolean (attributeValue);
						break;
					case "suppressJoinFailure":
						//_attributesArray[i][1] = attributeValue;
						suppressJoinFailure = Boolean (attributeValue);
						break;		
					default:
						trace("FaultHandlersDO ooops ... Wrong Attribute Name Passed to Me: " + attributeName);			
				}
			}
		}
		
		public function getName():String {
			for ( var i:int = 0; i < attributesArray.length; i++){
				if(attributesArray[i][0] == "name"){					
					if(attributesArray[i][1]){
						//trace("Name attribute" + attributesArray[i][1]);
						return attributesArray[i][1]
					}
				}				
			}
			return "faultHandlers" + counter++;
		}
		
		public function populateAttributes(attNamesList:XMLList):String {
			var processName:String = "";
			
			for (var i:int = 0; i < attNamesList.length(); i++) {				
				updateAttributesArray(attNamesList[i].name(),attNamesList[i]);  			
    			
    			if(attNamesList[i].name() == "name"){
    				if(_attributesArray[i][1]){
						return _attributesArray[i][1]
					}
    			}
			}
			fillAttributes();			
			return processName;			
		}
	}
}