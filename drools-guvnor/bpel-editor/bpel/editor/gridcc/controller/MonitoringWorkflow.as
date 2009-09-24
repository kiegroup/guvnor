package bpel.editor.gridcc.controller {
	
	import flash.events.EventDispatcher;
	import flash.events.Event;
	import bpel.editor.gridcc.events.MonitoringArrayUpdate;
	
	public class MonitoringWorkflow extends EventDispatcher {
		/**
         * The Singleton instance of ProcessCreator
         */        
        protected static var instance:MonitoringWorkflow;
        
        
        [Bindable]
        private var monitoringBPEL:XML; 
        
        [Bindable]
        private var monitoringBPELArray:Array;       
        
        private var localWorkflowID:String;
        
        // to wait while update is in process ......
        public var update:Boolean = false;
        
        /**
         * Constructor
         * 
         * Instantiates Singleton instance of MonitoringWorkflow
         */
        public function MonitoringWorkflow()
        {
            if (MonitoringWorkflow.instance == null) 
            {                
                MonitoringWorkflow.instance = this;
            }
            //this.addEventListener(MonitoringArrayUpdate.MONITORING_ARRAY_UPDATE_EVENT, arrayUpdatedEventHandler);
        }
        
        public function arrayUpdatedEventHandler(event:Event):void {
        	trace("arrayUpdatedEventHandler");
        }
        /**
         * Determines if the singleton instance of MonitoringWorkflow
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of MonitoringWorkflow
         */
                   
        public static function getInstance(workflow_ID:String):MonitoringWorkflow
        {
        	// This function to be called only from Process Creator
            if (instance == null) 
            {
               instance = new MonitoringWorkflow();
            }
            
            if(workflow_ID)
            	instance.localWorkflowID = workflow_ID;               
            
            return instance;
        }     
        
        public function getMonitoringWorkflow():XML {
        	//trace("getMonitoringWorkflow");
        	if(localWorkflowID ){
        		
        		// Even if I have the Monitoring BPEL
        		// It may not be the updated one ...
        		// always better to get the latest one ....
        		if(monitoringBPEL){
        			return monitoringBPEL;
        		} else {        	
        			// get the Monitoring BPEL form the server
        		} 
        	} else {
        		workflowFiltering();
        		populateArray();
        	}  
        	return monitoringBPEL;     	
        }
        
        public function getMonitoringArray():Array {
        	//trace("********************  getMonitoringArray");
        	/*
        	while(update){
        		trace("waiting for update");
        	}*/
        	populateArray();
        	//printArray();
        	return monitoringBPELArray;
        }
        
        public function setMonitoringWorkflow(tempWorkflow:XML):void {
        	//trace("********************  setMonitoringWorkflow");
        	//trace(tempWorkflow)
        	monitoringBPEL = tempWorkflow;
        	populateArray();
        	var arrayUpdateEvent:MonitoringArrayUpdate  = new MonitoringArrayUpdate("monitoringArrayUpdateEvent");
        	dispatchEvent(arrayUpdateEvent);

        }
        
        private function workflowFiltering():void {
        	        				
			var ActualBPEL:XML = ProcessCreator.getInstance().BPELProcess;
			monitoringBPEL = new XML("<monitor/>");
			if(ActualBPEL) {
				// Filter all Activities
				var fileteredAllBPEL:XMLList = ActualBPEL..*;
				
				// Filter all activities with name attribute
				var fileteredNameBPEL:XMLList = ActualBPEL..*.(hasOwnProperty("@name"));
				
				// Filter all activities where name contains "_monitor"
				var fileteredNameWithMonitoringBPEL:XMLList = 
					ActualBPEL..*.(hasOwnProperty("@name") && (@name.indexOf("_monitor") != -1));
				
				//trace(fileteredNameWithMonitoringBPEL);
				
				var item:XML;
				var tempActivity:XML = null;
	            for each(item in fileteredNameWithMonitoringBPEL) {
	                //trace("item: " + item.toXMLString());
	                tempActivity = new XML ("<" + item.localName() + "/>");
	                tempActivity.@name = item.@name;
	                tempActivity.@status = "unknown";
	                monitoringBPEL.appendChild(tempActivity);
	            }
   			}
            // For testing purposes  ....!
            trace(monitoringBPEL.toXMLString());			
		}
		
		private function populateArray():void {
			//trace("********************  populateArray");
			if(!localWorkflowID){
				workflowFiltering();
			}
			//push(["partnerLink", partnerLink, "mandatory"]);
			//if(!monitoringBPELArray){
				monitoringBPELArray = new Array();
				monitoringBPELArray.push(["start", "Started", "XXX"]);
			//}
			//if()
			var monitoringActivitiesList:XMLList = monitoringBPEL.children();
			var item:XML;
			for each(item in monitoringActivitiesList) {
			  monitoringBPELArray.push([item.localName(), item.@name, item.@status]);
			}
			monitoringBPELArray.push(["finish", "Finished", "XXX"]);
		}
        
        private function printArray():void {
        	if(monitoringBPELArray){
        		for(var i:int = 0; i < monitoringBPELArray.length; i++){
        			trace(monitoringBPELArray[i][0] + "  " + monitoringBPELArray[i][1] + "  " + monitoringBPELArray[i][2]);
        		}
        	}
        }
	}
}