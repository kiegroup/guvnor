package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.CaseDO;
	
	public class Case extends CompositeActivity
	{
		private var _dataObject:CaseDO;
		
		public function Case(parentValue:UIComponent, name:String, 
			type:String, caseDOValue:CaseDO){			
			super(parentValue, name, WorkflowActivities.CASE);
			
			super.dragable = false;	
					
			
			setStyle("backgroundColour","#FFBBFF");
			
			_dataObject = caseDOValue;				
		}
		
		public function get dataObject():CaseDO {
			return _dataObject;
		}
		
		public function set dataObject(value:CaseDO):void {
			_dataObject = value;
		}
		
	}
}