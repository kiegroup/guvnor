package  bpel.editor.gridcc.view
{
	//import mx.core.UIComponent;
	//import mx.core.Container;
	import mx.controls.Button;
	import flash.events.MouseEvent;
	import mx.managers.PopUpManager;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.constant.VisualCoordinateConstant;
	import bpel.editor.gridcc.data.*;
	
	public class Variable extends Button
	{
		private var _variableDO:VariableDO;		
		
		private var _activityYCoordinate:int;
		
		private var _parentActivity:CompositeActivity;
		
		public function Variable(parentValue:CompositeActivity, variableDOValue:VariableDO, yCoordinate:int){
			super();
			_parentActivity = parentValue
			_variableDO = variableDOValue;
			_activityYCoordinate = yCoordinate;			
		}
		
		override protected function createChildren():void {
			super.createChildren();			
				
			//this.label = "Variable";			
			this.label =_variableDO.getName();
			this.name =_variableDO.getName();
				
			this.height = VisualCoordinateConstant.buttonHeight;
			this.width = VisualCoordinateConstant.buttonWidth;
				
			this.y = _activityYCoordinate;
			this.x = (_parentActivity.width - this.width)/2
				
			// Add Action Listener in sub class if required				
			this.addEventListener(MouseEvent.CLICK, mouseClickHandler);					
				
			//this.setStyle("color","blue");								
		}
		
		public function get variableDO():VariableDO{
			return _variableDO;
		}
		
		public function set variableDO(variableDOValue:VariableDO):void{
			_variableDO = variableDOValue;
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
		
		private function mouseClickHandler(event:MouseEvent):void {
			
			var variablePopup:VariablePopup = new VariablePopup();		    
		    variablePopup.setVariableDO(variableDO);
		    variablePopup.modifyable =  true;
		    PopUpManager.addPopUp(variablePopup, this, true);
					    	
		}
	}
}