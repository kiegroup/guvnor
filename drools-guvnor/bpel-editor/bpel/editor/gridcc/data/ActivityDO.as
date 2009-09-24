package bpel.editor.gridcc.data
{
	public class ActivityDO
	{
		[Bindable]
		public var name:String;// =  "undefined";
		public var joinCondition:Boolean =  false;
		public var suppressJoinFailure:Boolean = false;		
		
		protected var _attributesArray:Array;
		
		public function ActivityDO(){						
			_attributesArray = new Array();
			_attributesArray.push(["name", name, "mandatory"]);
			_attributesArray.push(["joinCondition", joinCondition, "optional"]);
			_attributesArray.push(["suppressJoinFailure", suppressJoinFailure, "optional"]);			
		}
		/*
		public function printArray():void {
			for ( var i:int = 0; i < attributesArray.length; i++){
				for (var j:int = 0; j < attributesArray[i].length; j++){
					trace(attributesArray[i][j])
				}
			}
		}
		*/
		public function get attributesArray():Array{
			return _attributesArray;
		}
		
		public function set attributesArray(attribtesValue:Array):void {
			_attributesArray = attribtesValue;
			//printArray();
		}
	}
}