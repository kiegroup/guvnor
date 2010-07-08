package bpel.editor.gridcc.view	
{
	import mx.core.UIComponent;
	
	import mx.core.Container;
	import mx.containers.Panel;
	import mx.containers.Canvas;
	import mx.controls.Button;
	
	import bpel.editor.gridcc.constant.WorkflowActivities;
	import bpel.editor.gridcc.data.*;
	import mx.core.Application;
	
	public class WorkflowParser
	{	
		/**
         * The Singleton instance of WorkflowParser
         */        
        protected static var instance:WorkflowParser;
        //private var rootParent:Canvas;	
        private var rootParent:Canvas; 
        
        private var tempContainer:Process;
        
		/**
         * Constructor
         * 
         * Instantiates Singleton instance of WorkflowParser
         */        
        public function WorkflowParser()
        {
            if (WorkflowParser.instance == null) 
            {                
                WorkflowParser.instance = this; 
                
            }            
        }
        
        /**
         * Determines if the singleton instance of WorkflowParser
         * has been instantiated, if not an instance is instantiated
         * and returned in subsequent calls to getInstance();
         * 
         * @return Singleton instance of WorkflowParser
         */        
        public static function getInstance():WorkflowParser
        {
            if (instance == null) 
            {
               instance = new WorkflowParser();   
            }            
            return instance;
        }
        
        public function parseWorkflow(workflowInstance:XML, widthValue:Number):CompositeActivity {  	
        	        	        	
        	var attNamesList:XMLList = workflowInstance.@*;
        	
        	var ns:Array = workflowInstance.namespaceDeclarations();
        	
        	var tempProcess:Process = createProcessVisualObject(attNamesList, ns);
        	
			parseElements(workflowInstance, tempProcess);			
			
			return tempProcess;
		}		
			
		private function parseElements(node:XML, parent:CompositeActivity):void{		
			
			var verticalGap:Number = 10;			
			var tempComponentYPosition:Number = verticalGap;
			var tempActivityType:String;
			
			for each( var element:XML in node.elements() ){								
				//trace(element.children().length() + "  " + element.@name + "  " + element.localName());
							
				if(element.children().length() > 0){
					var tempContainer:CompositeActivity;
					switch(element.localName()){
												
						case WorkflowActivities.PARTNERLINKS:
							trace(WorkflowActivities.PARTNERLINKS);
							
							tempContainer = new PartnerLinks(parent, element.localName(), element.localName(), null);
							tempActivityType = WorkflowActivities.PARTNERLINKS;
							break;
						
						case WorkflowActivities.VARIABLES:
							
							trace(WorkflowActivities.VARIABLES);
							
							tempContainer = new Variables(parent, element.localName(), element.localName(), null);
							tempContainer.setStyle("backgroundColour","#FFBBFF");
							tempActivityType = WorkflowActivities.VARIABLES;
							break;
						
						case WorkflowActivities.SEQUENCE:
							trace(WorkflowActivities.SEQUENCE);
							
							tempContainer = new Sequence(parent, element.@name, element.localName(), null);
							tempActivityType = WorkflowActivities.SEQUENCE;
							break;
					}
					
					// Starting width for any activity 
					// endups for the innermost composite activity
					tempContainer.resetWidth(300);	
										
					
					// Temp Container's Y position
					tempContainer.y = tempComponentYPosition ;					
					
					//trace("tempComponentYPosition: "  + element.localName() +" : " + tempComponentYPosition)			
					parseElements(element, tempContainer);
					
					// set Y position for next component
					tempComponentYPosition = tempContainer.height + tempComponentYPosition + verticalGap;					
					//trace("tempComponentYPosition: " + element.localName() +": " + tempComponentYPosition);
					
					// If not direct child of root Process
					//if(parent != null){											
						
						// set the height of the parent activity
						
						//parent.height = tempComponentYPosition + verticalGap;
						parent.resetHeight(tempComponentYPosition + verticalGap + 45);
						
						// Increment the Width of the Parent if required
						if(parent.width <= tempContainer.width)
							//parent.width = tempContainer.width + 20;
							parent.resetWidth(tempContainer.width + 80);
						
						// set the X poisition of current activity in the middle
						tempContainer.x = ((parent.width - tempContainer.width)/2);
						
						parent.addNewActivity(tempContainer);	
					//}
					//tempContainer.setStyle("borderStyle","solid");
					
				}else {
					if(element.localName() == "variable"){
						var tempVariable:Variable = new Variable(parent, null, tempComponentYPosition);
						parent.addNewActivity(tempVariable);
					} 
					else if (element.localName() == "partnerLink"){
						var tempPartnerLink:PartnerLink = new PartnerLink(parent, null, tempComponentYPosition);
						parent.addNewActivity(tempPartnerLink);
					}
					else if (element.localName() == "reply"){
						var tempReply:Reply = new Reply(parent, null, tempComponentYPosition);
						parent.addNewActivity(tempReply);
					}
					else if (element.localName() == "receive"){
						var tempReceive:Receive = new Receive(parent, null, tempComponentYPosition);
						parent.addNewActivity(tempReceive);
					}
					else{
					var tempButton:Button = new Button();					
					tempButton.label = element.localName();
					tempButton.name = element.@name;
					tempButton.height = 75;
					tempButton.y = tempComponentYPosition;
					
					tempButton.width = 85;
					tempButton.x = (parent.width - tempButton.width)/2;
					parent.addNewActivity(tempButton);
					}				
					// set Y position for next component
					tempComponentYPosition = tempComponentYPosition + 75 + verticalGap
					//trace("tempComponentYPosition: " + element.localName() +": " + tempComponentYPosition);
											
					//parent.height = tempComponentYPosition + verticalGap;
					parent.resetHeight(tempComponentYPosition + verticalGap + 45);
					//parent.resetHeight(parent.activityContainer.height + verticalGap);
					 		
				}
			}			
		}
		
		public function computeAndAddChild(parentType:String, parentName:String, childType:String, childName:String):void{
			switch(childType){
				
				case WorkflowActivities.VARIABLE:
					var tempButton:Button = new Button();
					tempButton.label = childName;
					tempButton.name = childName;
					tempButton.height = 75;
					tempButton.width = 85;
					tempButton.y = getVariablesView().height - 50;
					tempButton.x = (getVariablesView().width - tempButton.width)/2;
					getVariablesView().height = getVariablesView().height + tempButton.height + 10;
					getVariablesView().activityContainer.height = getVariablesView().activityContainer.height + tempButton.height + 10;
					getVariablesView().addNewActivity(tempButton);
					break;
					
				default: 
					trace ("Not yet implemented");					
				
			}
			redrawActivities();
		}
		
		
		private function createProcessVisualObject(attributesList:XMLList, nsArray:Array):Process{
			var processDO:ProcessDO = ProcessDO.getInstance();
			var porceessName:String = processDO.populateAttributes(attributesList);
			processDO.populateNS(nsArray);
			//if()
			tempContainer = new Process(null, porceessName, "process", processDO);			
			return tempContainer;
		}
		
		private function getVariablesView():Variables {
			var tempArray:Array = tempContainer.subActivitiesArray;
			var tempVariablesView:Variables;
			
			trace("tempArray[1]: " + tempArray[1]);
			if(tempArray[1]is Variables){
				trace ("I have found Variables")
				tempVariablesView = Variables(tempArray[1]);
			}
			return tempVariablesView;
		}
		
		private function redrawActivities():void {
			var tempprocessCanvas:Canvas = tempContainer.activityContainer;
			var newYCordinate:int = 10;
			for (var index:int=0; index < tempprocessCanvas.numChildren; index++){
				tempprocessCanvas.getChildAt(index).y = newYCordinate;
				newYCordinate = newYCordinate + tempprocessCanvas.getChildAt(index).height + 20;				
			}
			findActivity(tempContainer, "Asif");
		}
		/*
		private function destroyProcessView(tempParentContainer:CompositeActivity):void{
			if(tempParentContainer){
				var tempCanvas:Canvas = tempParentContainer.activityContainer;
				
				for(var index:int = tempCanvas.numChildren -1; index > 0; index--){
					if(tempCanvas.getChildAt(index) is CompositeActivity){
						destroyProcessView(CompositeActivity(tempCanvas.getChildAt(index)));
					}
					tempCanvas.getChildAt(index) = null;
				}
			}
			
		}*/
		
		private function findActivity(parentActivity:CompositeActivity, activityName:String):void {
			
			var tempprocessCanvas:Canvas = parentActivity.activityContainer;
			var indention:String = " ";
			for (var index:int=0; index < tempprocessCanvas.numChildren; index++){				
				trace(indention + tempprocessCanvas.getChildAt(index).name);
				if(tempprocessCanvas.getChildAt(index) is CompositeActivity){
					findActivity(CompositeActivity(tempprocessCanvas.getChildAt(index)), activityName);
				}				
				indention = indention + "  ";				
			}
		}
	}
	
}