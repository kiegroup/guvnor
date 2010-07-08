package  bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	import mx.containers.Canvas;
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.PartnerLinksDO;
	
	public class PartnerLinks extends CompositeActivity
	{		
		private var _dataObject:PartnerLinksDO;
		
		public function PartnerLinks(parentValue:UIComponent, name:String, type:String, partnerLinksDOValue:PartnerLinksDO){
			super(parentValue, name, WorkflowActivities.PARTNERLINKS);			
			super.dragable = false;		
			
			super.backgroundColour="#FFBBFF";
			_dataObject = partnerLinksDOValue;			
		}
		
		public function get dataObject():PartnerLinksDO {
			return _dataObject;
		}
		
		public function set dataObject(value:PartnerLinksDO):void {
			_dataObject = value;
		}
	}
}