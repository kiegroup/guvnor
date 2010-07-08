package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	import mx.containers.Canvas;
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.AssignDO;
	
	public class Assign extends CompositeActivity
	{
		private var _dataObject:AssignDO;
		
		public function Assign(parentValue:UIComponent, name:String, type:String, assignDOValue:AssignDO){
			super(parentValue, name, WorkflowActivities.ASSIGN);			
			super.dragable = false;		
			
			super.backgroundColour="#FFBBFF";
			_dataObject = assignDOValue;	
		}
		
		public function get dataObject():AssignDO {
			return _dataObject;
		}
		
		public function set dataObject(value:AssignDO):void {
			_dataObject = value;
		}
	}
}