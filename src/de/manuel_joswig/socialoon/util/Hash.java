package de.manuel_joswig.socialoon.util;

import java.security.MessageDigest;

/**
 * Contains functions for hashing strings
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class Hash {
    public static String getHash(String text, String hashType) {
        try {
        	MessageDigest md = java.security.MessageDigest.getInstance(hashType);
            byte[] array = md.digest(text.getBytes());
            StringBuffer sb = new StringBuffer();
            
            for (int i = 0; i < array.length; i++) {
            	sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) { }
        
        return null;
    }

    public static String md5(String text) {
        return Hash.getHash(text, "MD5");
    }

    public static String sha1(String text) {
        return Hash.getHash(text, "SHA1");
    }
}
