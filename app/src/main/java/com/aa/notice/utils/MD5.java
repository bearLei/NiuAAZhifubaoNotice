package com.aa.notice.utils;

import java.security.MessageDigest;

public class MD5 {

	public static String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return str;
	}
	
	public static String md5byte(byte[] str) {
		String str1 = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str);
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str1 = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return str1;
	}
	public static void main(String[] args) {
		System.out.println(md5("31119@qq.com"+"123456"));
		System.out.println(md5("mj1"));
		System.out.println(md5("1BQljr6O6WxeHGZ77JU2qQ1eiMQvBdFFl3xTYWEIfW4="));
	}
}
