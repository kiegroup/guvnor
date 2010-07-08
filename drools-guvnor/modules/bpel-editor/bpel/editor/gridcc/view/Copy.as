package bpel.editor.gridcc.view
{
	import mx.controls.Button;
	import flash.events.MouseEvent;
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.constant.VisualCoordinateConstant;
	import bpel.editor.gridcc.data.*;
	import mx.managers.PopUpManager;
	
	
	public class Copy extends Button
	{
		private var _copyDO:CopyDO;		
		
		private var _activityYCoordinate:int;
		
		private var _parentActivity:CompositeActivity;
		
		
		
		public function Copy(parentValue:CompositeActivity, copyDOValue:CopyDO, yCoordinate:int){
			super();
			_parentActivity = parentValue
			_copyDO = copyDOValue;
			_activityYCoordinate = yCoordinate;	
		}
		
		override protected function createChildren():void {
			super.createChildren();			
				
				
			this.label =_copyDO.getName();
			this.name =_copyDO.getName();
				
			this.height = VisualCoordinateConstant.buttonHeight;
			this.width = VisualCoordinateConstant.buttonWidth;
				
			this.y = _activityYCoordinate;
			this.x = (_parentActivity.width - this.width)/2
				
			// Add Action Listener in sub class if required				
			this.addEventListener(MouseEvent.CLICK, mouseClickHandler);					
				
			//this.setStyle("color","blue");								
		}
		
		public function get copyDO():CopyDO{
			return _copyDO;
		}
		
		public function set receiveyDO(copyDOValue:CopyDO):void{
			_copyDO = copyDOValue;
		}
		
		public function get activityYCoordinate():int {
			return _activityYCoordinate
		}
		
		public function set activityYCoordinate(value:int):void {
			activityYCoordinate = value;
		}
		
		public function get parentActivity():CompositeActivity {
			return _parentActivity;
		}
		
		public function set parentAcitivity(value:CompositeActivity):void {
			_parentActivity = value;
		}	
		
		private function mouseClickHandler(event:MouseEvent):void{
			//trace("mouseClickHandler of Copy");
			//_variableDO.printArray();
			
			var copyPopUp:CopyPopUp = new CopyPopUp();
			
			copyPopUp.setFromDO(_copyDO.fromDO);
			copyPopUp.setToDO(_copyDO.toDO);
			copyPopUp.modifyable = true;
					    
			PopUpManager.addPopUp(copyPopUp, this, true);			
			//_copyDO.fromDO.printArray();	
		}
	}
}