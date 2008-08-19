package org.drools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class nvbj {


	public static<T> List<T> List(T...elements) {
		return Arrays.asList(elements);
	}


	public static<T> Set<T> Set(T...elements) {
		return new HashSet<T>(Arrays.asList(elements));
	}

	static class Pair <T1, T2> {
		public T1 _1 = null;
		public T2 _2 = null;

		public Pair(T1 o1, T2 o2){
			_1 = o1;
			_2 = o2;
		}
	}

	public static <K,V> Map<K,V> Map( Pair<K, V>  ... entries ){
		//SCREW YOU varargs and generics
		//they are runtime arrays, hence erasure bites us in the rear...
		//see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6227971
		Map<K, V> m = new HashMap<K, V>(entries.length);
		for (Pair<K, V> tuple2 : entries) {
			m.put(tuple2._1, tuple2._2);
		}
		return m;
	}

	static<K, V> Pair<K, V> __(K k, V v) {
		return new Pair<K, V>(k, v);
	}



	public static void println(Object m) {
		System.out.println(m);
	}

	public static void println_err(Object m) {
		System.err.println(m);

		new _c() {
			public void _() {
			}
		};
	}

	interface _c {
		void _();
	}



}



