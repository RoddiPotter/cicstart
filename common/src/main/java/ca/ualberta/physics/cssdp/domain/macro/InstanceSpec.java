package ca.ualberta.physics.cssdp.domain.macro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiProperty;

@XmlRootElement(name = "InstanceSpec")
@XmlAccessorType(XmlAccessType.FIELD)
public class InstanceSpec {

	@XmlElement
	@ApiProperty(value = "The actual cloud to start this image on (DAIR, CESWP, EC2, etc)", required = true)
	private String cloud;

	@XmlElement
	@ApiProperty(value = "The name of the image to use", required = true)
	private String image;

	@XmlElement
	@ApiProperty(value = "The flavor of the VM, default to m1.tiny", allowableValues = "m1.tiny, m1.small, m1.medium, m1.large, m1.xlarge", required = false)
	private String flavor = "m1.tiny";

	@XmlElement
	@ApiProperty(value = "An ID you can refer to later, like the job id of the macro you are running", required = true)
	private String requestId;

	public String getCloud() {
		return cloud;
	}

	public void setCloud(String cloud) {
		this.cloud = cloud;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getFlavor() {
		return flavor;
	}

	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}
