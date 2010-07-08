package bpel.editor.gridcc.data
{
	public class WhileDO extends ActivityDO implements DOInterface
	{
		private static var counter:int = 1;
		private var condition:Boolean = false; 
		private var _subActivitiesArray:Array; 
		
		// sub activities array can have only one element
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
		
		public function WhileDO(){
			super();
			//fillAttributes();
		}
		private function fillAttributes():void {	
			_attributesArray = new Array();		
			_attributesArray.push(["name", name, "mandatory"]);
			_attributesArray.push(["condition", condition, "mandatory"]);
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
					case "condition":
						//_attributesArray[i][1] = attributeValue;
						condition = Boolean (attributeValue);
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
						trace("SequenceDO ooops ... Wrong Attribute Name Passed to Me: " + attributeName);			
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
			return "while" + counter++;
		}
		
		public function getCondtion():String {
			for ( var i:int = 0; i < attributesArray.length; i++){
				if(attributesArray[i][0] == "condition"){					
					if(attributesArray[i][1]){
						//trace("Name attribute" + attributesArray[i][1]);
						return attributesArray[i][1]
					}
				}				
			}
			return "";
		}
		
		public function populateAttributes(attNamesList:XMLList):String {
			var activityName:String = "";
			
			for (var i:int = 0; i < attNamesList.length(); i++) {				
				updateAttributesArray(attNamesList[i].name(),attNamesList[i]);  			
    			
    			if(attNamesList[i].name() == "name"){
    				//trace("Do I reach Here in the Sequence");
    				activityName = attNamesList[i];
    			}
			}
			fillAttributes();			
			return activityName;			
		}
	}	
}