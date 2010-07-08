package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
		import bpel.editor.gridcc.data.WhileDO;
	
	public class While extends CompositeActivity
	{
		private var _dataObject:WhileDO;
		
		public function While(parentValue:UIComponent, name:String, 
			type:String, whileDOValue:WhileDO){			
			super(parentValue, name, WorkflowActivities.WHILE);
			
			super.dragable = false;	
					
			
			setStyle("backgroundColour","#FFBBFF");
			
			_dataObject = whileDOValue;				
		}
		
		public function get dataObject():WhileDO {
			return _dataObject;
		}
		
		public function set dataObject(value:WhileDO):void {
			_dataObject = value;
		}
		
	}
}