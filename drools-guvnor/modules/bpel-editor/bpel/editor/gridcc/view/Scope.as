package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.ScopeDO;
	
	public class Scope extends CompositeActivity
	{
		private var _dataObject:ScopeDO;
		
		public function Scope(parentValue:UIComponent, name:String, 
			type:String, scopeDOValue:ScopeDO){			
			super(parentValue, name, WorkflowActivities.SCOPE);
			
			super.dragable = false;	
					
			
			setStyle("backgroundColour","#FFBBFF");
			
			_dataObject = scopeDOValue;				
		}
		
		public function get dataObject():ScopeDO {
			return _dataObject;
		}
		
		public function set dataObject(value:ScopeDO):void {
			_dataObject = value;
		}		
	}
}