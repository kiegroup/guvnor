package bpel.editor.gridcc.data
{
	public interface DOInterface
	{
		function updateAttributesArray(attributeName:String, attributeValue:String):void;
		
		function getName():String;
		
		function populateAttributes(attNamesList:XMLList):String;
		
		function updateSubActivitiesArray(type:String, activity:Object):void;
	}
}