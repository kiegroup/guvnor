package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.FaultHandlersDO;
	
	public class FaultHandlers extends CompositeActivity
	{
		private var _dataObject:FaultHandlersDO;
		
		public function FaultHandlers(parentValue:UIComponent, name:String, 
			type:String, faultHandlersDOValue:FaultHandlersDO){			
			super(parentValue, name, WorkflowActivities.FAULTHANDLERS);
			
			super.dragable = false;	
					
			
			setStyle("backgroundColour","#FFBBFF");
			
			_dataObject = faultHandlersDOValue;				
		}
		
		public function get dataObject():FaultHandlersDO {
			return _dataObject;
		}
		
		public function set dataObject(value:FaultHandlersDO):void {
			_dataObject = value;
		}
	}
}