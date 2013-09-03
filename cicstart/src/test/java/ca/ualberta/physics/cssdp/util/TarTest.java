package ca.ualberta.physics.cssdp.util;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.configuration.ApplicationProperties;

import com.google.common.io.Files;

public class TarTest {

	@Test
	public void testTarAndUnTar() throws Exception {

		// tar up the source files
		File dirToTar = new File(ApplicationProperties.class.getResource(".")
				.toURI()).getParentFile().getCanonicalFile();

		File tarFile = FileUtil
				.tar(dirToTar,
						"/home/rpotter/workspaces/cicstart/common/bin/ca/ualberta/physics");

		File untarDir = Files.createTempDir();

		List<File> untaredFiles = FileUtil.unTar(tarFile, untarDir);

		List<File> original = FileUtil.walk(dirToTar);

		Assert.assertEquals(original.size(), untaredFiles.size());

		for (int i = 0; i < original.size(); i++) {
			Assert.assertEquals(original.get(i).getName(), untaredFiles.get(i)
					.getName());
		}

		// cleanup
		tarFile.delete();
		for (File file : untaredFiles) {
			file.delete();
		}
	}

	@Test
	public void testGzipAndUnGzip() throws Exception {

		// tar up the source files
		File dirToTar = new File(ApplicationProperties.class.getResource(".")
				.toURI()).getParentFile().getCanonicalFile();

		File tarFile = FileUtil
				.tar(dirToTar,
						"/home/rpotter/workspaces/cicstart/common/bin/ca/ualberta/physics");

		long tarFileSize = tarFile.length();
		String tarFileName = tarFile.getName();

		File gzippedFile = FileUtil.gzip(tarFile);

		Assert.assertTrue(tarFileSize > gzippedFile.length());
		Assert.assertEquals(tarFile.getAbsolutePath() + ".gz",
				gzippedFile.getAbsolutePath());

		File tempDir = Files.createTempDir();

		File unGzippedFile = FileUtil.unGzip(gzippedFile, tempDir);

		Assert.assertEquals(tarFileSize, unGzippedFile.length());
		Assert.assertEquals(tarFileName, unGzippedFile.getName());

		unGzippedFile.delete();
		tempDir.delete();

	}

}
