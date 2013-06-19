package ca.ualberta.physics.cssdp.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

public class CopyDirTest {

	private FileFilter filter;

	@Before
	public void setup() {
		filter = new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				return !arg0.getName().equals(".")
						&& !arg0.getName().equals("..");
			}
		};

	}

	@Test
	public void testCopy() {

		File tempDir = Files.createTempDir();
		File srcDir = new File("../macro/build/distributions/macro-1.0");
		FileUtil.copy(srcDir).to(tempDir);

		System.out.println(tempDir.getAbsolutePath());

		compare(srcDir, tempDir);

		delete(tempDir);

	}

	private void compare(File srcDir, File destDir) {

		List<File> original = walk(srcDir);
		List<File> copied = walk(destDir);

		Assert.assertEquals(original.size(), copied.size());

		for (int i = 0; i < original.size(); i++) {
			Assert.assertTrue(isEqual(original.get(i), copied.get(i)));
		}

	}

	private List<File> walk(File dir) {
		List<File> files = new ArrayList<File>();
		for (File file : dir.listFiles(filter)) {
			if (file.isFile()) {
				files.add(file);
			} else {
				files.addAll(walk(file));
			}
		}
		return files;
	}

	private boolean isEqual(File src, File dest) {
		boolean equal = src.getName().equals(dest.getName());
		equal = equal && src.isFile() == dest.isFile();
		equal = equal && src.length() == dest.length();
		equal = equal && src.canExecute() == dest.canExecute();
		equal = equal && src.canRead() == dest.canRead();
		equal = equal && src.canWrite() == dest.canWrite();
		equal = equal && src.lastModified() == dest.lastModified();
		return equal;
	}

	private void delete(File dir) {

		for (File file : dir.listFiles(filter)) {
			if (file.isFile()) {
				file.delete();
			} else {
				delete(file);
			}
		}
	}

}
