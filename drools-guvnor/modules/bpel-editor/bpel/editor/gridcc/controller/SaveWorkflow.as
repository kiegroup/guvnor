package bpel.editor.gridcc.controller
{
	import flash.events.Event;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.net.URLRequestMethod;
	import flash.net.URLVariables;
	
	public class SaveWorkflow
	{
		/**
         * The Singleton instance of SaveWorkflow
         */        
        protected static var instance:SaveWorkflow; 
        
        private var uuid:String;
        private var fileName:String;
        private var dirName:String;
        private var servletURL:String;
        private var tempURLRequest:URLRequest;
        
        /**
         * Constructor
         * 
         * Instantiates Singleton instance of SaveWorkflow
         */        
        public function SaveWorkflow()
        {
            if (SaveWorkflow.instance == null) 
            {                
                SaveWorkflow.instance = this;                 
            }            
        }
        
        public static function getInstance(uuidValue:String, fileNameValue:String, 
        	servletURLValue:String, dirNameValue:String):SaveWorkflow
        {
            if (instance == null) {
               instance = new SaveWorkflow();
            }            
            instance.uuid = uuidValue;
            instance.fileName = fileNameValue;
            instance.dirName = dirNameValue;
            instance.servletURL = servletURLValue;
            return instance;
        }
        
        public function saveWorkflowOnServer(fileType:String):void {
			//trace("saveWorkflowOnServer " + fileType);
			tempURLRequest = new URLRequest(servletURL);
			tempURLRequest.method = URLRequestMethod.POST;	
			//tempURLRequest.contentType = "text/xml";
			
			var workflowName:String = "uuid=" + uuid + "&workflowName=" + fileName + "&";
			var operationString:String = "operation=save&";
			var fullPath:String = "fullPath="+dirName+"&"; 
			//var fileNameTemp:String = "fileName="+ processCreator.BPELProcess.@name ;
			var fileNameTemp:String = "fileName="+ fileName;
			var fileContent:String = "fileContent=";
			
			var myPattern:RegExp = /&/g;
			
			
			switch(fileType){
				case "wsdl": 
					fileNameTemp = fileNameTemp+".wsdl&";
					fileContent = fileContent + WSDLCreator.getInstance().WorkflowWSDL.toString();		
					
					myPattern = /&/g;		
					fileContent = fileContent.replace(myPattern,"%26");	
					
					myPattern = /+/g;
					fileContent = fileContent.replace(myPattern,"%2B");			
					
					while(fileContent.indexOf("&") > -1){						
						//trace("fileContent.indexOf(&): " + fileContent.indexOf("&"));						
						fileContent = fileContent.replace("&","%26");
					}
										
					while(fileContent.indexOf("+") > -1){						
						//trace("fileContent.indexOf(+): " + fileContent.indexOf("+"));						
						fileContent = fileContent.replace("+","%2B");
					}
					
					var WSDLLoader:URLLoader = new URLLoader();
					var tempVariables2:URLVariables = new URLVariables(workflowName + operationString + fullPath + fileNameTemp + fileContent);
					WSDLLoader.addEventListener(Event.COMPLETE, completeWSDLHandler);
					tempURLRequest.data = tempVariables2;
					WSDLLoader.load(tempURLRequest);
					break;
					
				case "bpel":
					fileNameTemp = fileNameTemp + ".bpel&";
					
					fileContent = fileContent + ProcessCreator.getInstance().BPELProcess.toString();	
					//fileContent = fileContent.replace(" ","&#13;&#10;");							
					
					/*
					// original Regular Expression not working
					myPattern = /&/g;		
					fileContent = fileContent.replace(myPattern,"%26");
					*/
					while(fileContent.indexOf("&") > -1){						
						//trace("fileContent.indexOf(&): " + fileContent.indexOf("&"));						
						fileContent = fileContent.replace("&","%26");
					}
										
					while(fileContent.indexOf("+") > -1){						
						trace("fileContent.indexOf(+): " + fileContent.indexOf("+"));						
						fileContent = fileContent.replace("+","%2B");
					}
					
					//trace(fileContent);
					
					var tempBPELLoader:URLLoader = new URLLoader();
					var tempBPELVariables:URLVariables = new URLVariables(workflowName + operationString + fullPath + fileNameTemp + fileContent);	
					//trace(tempBPELVariables);
					tempBPELLoader.addEventListener(Event.COMPLETE, completeBPELHandler);							
					tempURLRequest.data = tempBPELVariables;					
					tempBPELLoader.load(tempURLRequest);			
					
					break;
					
				case "QoS":
					fileNameTemp = fileNameTemp+".qos&";
					
					// To BE Done
					//fileContent = fileContent + QOS
					
					/*
					myPattern = /&/g;
					fileContent = fileContent.replace(myPattern,"%26");
					*/
					
					while(fileContent.indexOf("&") > -1){						
						//trace("fileContent.indexOf(&): " + fileContent.indexOf("&"));						
						fileContent = fileContent.replace("&","%26");
					}
					myPattern = /+/g;
					fileContent = fileContent.replace(myPattern,"%2B");				
					
					break;
				
				case "pdd":
					fileNameTemp = fileNameTemp + ".pdd&";
					
					if(PDDCreator.getInstance().WorkflowPDD){
						
						fileContent = fileContent + PDDCreator.getInstance().WorkflowPDD.toString();
						
						myPattern = /&/g;
						fileContent = fileContent.replace(myPattern,"%26");
						
						myPattern = /+/g;
						fileContent = fileContent.replace(myPattern,"%2B");
						
						while(fileContent.indexOf("&") > -1){						
							//trace("fileContent.indexOf(&): " + fileContent.indexOf("&"));						
							fileContent = fileContent.replace("&","%26");
						}
											
						while(fileContent.indexOf("+") > -1){						
							//trace("fileContent.indexOf(+): " + fileContent.indexOf("+"));						
							fileContent = fileContent.replace("+","%2B");
						}
					
						var tempPDDLoader:URLLoader = new URLLoader();
						var tempPDDVariables:URLVariables = new URLVariables(workflowName + operationString + fullPath + fileNameTemp + fileContent);	
						tempPDDLoader.addEventListener(Event.COMPLETE, completePDDHandler);				
						tempURLRequest.data = tempPDDVariables;					
						tempPDDLoader.load(tempURLRequest);
					}					
					
					break;
					
				case "catalog":
					fileNameTemp = "fileName=wsdlCatalog.xml&";
					
					if(WSDLCatalogCreator.getInstance().WSDLCatalog){
						fileContent = fileContent + WSDLCatalogCreator.getInstance().WSDLCatalog.toString();						
						
						myPattern = /&/g;
						fileContent = fileContent.replace(myPattern,"%26");
						
						myPattern = /+/g;
						fileContent = fileContent.replace(myPattern,"%2B");
						
						while(fileContent.indexOf("&") > -1){						
							//trace("fileContent.indexOf(&): " + fileContent.indexOf("&"));						
							fileContent = fileContent.replace("&","%26");
						}
											
						while(fileContent.indexOf("+") > -1){						
							//trace("fileContent.indexOf(+): " + fileContent.indexOf("+"));						
							fileContent = fileContent.replace("+","%2B");
						}
						// WC stands for WSDL Catalog						
						var tempWCLoader:URLLoader = new URLLoader();
						var tempWCVariables:URLVariables = new URLVariables(workflowName + operationString + fullPath + fileNameTemp + fileContent);	
						tempWCLoader.addEventListener(Event.COMPLETE, completeWCHandler);				
						tempURLRequest.data = tempWCVariables;					
						tempWCLoader.load(tempURLRequest);	
					}
										
					break;
					
				case "subDoc":
					fileNameTemp = fileNameTemp + "-wfms.xml&";
					
					if(SubmissionDocumentCreator.getInstance().SubmissionDocument){
						fileContent = fileContent + SubmissionDocumentCreator.getInstance().SubmissionDocument.toString();
						
						myPattern = /&/g;
						fileContent = fileContent.replace(myPattern,"%26");
						
						myPattern = /+/g;
						fileContent = fileContent.replace(myPattern,"%2B");
						
						while(fileContent.indexOf("&") > -1){						
							//trace("fileContent.indexOf(&): " + fileContent.indexOf("&"));						
							fileContent = fileContent.replace("&","%26");
						}
											
						while(fileContent.indexOf("+") > -1){						
							//trace("fileContent.indexOf(+): " + fileContent.indexOf("+"));						
							fileContent = fileContent.replace("+","%2B");
						}
						//trace (fileContent)
						// SD stands for Submission Document						
						var tempSDLoader:URLLoader = new URLLoader();
						var tempSDVariables:URLVariables = new URLVariables(workflowName + operationString + fullPath + fileNameTemp + fileContent);	
						tempSDLoader.addEventListener(Event.COMPLETE, completeSDHandler);				
						tempURLRequest.data = tempSDVariables;					
						tempSDLoader.load(tempURLRequest);	
					}
					
					break;
			}			
		}
		
		private function completeWSDLHandler(event:Event):void{
			//trace("WSDL: " + event.target.data);
			saveWorkflowOnServer("bpel");
		}
		
		private function completeBPELHandler(event:Event):void{
			//trace("BPEL: " + event.target.data);
			saveWorkflowOnServer("pdd");
		}		
		
		private function completePDDHandler(event:Event):void{
			//trace("PDD: " + event.target.data);
			saveWorkflowOnServer("catalog");
		}
		
		private function completeWCHandler(event:Event):void{
			//trace("WSDL Catalog: " + event.target.data);
			saveWorkflowOnServer("subDoc");
		}
		
		private function completeSDHandler(event:Event):void{
			//saveWorkflowOnServer("catalog");
		}
	}
}