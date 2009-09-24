package  bpel.editor.gridcc.view
{
	import mx.controls.Button;
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.constant.VisualCoordinateConstant;
	import bpel.editor.gridcc.data.*;
	
	import flash.events.MouseEvent;
	import mx.managers.PopUpManager;
	import bpel.editor.gridcc.controller.WorkflowManager;
	
	public class Receive extends Button
	{
		private var _receiveDO:ReceiveDO;		
		
		private var _activityYCoordinate:int;
		
		private var _parentActivity:CompositeActivity;
		
		
		
		public function Receive(parentValue:CompositeActivity, receiveDOValue:ReceiveDO, yCoordinate:int){
			super();
			_parentActivity = parentValue
			_receiveDO = receiveDOValue;
			_activityYCoordinate = yCoordinate;	
		}
		
		override protected function createChildren():void {
			super.createChildren();			
				
			//this.label = "Receive";			
			this.label = _receiveDO.getName();
			this.name = _receiveDO.getName();
				
			this.height = VisualCoordinateConstant.buttonHeight;
			this.width = VisualCoordinateConstant.buttonWidth;
				
			this.y = _activityYCoordinate;
			this.x = (_parentActivity.width - this.width)/2
				
			// Add Action Listener in sub class if required				
			this.addEventListener(MouseEvent.CLICK, mouseClickHandler);					
				
			this.setStyle("color","red");								
		}
		
		public function get receiveDO():ReceiveDO{
			return _receiveDO;
		}
		
		public function set receiveyDO(receiveDOValue:ReceiveDO):void{
			_receiveDO = receiveDOValue;
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
			
			var receivePopup:ReceivePopup = new ReceivePopup();
			receivePopup.setReceiveDO(receiveDO);
			receivePopup.modifyable = true;
			// bpel.editor.gridcc.controller.WorkflowManager.getInstance().getBPELEditor()
			PopUpManager.addPopUp(receivePopup, 
			this, true);
		}
	}
}