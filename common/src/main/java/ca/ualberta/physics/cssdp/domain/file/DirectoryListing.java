package ca.ualberta.physics.cssdp.domain.file;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DirectoryListing {

	private List<RemoteFile> remoteFiles = new ArrayList<RemoteFile>();

	public DirectoryListing() {
		// for jaxb
	}

	public DirectoryListing(List<RemoteFile> remoteFiles) {
		this.remoteFiles.addAll(remoteFiles);
	}

	@XmlElementWrapper
	@XmlElement
	public List<RemoteFile> getRemoteFiles() {
		return remoteFiles;
	}

	@Override
	public String toString() {
		StringBuffer buffy = new StringBuffer();
		for (RemoteFile remoteFile : remoteFiles) {
			buffy.append(remoteFile.getModifedTstamp() + " "
					+ remoteFile.getSize() + " " + remoteFile.getUrl()
					+ System.getProperty("line.separator"));
		}
		return buffy.toString();
	}

}
