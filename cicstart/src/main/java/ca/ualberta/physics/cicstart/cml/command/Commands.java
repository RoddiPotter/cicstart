package ca.ualberta.physics.cicstart.cml.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

/**
 * A helper class with some utilities to help with DRY
 * 
 * @author rpotter
 * 
 */
public class Commands {

	/**
	 * Saves the given input stream into the file given.
	 * 
	 * @param inputStream
	 * @param file
	 */
	public static File streamToFile(final InputStream inputStream, File file) {

		try {
			Files.copy(new InputSupplier<InputStream>() {

				@Override
				public InputStream getInput() throws IOException {
					return inputStream;
				}

			}, file);
			return new File(file.getAbsolutePath());
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		return null;
	}

}
