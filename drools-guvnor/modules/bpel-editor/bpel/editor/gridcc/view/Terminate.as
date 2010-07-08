package bpel.editor.gridcc.view
{
	import mx.controls.Button;
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.constant.VisualCoordinateConstant;
	import bpel.editor.gridcc.data.TerminateDO;
	
	import flash.events.MouseEvent;
	import flash.events.FocusEvent;
	import mx.managers.PopUpManager;
	
	public class Terminate extends Button
	{
		private var _terminateDO:TerminateDO;		
		
		private var _activityYCoordinate:int;
		
		private var _parentActivity:CompositeActivity;
		
		
		
		public function Terminate(parentValue:CompositeActivity, terminateDOValue:TerminateDO, yCoordinate:int){
			super();
			_parentActivity = parentValue
			_terminateDO = terminateDOValue;
			_activityYCoordinate = yCoordinate;	
		}
		
		override protected function createChildren():void {
			super.createChildren();			
				
			//this.label = "Name Asif";			
			this.label =_terminateDO.getName();
			this.name =_terminateDO.getName();
				
			this.height = VisualCoordinateConstant.buttonHeight;
			this.width = VisualCoordinateConstant.buttonWidth;
				
			this.y = _activityYCoordinate;
			this.x = (_parentActivity.width - this.width)/2							
				
			//this.setStyle("color","blue");								
		}
		
		public function get terminateDO():TerminateDO{
			return _terminateDO;
		}
		
		public function set terminateDO(terminateDOValue:TerminateDO):void{
			_terminateDO = terminateDOValue;
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