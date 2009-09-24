package  bpel.editor.gridcc.view{
	
	import mx.controls.Button;
	import flash.events.MouseEvent;
	import mx.managers.PopUpManager;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.constant.VisualCoordinateConstant;
	import bpel.editor.gridcc.data.*;
	
	public class PartnerLink extends Button
	{
		private var _partnerLinkDO:PartnerLinkDO;		
		
		private var _activityYCoordinate:int;
		
		private var _parentActivity:CompositeActivity;	
		
		public function PartnerLink(parentValue:CompositeActivity, partnerLinkDOValue:PartnerLinkDO, yCoordinate:int){
			super();
			_parentActivity = parentValue
			_partnerLinkDO = partnerLinkDOValue;
			_activityYCoordinate = yCoordinate;	
		}
		
		override protected function createChildren():void {
			super.createChildren();			
				
			//this.label = "PartnerLink";			
			this.label = _partnerLinkDO.getName();
			this.name = _partnerLinkDO.getName();
				
			this.height = VisualCoordinateConstant.buttonHeight;
			this.width = VisualCoordinateConstant.buttonWidth;
				
			this.y = _activityYCoordinate;
			this.x = (_parentActivity.width - this.width)/2
				
			// Add Action Listener in sub class if required				
			this.addEventListener(MouseEvent.CLICK, mouseClickHandler);					
				
			//this.setStyle("color","blue");								
		}
		
		public function get partnerLinkDO():PartnerLinkDO{
			return _partnerLinkDO;
		}
		
		public function set partnerLinkDO(partnerLinkDOValue:PartnerLinkDO):void{
			_partnerLinkDO = partnerLinkDOValue;
		}
		
		public function get activityYCoordinate():int {
			return _activityYCoordinate
		}
		
		public function set activityYCoordinate(value:int):void {
			activityYCoordinate = value;
		}
		
		public function get parentActivity():CompositeActivity {
			return _parentActivity;
		}
		
		public function set parentAcitivity(value:CompositeActivity):void {
			_parentActivity = value;
		}
		
		private function mouseClickHandler(event:MouseEvent):void{
			var partnerLinkPopup:PartnerLinkPopup = new PartnerLinkPopup();					    
			
			//partnerLinkDO.printArray();		
			 //trace("partnerLinkDO.partnerRole: " + partnerLinkDO.partnerRole); 
			 //trace("partnerLinkDO.myRole: " + partnerLinkDO.myRole);   
			partnerLinkPopup.setPartnerLinkDO(partnerLinkDO);
			partnerLinkPopup.modifyable = true;
			PopUpManager.addPopUp(partnerLinkPopup, this, true);
		}
	}	
}