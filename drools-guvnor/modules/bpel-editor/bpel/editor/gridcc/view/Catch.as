package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.CatchDO;
	
	public class Catch extends CompositeActivity
	{
		private var _dataObject:CatchDO;
		
		public function Catch(parentValue:UIComponent, name:String, 
			type:String, catchDOValue:CatchDO){			
			super(parentValue, name, WorkflowActivities.CATCH);
			
			super.dragable = false;	
					
			
			setStyle("backgroundColour","#FFBBFF");
			
			_dataObject = catchDOValue;				
		}
		
		public function get dataObject():CatchDO {
			return _dataObject;
		}
		
		public function set dataObject(value:CatchDO):void {
			_dataObject = value;
		}
	}
}