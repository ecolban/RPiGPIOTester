package org.wintrisstech.erik.raspberrypi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.codec.binary.Base32;

public class IdKeeper {

	/* A seed of length 5 * N generates an id of length 8 * N. */
	private static final int SEED_LENGTH = 20; // should be a multiple of 5

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		try {
			System.out.println(new IdKeeper().getId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getId() throws IOException {
		String result = null;
		File idFile = new File("/home/pi/.sprinklerwiz/id.txt");
		File idDir = idFile.getParentFile();
		if (!idDir.exists()) {
			idDir.mkdirs();
		}
		if (!idFile.exists()) {
			result = generateId();
			writeId(result, idFile);
		} else {
			result = readId(idFile);
		}
		return result;
	}

	private String generateId() {
		byte[] seed = new byte[SEED_LENGTH];
		Random r = new Random();
		r.nextBytes(seed);
		Base32 base32 = new Base32();
		return base32.encodeToString(seed).toLowerCase();
	}

	private String readId(File idFile) throws IOException {
		String result;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(idFile));
			result = reader.readLine();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}

	private void writeId(String id, File idFile) throws IOException {
		FileWriter writer = null;
		try {
			idFile.createNewFile();
			writer = new FileWriter(idFile);
			writer.write(id);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
