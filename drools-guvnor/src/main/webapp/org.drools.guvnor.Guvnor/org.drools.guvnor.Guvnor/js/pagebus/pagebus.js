/**
 * Copyright (c) 2006-2007, TIBCO Software Inc.
 * Use, modification, and distribution subject to terms of license.
 * 
 * TIBCO(R) PageBus 1.1.0
 */

if(typeof window.PageBus == 'undefined') {

PageBus = {
	version: "1.1.0",
	S: {c:{},s:[]},                 
	X: 0,
	P: 0,
	U: [],
	H: "undefined"
};

PageBus.subscribe = function(name, scope, callback, subscriberData)			
{
	if(name == null)
		this._badName();
	if(scope == null)
		scope = window;
	var path = name.split(".");
	var sub = { f: callback, d: subscriberData, i: this.X++, p: path, w: scope };
	for(var i = 0; i < path.length; i++) {
		if((path[i].indexOf("*") != -1) && (path[i] != "*") && (path[i] != "**"))
					this._badName();
	}
 	this._subscribe(this.S, path, 0, sub);
	return sub;		
}

PageBus.publish = function (name, message)		
{	
	if((name == null) || (name.indexOf("*") != -1)) 
		this._badName();
	var path = name.split(".");
	if(this.P > 100) 
		this._throw("StackOverflow");
	try {
		this.P++;
		this._publish(this.S, path, 0, name, message);
	}
	catch(err) {
		this.P--;
		throw err;
	}
	try {
		this.P--;
		if((this.U.length > 0) && (this.P == 0)) {
			for(var i = 0; i < this.U.length; i++)
				this.unsubscribe(this.U[i]);
			this.U = [];
		}
	}
	catch(err) {	
		// All unsubscribe exceptions should already have 
		// been handled when unsubscribe was called in the
		// publish callback. This is a repeat appearance 
		// of this exception. Discard it.
	}
}

PageBus.unsubscribe = function(sub) 
{
	this._unsubscribe(this.S, sub.p, 0, sub.i);
}

/*
 * @private  @jsxobf-clobber
 */
PageBus._throw = function(n) 
{ 
	throw new Error("PageBus." + n); 
}

/*
 * @private  @jsxobf-clobber
 */
PageBus._badName = function(n) 
{ 
	this._throw("BadName"); 
}

/*
 * @private  @jsxobf-clobber
 */
PageBus._subscribe = function(tree, path, index, sub) 
{
	var tok = path[index];
	if(tok == "")
		this._badName();
	if(index == path.length) 	
		tree.s.push(sub);
	else { 
		if(typeof tree.c == this.H) 
			tree.c = {};
		if(typeof tree.c[tok] == this.H) {
			try {
				tree.c[tok] = { c: {}, s: [] }; 
				this._subscribe(tree.c[tok], path, index + 1, sub);
			}
			catch(err) {
				delete tree.c[tok];
				throw err;
			}
		}
		else 
			this._subscribe( tree.c[tok], path, index + 1, sub );
	}
}

/*
 * @private  @jsxobf-clobber
 */
PageBus._publish = function(tree, path, index, name, msg) {
	if(path[index] == "")
		this._badName();
	if(typeof tree != this.H) {
		if(index < path.length) {
			this._publish(tree.c[path[index]], path, index + 1, name, msg);
			this._publish(tree.c["*"], path, index + 1, name, msg);			
			this._call(tree.c["**"], name, msg);
		}
		else
			this._call(tree, name, msg);
	}
}

/*
 * @private  @jsxobf-clobber
 */
PageBus._call = function(node, name, msg) {
	if(typeof node != this.H) {
		var callbacks = node.s;
		var max = callbacks.length;
		for(var i = 0; i < max; i++) 
			if(callbacks[i].f != null) 
				callbacks[i].f.apply(callbacks[i].w, [name, msg, callbacks[i].d]); 
	}
}
	
/*
 * @jsxobf-clobber
 */
PageBus._unsubscribe = function(tree, path, index, sid) {
	if(typeof tree != this.H) {
		if(index < path.length) {
			var childNode = tree.c[path[index]];
			this._unsubscribe(childNode, path, index + 1, sid);
			if(childNode.s.length == 0) {
				for(var x in childNode.c) // not empty. We're done.
				 	return;
				delete tree.c[path[index]];	// if we got here, c is empty
			}
			return;
		}
		else {
			var callbacks = tree.s;
			var max = callbacks.length;
			for(var i = 0; i < max; i++) {
				if(sid == callbacks[i].i) {
					if(this.P > 0) {
						if(callbacks[i].f == null) 
							this._throw("BadParameter");
						callbacks[i].f = null;
						this.U.push(callbacks[i]);						
					}
					else
						callbacks.splice(i, 1);
					return; 	
				}
			}
			// Not found. Fall through
		}
	}
	this._throw("BadParameter");
}

}