package org.zkoss.jpa.examples;

public class TldUtil {

	public static Class toClass(String name) throws ClassNotFoundException{
		return TldUtil.class.forName(name);
	}
}
