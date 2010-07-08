package  bpel.editor.gridcc.view
{
	
	import mx.containers.*;
	
	import mx.controls.Button;
	import mx.controls.Label;
	import mx.controls.TextInput;
	
	import flash.events.MouseEvent;
	
	import bpel.editor.gridcc.data.*;
	import bpel.editor.gridcc.controller.*;
	import bpel.editor.gridcc.utility.*;
	
	public class PropertyPanel extends Canvas
	{
		/*
		[ArrayElementType("mx.controls.Label")]
		private var labelArray:Array = new Array;
		
		[ArrayElementType("mx.controls.TextInput")]
		private var textInputArray:Array = new Array;
		
		private var _tempAttributesArray:Array;
		private var tempNamespaceArray:Array;
		
		
		public function get tempAttributesArray():Array{
			return _tempAttributesArray;
		}
		
		private function set tempAttributesArray(tempArrayValue:Array):void{
			_tempAttributesArray = tempArrayValue;
		}
		private var OKButton:Button = new Button();
		private var _OKButtonClicked:Boolean = false;
		
		private var _structuredActivity:StructuredActivity;
		
		public function get OKButtonClicked():Boolean{
			return _OKButtonClicked;
		}
				
		private var tempLabel:Label;
		private var tempTextInput:TextInput;
		
		private var XCoordinate:Number = 20;
		private var YCoordinate:Number = 10;
		
		private var isProcess:Boolean = false;
		private var activityType:String;
		
		public function PropertyPanel(dummyValue:String){
						
		}
		
		public function set structuredActivity(structuredActivityValue:StructuredActivity):void{
			this._structuredActivity = structuredActivityValue;
		}
		
		public function activitySelector(activityType:String):void {
			trace("Activity Selector" + this.height + "  " + this.width)
			this.activityType = activityType;
			switch(activityType) {
					    
				case WorkflowActivities.PROCESS:
					trace("process");
					var process:gridcc.ic.ac.uk.data.Process = new gridcc.ic.ac.uk.data.Process();
					isProcess = !isProcess;
					this._tempAttributesArray = process.attributesArray;
					this.tempNamespaceArray = process.namespaceArray;
					fillPanel();         
					break;
				
				case WorkflowActivities.PARTNERLINK:
					trace("partenrLink");
					var partnerLink:gridcc.ic.ac.uk.data.PartnerLink = new gridcc.ic.ac.uk.data.PartnerLink();
					
					this._tempAttributesArray = partnerLink.attributesArray;
					fillPanel();         
					break;
				
				case WorkflowActivities.VARIABLE:
					trace("variable");
					var variable:gridcc.ic.ac.uk.data.Variable = new gridcc.ic.ac.uk.data.Variable();
					
					this._tempAttributesArray = variable.attributesArray;
					fillPanel();         
					break;
					
				case WorkflowActivities.INVOKE:
					trace("variable");
					var invoke:gridcc.ic.ac.uk.data.Invoke = new gridcc.ic.ac.uk.data.Invoke();
					
					this._tempAttributesArray = invoke.attributesArray;
					fillPanel();         
					break;
				
				case WorkflowActivities.REPLY:
					trace("variable");
					var reply:gridcc.ic.ac.uk.data.Reply = new gridcc.ic.ac.uk.data.Reply();
					
					this._tempAttributesArray = reply.attributesArray;
					fillPanel();         
					break;
					
				case WorkflowActivities.RECEIVE:
					trace("variable");
					var receive:gridcc.ic.ac.uk.data.Receive = new gridcc.ic.ac.uk.data.Receive();
					
					this._tempAttributesArray = receive.attributesArray;
					fillPanel();         
					break;
					
				case WorkflowActivities.SEQUENCE:
					trace("sequence");
					var sequence:gridcc.ic.ac.uk.data.Sequence = new gridcc.ic.ac.uk.data.Sequence();
					
					this._tempAttributesArray = sequence.attributesArray;
					fillPanel();         
					break;
			}
		}
		
		private function fillPanel():void{
			for(var i:int=0; i < _tempAttributesArray.length; i++){
				if(i % 2 == 0){
					tempLabel = new Label();
					tempLabel.name = _tempAttributesArray[i][0];
					tempLabel.text = _tempAttributesArray[i][0];
					tempLabel.id = _tempAttributesArray[i][0];
					tempLabel.x = 10;
					tempLabel.y = YCoordinate;
					//tempLabel.width = 190;
					tempLabel.height = 25;
					tempLabel.percentWidth = 25;
					tempLabel.setStyle("color", "#CD69C9");
					tempLabel.setStyle("fontSize", 12);
					if(_tempAttributesArray[i][2] == "mandatory"){
						tempLabel.setStyle("fontWeight","bold");
					}
					addChild(tempLabel);
					
					tempTextInput = new TextInput();
					tempTextInput.name = _tempAttributesArray[i][0];
					tempTextInput.id = _tempAttributesArray[i][0];
					tempTextInput.x = 220;
					tempTextInput.y = YCoordinate;
					tempTextInput.width = 190;
					tempTextInput.height = 25;
					textInputArray.push(tempTextInput);
					addChild(tempTextInput);
				} else{
					tempLabel = new Label();
					tempLabel.name = _tempAttributesArray[i][0];
					tempLabel.text = _tempAttributesArray[i][0];
					tempLabel.id = _tempAttributesArray[i][0];
					tempLabel.x = 440;
					tempLabel.y = YCoordinate;
					//tempLabel.width = 190;
					tempLabel.height = 25;
					tempLabel.percentWidth = 25;
					tempLabel.setStyle("color", "#CD69C9");
					tempLabel.setStyle("fontSize", 12);
					if(_tempAttributesArray[i][2] == "mandatory"){
						tempLabel.setStyle("fontWeight","bold");
					}
					addChild(tempLabel);
					
					tempTextInput = new TextInput();
					tempTextInput.name = _tempAttributesArray[i][0];
					tempTextInput.id = _tempAttributesArray[i][0];
					tempTextInput.x = 650;
					tempTextInput.y = YCoordinate;
					tempTextInput.width = 190;
					tempTextInput.height = 25;
					textInputArray.push(tempTextInput);
					addChild(tempTextInput);
					
					YCoordinate = YCoordinate + 30;
				}							
			}
			
			if(_tempAttributesArray.length % 2 != 0){
				YCoordinate = YCoordinate + 30;
			}
			
			OKButton.x = 20;
			OKButton.y = YCoordinate;
			OKButton.label = "OK";
			OKButton.addEventListener(MouseEvent.CLICK, OKButtonClick);
			if(this.activityType != "process"){
				OKButton.addEventListener(MouseEvent.CLICK, _structuredActivity.OKClicked);
				_structuredActivity.setLocalPropertyPanel(this);
			}
			addChild(OKButton);
		}
		
		public function OKButtonClick(event:MouseEvent):void {
			if(validateForm()){
			//trace("OKButtonClicked");
				for(var i:int = 0; i < textInputArray.length; i++){
					//trace(TextInput(textInputArray[i]).text);
					if(TextInput(textInputArray[i]).text != ""){
						//trace("It is not empty");
						_tempAttributesArray[i][1] = TextInput(textInputArray[i]).text;
					}
				}
				//partnerLink.printArray();			
				//PopUpManager.removePopUp(this);
				_OKButtonClicked = !_OKButtonClicked;
				
				this.parent.removeChildAt(0);
				
				
				trace(this.activityType);
				if(this.activityType == "process"){
					var bpel:BPELCreator = new BPELCreator();
					bpel.addProcessNamespaces(tempNamespaceArray);
					bpel.addProcessAttributes(_tempAttributesArray);
					var componentPanel:ComponenetPanels = new ComponenetPanels();
					componentPanel.getBPELActivitiesPanel().enabled = true;		
				}
			}
		}
		
		public function getNameValue():String{
			var nameValue:String;
			for(var i:int = 0; i < textInputArray.length; i++){
				if(TextInput(textInputArray[i]).id == "name"){
					if(TextInput(textInputArray[i]).text == ""){
						//trace(TextInput(textInputArray[i]).text);
						TextInput(textInputArray[i]).text = activityType + Math.random();
						//nameValue = TextInput(textInputArray[i]).text;
					}
					nameValue = TextInput(textInputArray[i]).text;										
					break;
				}				
			}
			return nameValue;
		}
		
		public function validateForm():Boolean{
			var validate:Boolean = true;
			for(var i:int = 0; i < _tempAttributesArray.length; i++){
				trace(_tempAttributesArray[i][0] + "  " + _tempAttributesArray[i][2]);
				if(_tempAttributesArray[i][2] == "mandatory"){
					if(TextInput(textInputArray[i]).text == ""){
						validate = false;
						break;
					}					
				}
			}
			//trace(validate);
			return validate;
		}		
		*/
	}
}