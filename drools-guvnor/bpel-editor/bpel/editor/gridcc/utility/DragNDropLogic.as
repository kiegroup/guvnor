package bpel.editor.gridcc.utility
{
	import bpel.editor.gridcc.constant.WorkflowActivities;
	
	public class DragNDropLogic
	{
		public static function dragNdrop(containerActivityName:String, droppedActivityName:String):Boolean {
			
				switch(containerActivityName) {					
				    case WorkflowActivities.PROCESS:	
				    	// Don't allow user to add any activity in the process
				    	// other than the default Sequence									    		    
		            	//return process(containerActivityName,droppedActivityName);
		            	return false;
				    	break;				   			      					      	
				    
				    case WorkflowActivities.PARTNERLINKS:				      	
				      	return partnerLinks(containerActivityName,droppedActivityName);
				      	break;
				    
				    case WorkflowActivities.PARTNERS:	
				    	// To Be Done			      	
				      	return true;
				      	break;
				      	  	  	
				    case WorkflowActivities.VARIABLES:				      	
				      	return variables(containerActivityName,droppedActivityName);
				      	break;
				   	
				   	case WorkflowActivities.CORRELATIONSETS:	
				    	// To Be Done			      	
				      	return true;
				      	break;
				    
				    case WorkflowActivities.FAULTHANDLERS:	
				    	// To Be Done			      	
				      	return true;
				      	break;
				    
				    case WorkflowActivities.COMPENSATIONHANDLER:	
				    	// To Be Done			      	
				      	return true;
				      	break;
				    
				    case WorkflowActivities.EVENTHANDLERS:	
				    	// To Be Done			      	
				      	return true;
				      	break;
				    
				    case WorkflowActivities.EMPTY:	
				    	// To Be Done			      	
				      	return true;
				      	break;
				      	  	  	  	  	
				   	case WorkflowActivities.INVOKE:				      	
				      	return invoke(containerActivityName,droppedActivityName);
				      	break;				    
				    
				    case WorkflowActivities.RECEIVE:				      	
				      	return receive(containerActivityName,droppedActivityName);
				      	break;				      	  	 			    	
				    
				    case WorkflowActivities.REPLY:				      	
				      	return reply(containerActivityName,droppedActivityName);;
				      	break;				      	
				    
				    case WorkflowActivities.ASSIGN:				      	
				      	return assign(containerActivityName,droppedActivityName);;
				      	break;
				      	
				   	case WorkflowActivities.WAIT:				      	
				      	return true;
				      	break;
				      	
				   	case WorkflowActivities.THROW:
				      	return true;
				      	break;
				      	
				   	case WorkflowActivities.TERMINATE:
				      	return true;
				      	break;
				      	
				   	case WorkflowActivities.FLOW:
				      	return true;
				      	break;
				      	
				   	case WorkflowActivities.SWITCH:
				      	return true;
				      	break;
				      	
				   	case WorkflowActivities.WHILE:
				      	return true;
				      	break;
				      	
				   	case WorkflowActivities.SEQUENCE:
				      	return sequence(containerActivityName,droppedActivityName);
				      	break;
				      	
				   	
				   	case WorkflowActivities.PICK:
				      	// To Be Done
				      	return true;
				      	break;
				   	
				   	case WorkflowActivities.SCOPE:
				      	// To Be Done
				      	return true;
				      	break;
				   	
				   	default:
				      	trace("Out of range ...!");
				      	return false;
                }
			
			return false;
		}
		
		private static function partnerLinks(containerActivityName:String, droppedActivityName:String):Boolean {
			//trace("DragNDropLogic.partnerLinks");
			if(containerActivityName == WorkflowActivities.PARTNERLINKS){
				if(droppedActivityName == WorkflowActivities.PARTNERLINK){
					return true;
				}
			}
			return false;
		}
		
		private static function variables(containerActivityName:String, droppedActivityName:String):Boolean {
			if(containerActivityName == WorkflowActivities.VARIABLES){
				if(droppedActivityName == WorkflowActivities.VARIABLE){
					return true;
				}
			}
			return false;
		}
		
		private static function process(containerActivityName:String, droppedActivityName:String):Boolean {
			if(containerActivityName == WorkflowActivities.PROCESS){
				switch(droppedActivityName) {					
				    case WorkflowActivities.PARTNERLINKS:
						//trace("partnerLinks");					    		    
		            	return true;
				    	break;
				    	
				    case WorkflowActivities.PARTNERS:
				      	//trace("partners");
				      	return true;
				      	break;
				      	
				    case WorkflowActivities.VARIABLES:
				      	//trace("variables");
				      	return true;
				      	break;
				   	
				   	 case WorkflowActivities.CORRELATIONSETS:
				      	//trace("correlationSets");
				      	return true;
				      	break;
				     
				    case WorkflowActivities.FAULTHANDLERS:
				      	//trace("faultHandlers");
				      	return true;
				      	break;
				    
				    case WorkflowActivities.COMPENSATIONHANDLER:
				      	//trace("compensationHandler");
				      	return true;
				      	break;				      	  	 			    	
				    
				    case WorkflowActivities.EMPTY:
				      	//trace("empty");
				      	return true;
				      	break;
				    case WorkflowActivities.INVOKE:
				      	//trace("invoke");
				      	return true;
				      	break;
				    case WorkflowActivities.RECEIVE:
				      	//trace("receive");
				      	return true;
				      	break;
				    case WorkflowActivities.REPLY:
				      	//trace("reply");
				      	return true;
				      	break;
				   	case WorkflowActivities.ASSIGN:
				      	//trace("assign");
				      	return true;
				      	break;
				   	case WorkflowActivities.WAIT:
				      	//trace("wait");
				      	return true;
				      	break;
				   	case WorkflowActivities.THROW:
				      	//trace("throw");
				      	return true;
				      	break;
				   	case WorkflowActivities.TERMINATE:
				      	//trace("terminate");
				      	return true;
				      	break;
				   	case WorkflowActivities.FLOW:
				      	//trace("Tuesday");
				      	return true;
				      	break;
				   	case WorkflowActivities.SWITCH:
				      	//trace("switch");
				      	return true;
				      	break;
				   	case WorkflowActivities.WHILE:
				      	//trace("while");
				      	return true;
				      	break;
				   	case WorkflowActivities.SEQUENCE:
				      	//trace("sequence");
				      	return true;
				      	break;
				   	case WorkflowActivities.PICK:
				      	//trace("pick");
				      	return true;
				      	break;
				   	case WorkflowActivities.SCOPE:
				      	//trace("scope");
				      	return true;
				      	break;
				   	default:
				      	trace("Out of range   !");
				      	return false;
                }
			}
			return false;
		}
		
		private static function sequence(containerActivityName:String, droppedActivityName:String):Boolean {
			//trace("DragNDropLogic.sequence");
			if(containerActivityName == WorkflowActivities.SEQUENCE){
				switch(droppedActivityName) {					
				    case WorkflowActivities.TARGET:
						//trace("target");					    		    
		            	return true;
				    	break;
				    	
				    case WorkflowActivities.SOURCE:
				      	//trace("source");
				      	return true;
				      	break;				      	  	 			    	
				    
				    case WorkflowActivities.EMPTY:
				      	//trace("empty");
				      	return true;
				      	break;
				    case WorkflowActivities.INVOKE:
				      	//trace("invoke");
				      	return true;
				      	break;
				    case WorkflowActivities.RECEIVE:
				      	//trace("receive");
				      	return true;
				      	break;
				    case WorkflowActivities.REPLY:
				      	//trace("reply");
				      	return true;
				      	break;
				   	case WorkflowActivities.ASSIGN:
				      	//trace("assign");
				      	return true;
				      	break;
				   	case WorkflowActivities.WAIT:
				      	//trace("wait");
				      	return true;
				      	break;
				   	case WorkflowActivities.THROW:
				      	//trace("throw");
				      	return true;
				      	break;
				   	case WorkflowActivities.TERMINATE:
				      	//trace("terminate");
				      	return true;
				      	break;
				   	case WorkflowActivities.FLOW:
				      	//trace("Tuesday");
				      	return true;
				      	break;
				   	case WorkflowActivities.SWITCH:
				      	//trace("switch");
				      	return true;
				      	break;
				   	case WorkflowActivities.WHILE:
				      	//trace("while");
				      	return true;
				      	break;
				   	case WorkflowActivities.SEQUENCE:
				      	//trace("sequence");
				      	return true;
				      	break;
				   	case WorkflowActivities.PICK:
				      	//trace("pick");
				      	return true;
				      	break;
				   	case WorkflowActivities.SCOPE:
				      	//trace("scope");
				      	return true;
				      	break;
				   	default:
				      	trace("Out of range ???");
				      	return false;
                }
			}
			return false;
		}
		
		private static function scope(containerActivityName:String, droppedActivityName:String):Boolean {
			//trace("DragNDropLogic.scope");
			if(containerActivityName == WorkflowActivities.SCOPE){
				switch(droppedActivityName) {					
				    case WorkflowActivities.TARGET:
						//trace("target");					    		    
		            	return true;
				    	break;
				    	
				    case WorkflowActivities.SOURCE:
				      	//trace("source");
				      	return true;
				      	break;				      	  	 			    	
				    
				    case WorkflowActivities.EMPTY:
				      	//trace("empty");
				      	return true;
				      	break;
				    case WorkflowActivities.INVOKE:
				      	//trace("invoke");
				      	return true;
				      	break;
				    case WorkflowActivities.RECEIVE:
				      	//trace("receive");
				      	return true;
				      	break;
				    case WorkflowActivities.REPLY:
				      	//trace("reply");
				      	return true;
				      	break;
				   	case WorkflowActivities.ASSIGN:
				      	//trace("assign");
				      	return true;
				      	break;
				   	case WorkflowActivities.WAIT:
				      	//trace("wait");
				      	return true;
				      	break;
				   	case WorkflowActivities.THROW:
				      	//trace("throw");
				      	return true;
				      	break;
				   	case WorkflowActivities.TERMINATE:
				      	//trace("terminate");
				      	return true;
				      	break;
				   	case WorkflowActivities.FLOW:
				      	//trace("Tuesday");
				      	return true;
				      	break;
				   	case WorkflowActivities.SWITCH:
				      	//trace("switch");
				      	return true;
				      	break;
				   	case WorkflowActivities.WHILE:
				      	//trace("while");
				      	return true;
				      	break;
				   	case WorkflowActivities.SEQUENCE:
				      	//trace("sequence");
				      	return true;
				      	break;
				   	case WorkflowActivities.PICK:
				      	//trace("pick");
				      	return true;
				      	break;
				   	case WorkflowActivities.SCOPE:
				      	//trace("scope");
				      	return true;
				      	break;
				   	default:
				      	trace("Out of range ???");
				      	return false;
                }
			}
			return false;
		}
		private static function invoke(containerActivityName:String, droppedActivityName:String):Boolean {
			if(containerActivityName == WorkflowActivities.INVOKE){
				switch(droppedActivityName) {					
				    case WorkflowActivities.TARGET:
						//trace("target");					    		    
		            	return true;
				    	break;
				    	
				    case WorkflowActivities.SOURCE:
				      	//trace("source");
				      	return true;
				      	break;
				      	
				    case WorkflowActivities.CORRELATIONS:
				      	//trace("correlations");
				      	return true;
				      	break;
				   	
				   	 case WorkflowActivities.CATCH:
				      	//trace("catch");
				      	return true;
				      	break;
				     
				    case WorkflowActivities.CATCHALL:
				      	//trace("catchAll");
				      	return true;
				      	break;			    
				   				      	
				    case WorkflowActivities.COMPENSATIONHANDLER:
				      	//trace("compensationHandler");
				      	return true;
				      	break;
				      		
				   	default:
				      	trace("Out of range");
				      	return false;
                }
			}
			return false;
		}
		
		private static function receive(containerActivityName:String, droppedActivityName:String):Boolean {
			if(containerActivityName == WorkflowActivities.RECEIVE){
				switch(droppedActivityName) {					
				    case WorkflowActivities.TARGET:
						//trace("target");					    		    
		            	return true;
				    	break;
				    	
				    case WorkflowActivities.SOURCE:
				      	//trace("source");
				      	return true;
				      	break;
				      	
				    case WorkflowActivities.CORRELATIONS:
				      	//trace("correlations");
				      	return true;
				      	break;				  
				      		
				   	default:
				      	//trace("Out of range");
				      	return false;
                }
			}
			return false;
		}
		
		private static function reply(containerActivityName:String, droppedActivityName:String):Boolean {
			if(containerActivityName == WorkflowActivities.REPLY){
				switch(droppedActivityName) {					
				    case WorkflowActivities.TARGET:
						//trace("target");					    		    
		            	return true;
				    	break;
				    	
				    case WorkflowActivities.SOURCE:
				      	//trace("source");
				      	return true;
				      	break;
				      	
				    case WorkflowActivities.CORRELATIONS:
				      	//trace("correlations");
				      	return true;
				      	break;				  
				      		
				   	default:
				      	trace("Out of range");
				      	return false;
                }
			}
			return false;
		}
		
		private static function assign(containerActivityName:String, droppedActivityName:String):Boolean {
			if(containerActivityName == WorkflowActivities.ASSIGN){
				switch(droppedActivityName) {					
				    case WorkflowActivities.TARGET:
						//trace("target");					    		    
		            	return true;
				    	break;
				    	
				    case WorkflowActivities.SOURCE:
				      	//trace("source");
				      	return true;
				      	break;
				      	
				    case WorkflowActivities.COPY:
				      	//trace("copy");
				      	return true;
				      	break;				  
				      		
				   	default:
				      	trace("Out of range");
				      	return false;
                }
			}
			return false;
		}
		
		private static function copy(containerActivityName:String, droppedActivityName:String):Boolean {
			if(containerActivityName == WorkflowActivities.COPY){
				switch(droppedActivityName) {					
				    case WorkflowActivities.TO:
						//trace("to");					    		    
		            	return true;
				    	break;
				    	
				    case WorkflowActivities.FROM:
				      	//trace("from");
				      	return true;
				      	break;		    			  
				      		
				   	default:
				      	trace("Out of range");
				      	return false;
                }
			}
			return false;
		}	
	}
}