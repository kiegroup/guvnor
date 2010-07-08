package bpel.editor.gridcc.utility
{
	import mx.containers.*;
	public class ComponenetPanels
	{
		private static var BPELActivitiesPanel:ApplicationControlBar;
		private static var QoSActivities:ApplicationControlBar = new ApplicationControlBar();
		private static var WSRegistry:ApplicationControlBar = new ApplicationControlBar();
		private static var SchemaRegistry:ApplicationControlBar = new ApplicationControlBar();
		private static var propertiesPanel:ApplicationControlBar;
		
		public function ComponenetPanels(){
			/*
			if(!BPELActivities){
				BPELActivities = new ApplicationControlBar();
			}
			if(!QoSActivities){
				QoSActivities = new ApplicationControlBar();
			}
			if(!WSRegistry){
				WSRegistry = new ApplicationControlBar();
			}
			if(!SchemaRegistry){
				SchemaRegistry = new ApplicationControlBar();
			}
			if(!propertiesPanel){
				propertiesPanel = new ApplicationControlBar();
			}		
			*/	
		}
		
		public function getPropertiesPanel():ApplicationControlBar{
			if(propertiesPanel){
				return propertiesPanel;
			}			
			return null;
		}
		
		public function setPropertiesPanel(propertiesPanelValue:ApplicationControlBar):void {
			if(!propertiesPanel){
				propertiesPanel = propertiesPanelValue;
				trace("PropertiesPanel set successfully");
			} else {
				trace("OOOPS    PropertiesPanel is already set");
			}
		}
		
		public function getBPELActivitiesPanel():ApplicationControlBar{
			if(BPELActivitiesPanel){
				return BPELActivitiesPanel;
			}			
			return null;
		}
		
		public function setBPELActivitiesPanel(BPELActivitiesPanelValue:ApplicationControlBar):void {
			if(!BPELActivitiesPanel){
				BPELActivitiesPanel = BPELActivitiesPanelValue;
				//trace("BPELPanel set successfully");
			} else {
				//trace("OOOPS    BPELPanel is already set");
			}
		}
	}
}