package ca.ualberta.physics.cssdp.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Throwables;

public class HashUtils {

	/**
	 * From a password, a number of iterations and a salt, returns the
	 * corresponding digest
	 * 
	 * @param iterationNb
	 *            int The number of iterations of the algorithm
	 * @param password
	 *            String The password to encrypt
	 * @param salt
	 *            byte[] The salt
	 * @return byte[] The digested password
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm doesn't exist
	 */
	public static byte[] getHash(int iterationNb, String password, byte[] salt) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			digest.update(salt);
			byte[] input = digest.digest(password.getBytes("UTF-8"));
			for (int i = 0; i < iterationNb; i++) {
				digest.reset();
				input = digest.digest(input);
			}
			return input;
		} catch (NoSuchAlgorithmException nsa) {
			throw Throwables.propagate(nsa);
		} catch (UnsupportedEncodingException e) {
			throw Throwables.propagate(e);
		}
	}

	/**
	 * From a base 64 representation, returns the corresponding byte[]
	 * 
	 * @param data
	 *            String The base64 representation
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] base64ToByte(String data) {
		Base64 decoder = new Base64();
		return decoder.decode(data);
	}

	/**
	 * From a byte[] returns a base 64 representation
	 * 
	 * @param data
	 *            byte[]
	 * @return String
	 * @throws IOException
	 */
	public static String byteToBase64(byte[] data) {
		Base64 encoder = new Base64();
		return encoder.encodeAsString(data);
	}

}
