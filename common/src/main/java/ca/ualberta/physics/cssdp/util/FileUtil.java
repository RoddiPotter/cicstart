package ca.ualberta.physics.cssdp.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

public class FileUtil {

	private static final FileFilter thisAndParentFilter = new FileFilter() {

		@Override
		public boolean accept(File arg0) {
			return !arg0.getName().equals(".") && !arg0.getName().equals("..");
		}
	};

	private static String relativize(File actual, String relativeRoot) {
		String path = null;
		try {
			path = actual.getCanonicalPath();
			path = path.replaceAll(relativeRoot, "");
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		return path;
	}

	public static File tar(File directory, String relativeRoot) {

		File tarFile = new File(directory.getParent(), directory.getName()
				+ ".tar");
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(tarFile);
			List<File> fileList = walk(directory);

			TarArchiveOutputStream out = (TarArchiveOutputStream) new ArchiveStreamFactory()
					.createArchiveOutputStream(ArchiveStreamFactory.TAR, os);
			out.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
			for (File file : fileList) {
				TarArchiveEntry ae = new TarArchiveEntry(file,
						FileUtil.relativize(file, relativeRoot));
				ae.setMode(getMode(file));
				out.putArchiveEntry(ae);
				Files.copy(file, out);
				out.closeArchiveEntry();
			}
			out.flush();
			out.finish();

		} catch (Exception e) {
			throw new IllegalStateException(String.format(
					"Couldn't create tar file %s.", tarFile.getAbsolutePath()
							+ " because " + e.getMessage()));
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}
		return tarFile;

	}

	private static int getMode(File file) {
		int mode = 0100000;
		if (file.canRead()) {
			mode += 0444;
		}
		if (file.canWrite()) {
			mode += 0222;
		}
		if (file.canExecute()) {
			mode += 0111;
		}
		return mode;
	}

	public static List<File> walk(File dir) {
		List<File> files = new LinkedList<File>();
		for (File file : dir.listFiles(thisAndParentFilter)) {
			if (file.isFile()) {
				files.add(file);
			} else {
				files.addAll(walk(file));
			}
		}
		return files;
	}

	public static List<File> unTar(File tar, File outputDir) {

		final List<File> untaredFiles = new LinkedList<File>();
		InputStream is = null;
		try {
			is = new FileInputStream(tar);
			final TarArchiveInputStream inputStream = (TarArchiveInputStream) new ArchiveStreamFactory()
					.createArchiveInputStream(ArchiveStreamFactory.TAR, is);
			TarArchiveEntry entry = null;
			while ((entry = (TarArchiveEntry) inputStream.getNextEntry()) != null) {
				File outputFile = new File(outputDir, entry.getName());
				if (entry.isDirectory()) {
					if (!outputFile.exists()) {
						if (!outputFile.mkdirs()) {
							throw new IllegalStateException(String.format(
									"Couldn't create directory %s.",
									outputFile.getAbsolutePath()));
						}
					}
				} else {
					Files.createParentDirs(outputFile);
					outputFile.createNewFile();
					setMode(outputFile, entry.getMode());
					OutputStream outputFileStream = new FileOutputStream(
							outputFile);
					IOUtils.copy(inputStream, outputFileStream);
					outputFileStream.close();
				}
				untaredFiles.add(outputFile);
			}
		} catch (Exception e) {
			throw new IllegalStateException(String.format(
					"Couldn't extract tar file %s.", tar.getAbsolutePath()
							+ " because " + e.getMessage()));
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignore) {
				}
			}
		}
		return untaredFiles;
	}

	private static void setMode(File outputFile, int mode) {

		boolean read = true;
		boolean write = true;
		boolean exec = true;

		int notPermission = 0100777 - mode;
		if (notPermission == 000) {
			// file has full permissions
		} else if (notPermission < 0222) {
			// file doesn't have exec
			exec = false;
		} else if (notPermission < 0444) {
			// file doesn't have write permission
			write = false;
		} else if (notPermission >= 0444) {
			// file doesn't have read permission
			read = false;
		}

		outputFile.setExecutable(exec, false);
		outputFile.setWritable(write, false);
		outputFile.setReadable(read, false);

	}

	public static File gzip(File file) {

		final File gzFile = new File(file.getParent(), file.getName() + ".gz");

		OutputStream os = null;
		try {
			os = new FileOutputStream(gzFile);
			final GZIPOutputStream out = new GZIPOutputStream(os);
			gzFile.createNewFile();
			Files.copy(file, out);
			out.finish();
		} catch (Exception e) {
			throw new IllegalStateException(String.format(
					"Couldn't create gzip file %s.", gzFile.getAbsolutePath()
							+ " because " + e.getMessage()));

		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}
		// cleanup
		file.delete();
		return gzFile;

	}

	public static File unGzip(File gzFile, File outputDir) {

		final File outputFile = new File(outputDir, gzFile.getName().substring(
				0, gzFile.getName().length() - 3));

		InputStream is = null;
		try {
			is = new FileInputStream(gzFile);
			final GZIPInputStream in = new GZIPInputStream(is);
			outputFile.createNewFile();
			Files.copy(new InputSupplier<InputStream>() {

				@Override
				public InputStream getInput() throws IOException {
					return in;
				}
			}, outputFile);
		} catch (Exception e) {
			throw new IllegalStateException(String.format(
					"Couldn't extract gzip file %s.", gzFile.getAbsolutePath()
							+ " because " + e.getMessage()));

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignore) {
				}
			}
		}
		return outputFile;
	}

	/**
	 * Duplicate the directory given in the constructor to the one given in the
	 * to() method of the returned CopyDir object.
	 * 
	 * @param dir
	 * @return
	 */
	public static CopyDir copy(File dir) {
		return new CopyDir(dir);
	}

	public static class CopyDir {
		final File source;

		CopyDir(File source) {
			this.source = source;
		}

		public void to(File dest) {

			for (File file : source.listFiles(thisAndParentFilter)) {
				File to = new File(dest, file.getName());
				if (file.isFile()) {
					try {
						Files.createParentDirs(to);
						to.createNewFile();
						Files.copy(file, to);
						to.setExecutable(file.canExecute());
						to.setReadable(file.canRead());
						to.setWritable(file.canWrite());
						to.setLastModified(file.lastModified());
					} catch (IOException e) {
						Throwables.propagate(e);
					}
				} else {
					new CopyDir(file).to(to);
				}
			}
		}
	}

}
