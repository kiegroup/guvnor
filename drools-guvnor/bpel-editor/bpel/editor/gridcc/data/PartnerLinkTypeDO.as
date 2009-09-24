package bpel.editor.gridcc.data
{
	public class PartnerLinkTypeDO
	{
		
		private var _name:String;
		private var _role:String;		
		private var _portType:String;		
		private var _namespacePrefix:String;		
		private var _operationNames:Array = new Array();
		
		public function get Name():String{
			return _name;
		}
		
		public function get Role():String {
			return _role;
		}
		
		public function get PortType():String{
			return _portType;
		}
		
		public function get NamespacePrefix():String {
			return _namespacePrefix;
		}
		
		public function get OperationNames():Array{
			 return _operationNames;
		}
		
		public function set Name(nameValue:String):void {
			_name = nameValue;			
		}
		
		public function set Role(roleValue:String):void {
			_role = roleValue;
		}
		
		public function set NamespacePrefix(namesapcePrefixValue:String):void{
			_namespacePrefix = namesapcePrefixValue;
		}
		
		public function set PortType(portTypeValue:String):void {
			_portType = portTypeValue;
		}
		
		public function set OperationNames(operationNamesValues:Array):void{
			_operationNames = operationNamesValues;
		}
	}
}