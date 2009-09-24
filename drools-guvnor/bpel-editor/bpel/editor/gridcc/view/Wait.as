package bpel.editor.gridcc.view
{
	import mx.controls.Button;
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.constant.VisualCoordinateConstant;
	import bpel.editor.gridcc.data.*;
	
	import flash.events.MouseEvent;
	import flash.events.FocusEvent;
	import mx.managers.PopUpManager;
	
	public class Wait extends Button
	{
		private var _waitDO:WaitDO;		
		
		private var _activityYCoordinate:int;
		
		private var _parentActivity:CompositeActivity;
		
		
		
		public function Wait(parentValue:CompositeActivity, waitDOValue:WaitDO, yCoordinate:int){
			super();
			_parentActivity = parentValue
			_waitDO = waitDOValue;
			_activityYCoordinate = yCoordinate;	
		}
		
		override protected function createChildren():void {
			super.createChildren();			
				
			//this.label = "Name Asif";			
			this.label =_waitDO.getName();
			this.name =_waitDO.getName();
				
			this.height = VisualCoordinateConstant.buttonHeight;
			this.width = VisualCoordinateConstant.buttonWidth;
				
			this.y = _activityYCoordinate;
			this.x = (_parentActivity.width - this.width)/2
				
			// Add Action Listener in sub class if required				
			this.addEventListener(MouseEvent.CLICK, mouseClickHandler);	
			//this.addEventListener(FocusEvent.FOCUS_IN, mouseClickHandler);				
				
			//this.setStyle("color","blue");								
		}
		
		public function get waitDO():WaitDO{
			return _waitDO;
		}
		
		public function set waitDO(waitDOValue:WaitDO):void{
			_waitDO = waitDOValue;
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
			//trace("mouseClickHandler");
			//_variableDO.printArray();
			//trace("Number of Children in parent: " + this.parent.numChildren + this.parent.parent.name);
			//trace("My Index in parent ChildList: " + this.parent.getChildIndex(this));
			var waitPopup:WaitPopup = new WaitPopup();
			waitPopup.setWaitDO(waitDO);
			waitPopup.modifyable = true;
			
			PopUpManager.addPopUp(waitPopup, this, true);
		}
	}
}