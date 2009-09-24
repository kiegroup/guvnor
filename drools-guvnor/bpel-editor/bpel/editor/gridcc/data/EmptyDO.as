package bpel.editor.gridcc.data
{
	public class EmptyDO extends ActivityDO implements DOInterface
	{	
		private static var counter:int = 1;	
		public function EmptyDO(){			
			super();			
		}
		
		private function fillAttributes():void {
			_attributesArray = new Array();			
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {
			for ( var i:int = 0; i < attributesArray.length; i++){
				switch(attributeName){
					default:
						trace("ooops ... Wrong Attribute Name Passed to Receive: " + attributeName);	
				}
			}			
		}		
		
		public function getName():String {			
			return "empty" + counter++;
		}
		
		public function populateAttributes(attNamesList:XMLList):String {					
			return "empty";			
		}
		
		public function updateSubActivitiesArray(type:String, activity:Object):void {}
	}
}