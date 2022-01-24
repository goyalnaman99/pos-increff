package com.increff.pos.util;

public class BarcodeUtil {

	public static String randomString(int len) {
		String AlphaNumeric = "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(len);
		
		for (int i = 0; i < len; i++) {

			int index = (int) (AlphaNumeric.length() * Math.random());

			sb.append(AlphaNumeric.charAt(index));

		}
	
		return sb.toString();
	}
}
