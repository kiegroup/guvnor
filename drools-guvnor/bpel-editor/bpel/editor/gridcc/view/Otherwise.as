package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.OtherwiseDO;
	
	public class Otherwise extends CompositeActivity
	{
		private var _dataObject:OtherwiseDO;
		
		public function Otherwise(parentValue:UIComponent, name:String, 
			type:String, otherwiseDOValue:OtherwiseDO){			
			super(parentValue, name, WorkflowActivities.OTHERWISE);
			
			super.dragable = false;	
					
			
			setStyle("backgroundColour","#FFBBFF");
			
			_dataObject = otherwiseDOValue;				
		}
		
		public function get dataObject():OtherwiseDO {
			return _dataObject;
		}
		
		public function set dataObject(value:OtherwiseDO):void {
			_dataObject = value;
		}		
	}
}