package bpel.editor.gridcc.view
{
	import mx.core.UIComponent;
	import mx.containers.Canvas;
	import mx.containers.VBox;
	import mx.containers.FormHeading;
	import mx.controls.Button;
	
	//import mx.controls.Label;
	//import mx.controls.Button;
	
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.FocusEvent;
	           
    //import mx.managers.DragManager;
    //import mx.core.DragSource;
	
	public class Activity extends UIComponent {
		
		[Bindable("parentChangedEvent")]
		private var _parentActivity:UIComponent;
		
		private var _activityType:String;
		private var _activityName:String;
		
		private var _dragable:Boolean;	
		
		//public var activityHeading:FormHeading;	
		public var activityButton:Button;
		
		//private var _startEndGap:Number;
		
		//protected var _preferedWidth:Number;
		//protected var _preferedHeight:Number;
		
		[Bindable("yPositionChangedEvent")]
		private var _yPosition:Number;
		
		[Bindable]		
		public function get parentActivity():UIComponent {
			return _parentActivity;
		}
		
		public function set parentActivity(parentValue:UIComponent):void {
			_parentActivity = parentValue;
		}
		
		[Bindable]
		public function get activityType():String{
			return _activityType;
		}		
		
		public function set activityType(value:String):void{
			_activityType = value;
			//activityHeading.label = this.activityType + " > " + name;
			activityButton.label = this.activityType + " > " + name;
		}
		
		[Bindable]
		public function get activityName():String{
			return _activityName;
		}		
		
		
		public function set activityName(value:String):void{
			super.name = value;
			_activityName = value;
			//activityHeading.label = this.activityType + " > " + name;
			activityButton.label = this.activityType + " > " + name;
		}
		
		public function set dragable(value:Boolean):void {
			_dragable = value;			
		}
		
		public function get dragable():Boolean {
			return _dragable;
		}
		
		/*
		public function set preferedWidth(value:Number):void {
			_preferedWidth = value;
		}
		
		public function get preferedWidth():Number {
			return _preferedWidth;
		}
		
		public function set preferedHeight(value:Number):void {
			_preferedWidth = value;
		}
		
		public function get preferedHeight():Number {
			return _preferedWidth;
		}
		
		public function set startEndGap(value:Number):void {
			_startEndGap = value;
		}
		*/
		
		[Bindable("yPositionChangedEvent")]
		public function get yPosition():Number {
			return _yPosition;
		}
		
		public function set yPosition(value:Number):void {
			_yPosition = value;
			dispatchEvent(new Event("yPositionChangedEvent"));
		}
		
		public function Activity(parentValue:UIComponent, name:String, type:String){
			super();
			//_parentActivity = parentValue;
			super.name = name;
			_activityName = name;
			_activityType = type;
			
			//createChildren();
			
			//setStyle("backgroundColour","#FFBBFF");
			//parentActivity = parentValue;
			//startEndGap = 5;			
				
			// Add this listeners if event is dragable .. which means in actual activity
			//this.addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler, false);
			//this.addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);		
			this.addEventListener(FocusEvent.FOCUS_IN, myFocusIn);	
			this.addEventListener(FocusEvent.FOCUS_OUT, myFocusOut);			
		}
		/*
		override protected function createChildren():void {
			super.createChildren();				
						
			if(!activityHeading){				
				activityHeading = new FormHeading();
				sizeContainer();
				activityHeading.label = this.activityType + " > " + name;
				
				// Add Action Listener in sub class if required				
				//this.addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler);					
				
				activityHeading.setStyle("backgroundColor","blue");
				activityHeading.setStyle("color","red");
				
				// Not sure where it will be added
				this.addChild(activityHeading);							
			}					
		}
		
		private function sizeContainer():void {
				
			
			activityHeading.explicitHeight = 25;
			activityHeading.explicitWidth = this.width - 10;
				
			activityHeading.maxHeight = 25;
			activityHeading.maxWidth = this.width - 10;
				
			activityHeading.height = 25;
			activityHeading.width = this.width - 10;				
		}
		*/
		
		override protected function createChildren():void {
			super.createChildren();				
						
			if(!activityButton){				
				activityButton = new Button();
				sizeContainer();
				activityButton.label = this.activityType + " > " + name;
				
				// Add Action Listener in sub class if required				
				//this.addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler);					
				
				//activityButton.setStyle("borderColor","blue");
				//activityButton.setStyle("color","blue");
				activityButton.setStyle("fontWeight","bold");
				activityButton.setStyle("fontSize","13");
				activityButton.setStyle("textAlign","center");

				
				// Not sure where it will be added
				this.addChild(activityButton);							
			}					
		}
		
		private function sizeContainer():void {
				
			
			activityButton.explicitHeight = 25;
			activityButton.explicitWidth = this.width - 10;
				
			activityButton.maxHeight = 25;
			activityButton.maxWidth = this.width - 10;
				
			activityButton.height = 25;
			activityButton.width = this.width - 10;				
		}
		
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			//trace("CustomContainer.updateDisplayList " + name);
			/*
			activityHeading.x = 10
			activityHeading.y = 10	
			*/		
			activityButton.x = 5
			activityButton.y = 5					
			drawBorder();
		}
		
		private function drawBorder():void {
			graphics.clear();
			graphics.lineStyle(1, 0X000000, 1.0);
			graphics.drawRect(0,0, this.width,this.height);
			//graphics.drawRect(5,5, this.activityHeading.width,this.activityHeading.height);
			// Border around Button is horrible
			//graphics.drawRect(5,5, this.activityButton.width,this.activityButton.height);
		}
		
		/*
		public function configureEventListener():void{
			// Add this listeners if event is dragable .. which means in actual activity
			this.addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler, false);
			//this.addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);		
			//this.addEventListener(FocusEvent.FOCUS_IN, myFocusIn);	
			//this.addEventListener(FocusEvent.FOCUS_OUT, myFocusOut);	
		}
		*/
		private function mouseMoveHandler(event:MouseEvent):void {
						
		}					
		        
        private function mouseUpHandler(event:MouseEvent):void {
        						
        }
        
        public function mouseDownHandler(event:MouseEvent):void {
        	event.stopPropagation();
        	//trace("_activityType: " + _activityType + " " +event.localX + "  " + event.localY)        	
        }  
        
        private function myFocusIn(event:FocusEvent):void {
        	trace("Focus In " + activityType + "  " + activityName);
        }   
        private function myFocusOut(event:FocusEvent):void {
        	//trace("Focus Out " + activityType + "  " + activityName);
        } 
	}
}