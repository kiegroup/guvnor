package bpel.editor.gridcc.controller
{
	import flash.events.Event;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.net.URLRequestHeader;
	import flash.net.URLRequestMethod;
	import flash.net.URLVariables;
	
	public class RetrieveWorkflow
	{
		/**
         * The Singleton instance of RetrieveWorkflow
         */        
        protected static var instance:RetrieveWorkflow; 
        
        private var uuid:String;
        private var fileName:String;
        private var dirName:String;
        private var servletURL:String;
        private var tempURLRequest:URLRequest;
        private var tempWorkflowID:String;
        
        /**
         * Constructor
         * 
         * Instantiates Singleton instance of RetrieveWorkflow
         */        
        public function RetrieveWorkflow()
        {
            if (RetrieveWorkflow.instance == null) 
            {                
                RetrieveWorkflow.instance = this;                 
            }            
        }
        
        public static function createInstance(uuidValue:String, fileNameValue:String, 
        	servletURLValue:String, dirNameValue:String):RetrieveWorkflow
        {
            if (instance == null) {
               instance = new RetrieveWorkflow();
               if(uuidValue)
               	instance.uuid = uuidValue;
               if(fileNameValue)
               	instance.fileName = fileNameValue;
               if(dirNameValue)
               	instance.dirName = dirNameValue;
               if(servletURLValue)
               	instance.servletURL = servletURLValue;
            }
            return instance;
        }
        
        public static function getInstance():RetrieveWorkflow{
        	if(instance == null) {
        		// Do Nothing
        	} 
        	return instance;       	        	
        } 
        
        public function loadWorkflowFromServer(fileType:String):void {
						
			//trace("loadWorkflowFromServer " + fileType);
			
			tempURLRequest = new URLRequest(servletURL);
			var workflowName:String = "uuid=" + uuid + "&workflowName=" + fileName + "&";
			var operationString:String = "operation=retrieve&";
			var fullPath:String = "fullPath="+dirName+"&"; 
			//var fileNameTemp:String = "fileName="+ processCreator.BPELProcess.@name ;
			var fileNameTemp:String = "fileName="+ fileName;
			
			switch(fileType){
				case "wsdl": 
					fileNameTemp = fileNameTemp+".wsdl";
									
					var tempWSDLLoader:URLLoader = new URLLoader();
					var tempVariables2:URLVariables = new URLVariables(workflowName + operationString + fullPath + fileNameTemp );
					tempWSDLLoader.addEventListener(Event.COMPLETE, completeWSDLLoadHandler);
					tempURLRequest.data = tempVariables2;
					tempWSDLLoader.load(tempURLRequest);
					break;
					
				case "bpel":
					fileNameTemp = fileNameTemp+".bpel";
								
					var tempBPELLoader:URLLoader = new URLLoader();
					var tempBPELVariables:URLVariables = new URLVariables(workflowName + operationString + fullPath + fileNameTemp);	
					tempBPELLoader.addEventListener(Event.COMPLETE, completeBPELLoadHandler);				
					
					tempURLRequest.data = tempBPELVariables;					
					tempBPELLoader.load(tempURLRequest);			
					//tempBPELLoader.load(new URLRequest("F:/JBProject2005/VCRUploadDownloadServlet/Tomcat/temp/Full_FeedbackCorrection_withCREAM/bpel/Full_FeedbackCorrection_withCREAM/Full_FeedbackCorrection_withCREAM_Original.bpel"));			
					
					break;
					
				case "pdd":
					fileNameTemp = fileNameTemp+".pdd";
					
					var tempPDDLoader:URLLoader = new URLLoader();
					var tempPDDVariables:URLVariables = new URLVariables(workflowName + operationString + fullPath + fileNameTemp);	
					tempPDDLoader.addEventListener(Event.COMPLETE, completePDDLoadHandler);	
					
					tempURLRequest.data = tempPDDVariables;					
					tempPDDLoader.load(tempURLRequest);	
					
					break;
					
				case "catalog":
					fileNameTemp = "fileName=wsdlCatalog.xml";
					
					var tempWCLoader:URLLoader = new URLLoader();
					var tempWCVariables:URLVariables = new URLVariables(workflowName + operationString + fullPath + fileNameTemp);	
					tempWCLoader.addEventListener(Event.COMPLETE, completeWCLoadHandler);	
					
					tempURLRequest.data = tempWCVariables;					
					tempWCLoader.load(tempURLRequest);					
					break;
						
				case "subDoc":
					fileNameTemp = fileNameTemp+"-wfms.xml";
					
					var tempSDLoader:URLLoader = new URLLoader();
					var tempSDVariables:URLVariables = new URLVariables(workflowName + operationString + fullPath + fileNameTemp);	
					tempSDLoader.addEventListener(Event.COMPLETE, completeSDLoadHandler);				
					tempURLRequest.data = tempSDVariables;					
					tempSDLoader.load(tempURLRequest);					
					break;
			}
		}			
		
		private function completeBPELLoadHandler(event:Event):void{
			//trace("BBPPEELL");
			trace(event.target.data);
			XML.ignoreComments = false;
			XML.ignoreProcessingInstructions = false;
			
			var tempString:String = String (event.target.data);	
			//tempString = tempString.replace(" ","&#13;&#10;")
			var myPattern:RegExp = /&#13;&#10;/g;
			tempString = tempString.replace(myPattern, "");			
				
			myPattern = /&/g;		
			//tempString = tempString.replace(myPattern,"%26");
			//trace(tempString);
			
			var tempBPEL:XML;
			try {
			     tempBPEL = new XML(tempString);
			} catch (error:Error) {
			      tempBPEL = new XML("<process>Error in Loading</process>");
			} finally {
			     // statements
			}
			
			if(tempBPEL)
				ProcessCreator.getInstance().BPELProcess = tempBPEL;
			//trace(ProcessCreator.getInstance().BPELProcess);
			
			WorkflowManager.getInstance().createGUIFromBPELProcess();
			loadWorkflowFromServer("wsdl");
		}
		
		private function completeWSDLLoadHandler(event:Event):void{			
			WSDLCreator.getInstance().WorkflowWSDL = new XML (event.target.data);
			loadWorkflowFromServer("pdd");
		}
		
		private function completePDDLoadHandler(event:Event):void{
			//trace("PDD");
			//trace(event.target.data);
			
			// This part is only to avoid the complications
			//trace("WS Local Name passed to PDD Creator: " + fileName);
			var pddCreator:PDDCreator = PDDCreator.createInstance(this.fileName,"Dummy/Namespace");					
			pddCreator.WorkflowPDD = null;
			
			// This is acturally loading and assigning the PDD file
			pddCreator.WorkflowPDD = new XML (event.target.data);			
			loadWorkflowFromServer("catalog");
		}
		
		private function completeWCLoadHandler(event:Event):void{
			//trace("WC");
			//trace(event.target.data);
			
			// This part is only to avoid the complications
			//trace("WS Local Name passed to WC Creator: " + fileName);
			var catalogCreator:WSDLCatalogCreator = WSDLCatalogCreator.createInstance(this.fileName);					
			catalogCreator.WSDLCatalog = null;
			
			// This is acturally loading and assigning the PDD file
			catalogCreator.WSDLCatalog = new XML (event.target.data);			
			loadWorkflowFromServer("subDoc");
		}
		
		private function completeSDLoadHandler(event:Event):void{
			//trace("WC");
			//trace(event.target.data);			
			// This part is only to avoid the complications
			//trace("WS Local Name passed to S Creator: " + fileName);
			var loader:URLLoader = URLLoader(event.target);
			var subDOcCreator:SubmissionDocumentCreator = SubmissionDocumentCreator..createInstance(
					this.fileName, "Dummy/Namespace");					
			subDOcCreator.SubmissionDocument = null;
			
			// This is acturally loading and assigning the PDD file
			subDOcCreator.SubmissionDocument = new XML (event.target.data);			
			//loadWorkflowFromServer("QoS");
			loader.close();
		}
		
		public function retrieveMonitoringWorkflow(workflowID:String):void{			
			//trace("********************   retrieveMonitoringWorkflow *******");
			var header:URLRequestHeader = new URLRequestHeader("pragma", "no-cache");

			tempWorkflowID = workflowID;
			//trace("servletURL: " + servletURL);
			tempURLRequest = null;
			tempURLRequest = new URLRequest(servletURL);
			tempURLRequest.requestHeaders.push(header);
			tempURLRequest.method = URLRequestMethod.POST;

			var tempWorkflowID:String = "wfId=" + workflowID + "&";
			var operationString:String = "operation=getStatus";
								
			var tempMonitoringBPELLoader:URLLoader = new URLLoader();
			tempMonitoringBPELLoader.addEventListener(Event.COMPLETE, completeMonitoringLoadHandler);
			
			var tempVariables:URLVariables = new URLVariables(tempWorkflowID + operationString /* + fullPath + fileNameTemp */);			
			tempURLRequest.data = tempVariables;
			tempMonitoringBPELLoader.load(tempURLRequest);
			var monitoringBPEL:MonitoringWorkflow = MonitoringWorkflow.getInstance(tempWorkflowID);
			monitoringBPEL.update = true;					
		}	
		
		private function completeMonitoringLoadHandler(event:Event):void{
			//trace("********************   completeMonitoringLoadHandler  *******");
			//trace(event.target.data);			
			var loader:URLLoader = URLLoader(event.target);

			var monitoringBPEL:MonitoringWorkflow = MonitoringWorkflow.getInstance(tempWorkflowID);	
			//trace(event.target.data);
			monitoringBPEL.setMonitoringWorkflow(new XML (event.target.data));	
			monitoringBPEL.update = false;
			loader.close();			
		}	
	}	
}