package cc.aznc.demo.androidservice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

public class Tools {
	public static int MaxLen = 5;
	
	static String md5(String plainText) {
		MessageDigest md;
		
		try {md = MessageDigest.getInstance("MD5");}
		catch (NoSuchAlgorithmException e) { return ""; }
		
		md.update(plainText.getBytes());
		byte[] mdbytes = md.digest();
		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
        	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();
	}
	
	static String randomString() {
		//String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
		String chars = "0123456789";
		char text[] = new char[MaxLen];
		for (int i = 0; i < MaxLen; ++i) {
			text[i] = chars.charAt( (int)(Math.random() * chars.length()) );
		}
		
		return new String(text);
	}
	
	static String brutalCrack(String md5String) {
		Integer trial = 0;
		while (trial < Integer.MAX_VALUE) {
			trial++;
			String guess = randomString();
			
			/*
			if (trial == 10000) {
				guess = "111";
				Log.e("going to check", "guess 111, md5=" + md5(guess) + ", passmd5=" + md5String);
			}
			*/
			if (md5String.equals(md5(guess))) {
				Log.e("Tools", "trial: " + trial + ", found password: " + guess);
				return guess;
			}
			
			if ((trial % 1000) == 0) {
				Log.e("Tools", "trial: " + trial + ", guess: " + guess);
			}
		}
		
		return "Max try exceeded";
	}
}
