package bpel.editor.gridcc.utility
{
	import flash.external.*;
	import flash.utils.*;

	public class QueryString 
	{

		private var _queryString:String;
		private var _all:String;
		private var _params:Object;
		
		public function get queryString():String
		{
			return _queryString;
		}
		public function get url():String
		{
			return _all;
		}
		public function get parameters():Object
		{
			return _params;
		}		

		
		public function QueryString()
		{
		
			readQueryString();
		}

		private function readQueryString():void
		{
			_params = {};
			try 
			{
				_all =  ExternalInterface.call("window.location.href.toString");
				_queryString = ExternalInterface.call("window.location.search.substring", 1);
				if(_queryString)
				{
					// Split the query string separated by "&"
					var params:Array = _queryString.split('&');
					var length:uint = params.length;
					
					for (var i:uint=0,index:int=-1; i<length; i++) 
					{
						// process each entry in the params array
						var kvPair:String = params[i];
						
						// find the index of "=" in each entry
						if((index = kvPair.indexOf("=")) > 0)
						{
							// split each entry in two components
							// key and value
							var key:String = kvPair.substring(0,index);
							var value:String = kvPair.substring(index+1);
							_params[key] = value;
						}
					}
				}
			}catch(e:Error) { 
				trace("Some error occured. ExternalInterface doesn't work in Standalone player.");	 
			}
		}

	}
}
