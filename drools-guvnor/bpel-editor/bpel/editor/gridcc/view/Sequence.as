package  bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.SequenceDO;
	
	public class Sequence extends CompositeActivity
	{
		private var _dataObject:SequenceDO;
		
		public function Sequence(parentValue:UIComponent, name:String, 
			type:String, sequenceDOValue:SequenceDO){			
			super(parentValue, name, WorkflowActivities.SEQUENCE);
			
			super.dragable = false;	
					
			
			setStyle("backgroundColour","#FFBBFF");
			
			_dataObject = sequenceDOValue;				
		}
		
		public function get dataObject():SequenceDO {
			return _dataObject;
		}
		
		public function set dataObject(value:SequenceDO):void {
			_dataObject = value;
		}		
	}
}