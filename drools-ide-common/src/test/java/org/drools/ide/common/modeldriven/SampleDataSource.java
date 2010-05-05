package org.drools.ide.common.modeldriven;

import java.util.ArrayList;
import java.util.List;

public class SampleDataSource {

	public static List<String> getData() {
		return new ArrayList<String>() {{
			add("Hello");
			add("World");
		}};
	}
}
