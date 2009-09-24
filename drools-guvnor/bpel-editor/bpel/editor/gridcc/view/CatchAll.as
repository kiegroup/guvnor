package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.CatchAllDO;
	
	public class CatchAll extends CompositeActivity
	{
		private var _dataObject:CatchAllDO;
		
		public function CatchAll(parentValue:UIComponent, name:String, 
			type:String, catchAllDOValue:CatchAllDO){			
			super(parentValue, name, WorkflowActivities.CATCHALL);
			
			super.dragable = false;	
					
			
			setStyle("backgroundColour","#FFBBFF");
			
			_dataObject = catchAllDOValue;	
		}
		
		public function get dataObject():CatchAllDO {
			return _dataObject;
		}
		
		public function set dataObject(value:CatchAllDO):void {
			_dataObject = value;
		}
	}
}