package bpel.editor.gridcc.view
{
	import mx.controls.Button;
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.constant.VisualCoordinateConstant;
	import bpel.editor.gridcc.data.*;
	
	import flash.events.MouseEvent;
	import flash.events.FocusEvent;
	import mx.managers.PopUpManager;
	
	public class Empty extends Button
	{
		private var _emptyDO:EmptyDO;		
		
		private var _activityYCoordinate:int;
		
		private var _parentActivity:CompositeActivity;
		
		
		
		public function Empty(parentValue:CompositeActivity, emptyDOValue:EmptyDO, yCoordinate:int){
			super();
			_parentActivity = parentValue
			_emptyDO = emptyDOValue;
			_activityYCoordinate = yCoordinate;	
		}
		
		override protected function createChildren():void {
			super.createChildren();			
				
			//this.label = "Name Asif";			
			this.label =_emptyDO.getName();
			this.name =_emptyDO.getName();
				
			this.height = VisualCoordinateConstant.buttonHeight;
			this.width = VisualCoordinateConstant.buttonWidth;
				
			this.y = _activityYCoordinate;
			this.x = (_parentActivity.width - this.width)/2							
				
			//this.setStyle("color","blue");								
		}
		
		public function get emptyDO():EmptyDO{
			return _emptyDO;
		}
		
		public function set emptyDO(emptyDOValue:EmptyDO):void{
			_emptyDO = emptyDOValue;
		}
		
		public function get activityYCoordinate():int {
			return _activityYCoordinate;
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
	}
}