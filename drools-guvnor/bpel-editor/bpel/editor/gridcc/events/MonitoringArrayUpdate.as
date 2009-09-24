package bpel.editor.gridcc.events
{
	import flash.events.Event;
	[Event(name="monitoringArrayUpdateEvent", type="bpel.editor.gridcc.events.MonitoringArrayUpdate")]
	public class MonitoringArrayUpdate extends Event
	{
		public static const MONITORING_ARRAY_UPDATE_EVENT:String = "monitoringArrayUpdateEvent";
		
		public function MonitoringArrayUpdate(type:String){
			super(type);
		}
		
		override public function clone():Event {
			return new MonitoringArrayUpdate(MONITORING_ARRAY_UPDATE_EVENT);
		}
	}
}