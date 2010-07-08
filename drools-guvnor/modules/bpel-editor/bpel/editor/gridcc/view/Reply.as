package  bpel.editor.gridcc.view
{
	import mx.controls.Button;
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.constant.VisualCoordinateConstant;
	import bpel.editor.gridcc.data.*;
	
	import flash.events.MouseEvent;
	import mx.managers.PopUpManager;
	
	public class Reply extends Button
	{
		private var _replyDO:ReplyDO;		
		
		private var _activityYCoordinate:int;
		
		private var _parentActivity:CompositeActivity;
		
		
		
		public function Reply(parentValue:CompositeActivity, replyDOValue:ReplyDO, yCoordinate:int){
			super();
			_parentActivity = parentValue
			_replyDO = replyDOValue;
			_activityYCoordinate = yCoordinate;	
		}
		
		override protected function createChildren():void {
			super.createChildren();			
				
			//this.label = "Reply";			
			this.label = _replyDO.getName();
			this.name = _replyDO.getName();
				
			this.height = VisualCoordinateConstant.buttonHeight;
			this.width = VisualCoordinateConstant.buttonWidth;
				
			this.y = _activityYCoordinate;
			this.x = (_parentActivity.width - this.width)/2
				
			// Add Action Listener in sub class if required				
			this.addEventListener(MouseEvent.CLICK, mouseClickHandler);					
				
			this.setStyle("color","red");								
		}
		
		public function get replyDO():ReplyDO{
			return _replyDO;
		}
		
		public function set replyDO(replyDOValue:ReplyDO):void{
			_replyDO = replyDOValue;
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
			trace("mouseClickHandler");
			//_variableDO.printArray();
			//trace("Number of Children in parent: " + this.parent.numChildren + this.parent.parent.name);
			//trace("My Index in parent ChildList: " + this.parent.getChildIndex(this));
			
			var replyPopup:ReplyPopup = new ReplyPopup();
			replyPopup.setReplyDO(replyDO);
			replyPopup.modifyable = true;
			// bpel.editor.gridcc.controller.WorkflowManager.getInstance().getBPELEditor()
			PopUpManager.addPopUp(replyPopup, this, true);			
		}
	}	
	
}