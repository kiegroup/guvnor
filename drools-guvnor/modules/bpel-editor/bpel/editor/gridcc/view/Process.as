package  bpel.editor.gridcc.view
{
	//import mx.core.UIComponent;	
	import mx.containers.Canvas;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.ProcessDO;
	
	public class Process extends CompositeActivity
	{		
		private var _dataObject:ProcessDO;
		
		public function Process(parentValue:Canvas, name:String, type:String, processDOValue:ProcessDO){					
			super(parentValue, name, WorkflowActivities.PROCESS);
			
			super.dragable = false;	
			
			
			_dataObject = processDOValue;					
		}	
		
		public function get dataObject():ProcessDO {
			return _dataObject;
		}
		
		public function set dataObject(value:ProcessDO):void {
			_dataObject = value;
		}
	}
}