package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.SwitchDO;
	
	public class Switch extends CompositeActivity
	{
		private var _dataObject:SwitchDO;
		
		public function Switch(parentValue:UIComponent, name:String, 
			type:String, switchDOValue:SwitchDO){			
			super(parentValue, name, WorkflowActivities.SWITCH);
			
			super.dragable = false;	
					
			
			setStyle("backgroundColour","#FFBBFF");
			
			_dataObject = switchDOValue;				
		}
		
		public function get dataObject():SwitchDO {
			return _dataObject;
		}
		
		public function set dataObject(value:SwitchDO):void {
			_dataObject = value;
		}		
	}
}