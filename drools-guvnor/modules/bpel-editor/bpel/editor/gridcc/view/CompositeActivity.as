package bpel.editor.gridcc.view {
	
	import mx.core.UIComponent;
	import mx.core.Container;
	
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.FocusEvent;
	
	import mx.controls.Label;
	import mx.containers.Canvas; 
	
	import mx.events.DragEvent;  	        
    import mx.managers.DragManager;
    import mx.core.DragSource;
    
   
    import bpel.editor.gridcc.controller.WorkflowManager;
   
	
	public class CompositeActivity extends Activity {	
		
		private var _collapsedSize:Number;
		
		[Bindable("expandedSizeChangedEvent")]
		private var _expandedSize:Number;		
		
		[Bindable("expandedOrCollapsedEvent")]
		private var _expanded:Boolean = true;
		
		private var _dropable:Boolean; 			
		
		//private var expandCollapseLabel:Label;
		
		//private var titleLabel:Label;
		
		// Used for Structured Activity Only
		public var activityContainer:Canvas;
		
		// This boolean is to stop parent to accept the actvity event
		protected var acceptDrag:Boolean = false;
		
		protected var backgroundColour:String = "#ECC8EC";
		
		/* -------------- Sub Activities Array --------------------*/
		// It should be in the Structured Activities ONLY
		[ArrayElementType("bpel.editor.gridcc.view.Activity")]
		protected var _subActivitiesArray:Array = new Array;	
		
		public function get subActivitiesArray():Array {
			return _subActivitiesArray;
		}
		
		public function set subActivitiesArray(subActivitiesArrayValue:Array):void {
			_subActivitiesArray = subActivitiesArrayValue;
		}
		
		public function addNewActivity(/*newActivityType:String,*/ newActivity:UIComponent):void {
			_subActivitiesArray.push( newActivity);
			//trace(this.name + "  **  "+ newActivity.name)
			activityContainer.addChild(newActivity);
		}
		/* -----------------------------------------------------------------------*/
		private var dropedActivityValue:String;
		private var target:UIComponent;
		
		public function set dropable(value:Boolean):void {
			_dropable = value;			
		}
		
		public function get dropable():Boolean {
			return _dropable;
		}
		
		public function set expanded(value:Boolean):void {
			_expanded = value;
		}
		
		[Bindable("expandedOrCollapsedEvent")]
		public function get expanded():Boolean {
			return expanded;
		}
		
		public function CompositeActivity(parentValue:UIComponent, name:String, type:String){
			super(parentValue, name, type);
			super.dragable = true;
			super.width = 300;
			super.height = 60;
			createChildren();		
			
			//addEventListener(DragEvent.DRAG_ENTER, dragEnterHandler);
			//addEventListener(DragEvent.DRAG_EXIT,dragExitHandler);
			//addEventListener(DragEvent.DRAG_DROP,dragDropHandler);										
		}
		
		/*
		public function configureEventListeners():void {
			this.addEventListener(DragEvent.DRAG_ENTER, dragEnterHandler);
			this.addEventListener(DragEvent.DRAG_EXIT,dragExitHandler);
			this.addEventListener(DragEvent.DRAG_DROP,dragDropHandler);		
			this.addEventListener(FocusEvent.FOCUS_IN, focusInEventHandler);
			this.addEventListener(FocusEvent.FOCUS_OUT, focusOutEventHandler);
		}
		*/
		override protected function createChildren():void {
			super.createChildren();
			//trace("CompositeActivity.createChildren " + super.type);
			
			createActivityContainer();
			//createExpandCollapseLabel();
			//createTitleLabel();	
			//createAxtivityContainer();		
		}		
		
		private function createActivityContainer():void {
			if(!activityContainer){
				activityContainer = new Canvas();
				
				// Only to give reasonable name to activity container
				//activityContainer.name = super.activityType;			
				
				activityContainer.width = this.width - 10;
				activityContainer.height = this.height - 40;
				
				activityContainer.y = 35;
				//activityContainer.x = (this.width - activityContainer.width)/2;
				activityContainer.x = 5;
				
				activityContainer.setStyle("borderStyle", "solid");
				//activityContainer.setStyle("backgroundColor","#C6C3BA");
				
				activityContainer.addEventListener(DragEvent.DRAG_ENTER, dragEnterHandler);
				activityContainer.addEventListener(DragEvent.DRAG_EXIT,dragExitHandler);
				activityContainer.addEventListener(DragEvent.DRAG_DROP,dragDropHandler);
				//activityContainer.addEventListener(FocusEvent.FOCUS_IN,onFocus);					
				
				this.addChild(activityContainer);
			}
		}			
		
		override protected function commitProperties():void {
			super.commitProperties();			
		}
		
		override protected function measure():void {
			super.measure();				
		}
		
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
			super.updateDisplayList(unscaledWidth, unscaledHeight);								
		}
		
		private function calculateMinWidth():Number {
			if(subActivitiesArray.length == 0){
				
			}
			return 0;
		}
		
		private function calculateMinHeight():Number {
			if(subActivitiesArray.length == 0){
				
			}
			return 0;
		}
		
		private function dragEnterHandler(event:DragEvent):void {
			//trace("CompositeActivity.dragEnterHandler "+ super.activityType);
            // Get the drop target component from the event object.
            var dropTarget:Container = event.currentTarget as Container;              
            DragManager.acceptDragDrop(dropTarget);
            //this.setStyle("backgroundColor", "#FFE1FF");
            dropTarget.setStyle("backgroundColor", "#C9D1DD");
            //backgroundColor="#C0C0C0"
        }
        
        // Called if the user drags the drag proxy away from the drop target.
        private function dragExitHandler(event:DragEvent):void {        	
        	//trace("CompositeActivity.dragExitHandler " + super.activityType);
           	revertFocus(event.currentTarget as Canvas);                
       	}                    
            
        private function revertFocus(dropTarget:Canvas):void {
            dropTarget.setStyle("borderStyle", "solid");
            dropTarget.setStyle("backgroundColor", "#e0e0e0");
        }
        
       	private function dragDropHandler(event:DragEvent):void {
			//trace("CompositeActivity.dragDropHandler " + super.activityType);
			//event.stopPropagation();
			//trace("Y Position of Drag Drop: " + event.localY);
            // Get the data identified by the color format from the drag source.
            dropedActivityValue = event.dragSource.dataForFormat("activity") as String;     
            //target =  UIComponent(event.currentTarget);          
			//trace("Target Value " + dropedActivityValue + " Target Parent.type " + target.parent.name);                
             
            var workflowManager:WorkflowManager = WorkflowManager.getInstance();
            workflowManager.dragDropped(super.activityType, dropedActivityValue, event); 
                        
            revertFocus(event.currentTarget as Canvas);                                   
        }
        
        private function focusInEventHandler(event:FocusEvent):void{
        	 this.setStyle("backgroundColor", "#FFE1FF");
        }
        
        private function focusOutEventHandler(event:FocusEvent):void{
        	//revertFocus(this);
        }
        
        public function resetWidth(widthValue:int):void {
        	this.width = widthValue;
        	/*
        	activityHeading.width = this.width - 10;
        	*/
        	activityButton.width = this.width - 10;
        	activityContainer.width = this.width - 10;
        	resetXCoordinateOfChildActivities();
        	//trace(super.activityType + " " + activityContainer.height + "  " + activityContainer.width);
        	//drawBorders();
        }
        
        public function resetHeight(heightValue:int):void {
        	this.height = heightValue;        	
        	activityContainer.height = this.height - 40;
        	//trace(super.activityType + " " + activityContainer.height + "  " + activityContainer.width);
        	//drawBorders();
        }
        
        private function drawBorders():void {
			graphics.clear();
			graphics.lineStyle(1, 0X000000, 1.0);
			//graphics.drawRect(0,0, this.width,this.height);
			graphics.drawRect(5,5, activityContainer.width, activityContainer.height);
		}
		
		private function resetXCoordinateOfChildActivities():void {
			var childXCoordiate:int = 0;
			for (var index:int = 0; index < subActivitiesArray.length; index++){
				
				childXCoordiate = (this.width - UIComponent(subActivitiesArray[index]).width)/2;
				
				UIComponent (subActivitiesArray[index]).x = childXCoordiate
			}
		}
	}
}