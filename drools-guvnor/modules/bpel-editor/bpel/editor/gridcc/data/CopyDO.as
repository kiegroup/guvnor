package bpel.editor.gridcc.data
{
	public class CopyDO implements DOInterface
	{
		[Bindable]
		private var _toDO:ToDO;
		
		[Bindable]
		private var _fromDO:FromDO
		
		public function set fromDO(dataObject:FromDO):void {
			_fromDO = dataObject;
		}
		
		public function get fromDO():FromDO {
			return _fromDO;
		}
		
		public function set toDO(dataObject:ToDO):void {
			_toDO = dataObject;
		}
		
		public function get toDO():ToDO {
			return _toDO;
		}
		
		public function updateAttributesArray(attributeName:String, attributeValue:String):void {			
			// Doing Nothing
		}
		
		public function updateSubActivitiesArray(type:String, activity:Object):void{
			if(type == "to"){
				//toDO = 
				//ToDO (activity).printArray();
				_toDO = ToDO (activity);
				//trace(activity);
				//trace("To Activity added in the Copy")
			} else if (type == "from"){
				//fromDO = FromDO (activity)
				//FromDO(activity).printArray();
				_fromDO = FromDO (activity)
				//trace(activity);
				//trace("From Activity added in the Copy: "+ activity);
			}
		}
		
		public function printArray():void {
			// Doing Nothing
		}
		
		public function getName():String {
			// Doing Nothing
			return "copy";
		}
		
		public function populateAttributes(attNamesList:XMLList):String {
			// Doing Nothing
			return null;		
		}
		
	}
}