package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	//import mx.containers.Canvas;	  
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.VariablesDO;
	
	public class Variables extends CompositeActivity
	{
		private var _dataObject:VariablesDO;
		
		public function Variables(parentValue:UIComponent, name:String, type:String, variablesDOValue:VariablesDO){
			//trace("Variables: " + name + "  " + type);			
			super(parentValue, name, WorkflowActivities.VARIABLES);
			
			super.dragable = false;	
					
			
			setStyle("backgroundColour","#FFBBFF");
			
			_dataObject = variablesDOValue;	
		}
		
		public function get dataObject():VariablesDO {
			return _dataObject;
		}
		
		public function set dataObject(value:VariablesDO):void {
			_dataObject = value;
		}
	}	
}