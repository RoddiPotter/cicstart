package ca.ualberta.physics.cssdp.domain.catalogue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.dao.Persistent;

@Entity
@Table(name = "catalogue_url_dataproduct")
public class UrlDataProduct extends Persistent {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "dataproduct_id", nullable = false)
	private DataProduct dataProduct;

	@Column(name = "url", length = 2048, nullable = false)
	private String url;

	@Column(name = "start_tstamp", nullable = true)
	@Type(type = "ca.ualberta.physics.cssdp.dao.type.PersistentLocalDateTime")
	private LocalDateTime startTimestamp;

	@Column(name = "end_tstamp", nullable = true)
	@Type(type = "ca.ualberta.physics.cssdp.dao.type.PersistentLocalDateTime")
	private LocalDateTime endTimestamp;

	@Column(name = "scan_tstamp", nullable = false)
	@Type(type = "ca.ualberta.physics.cssdp.dao.type.PersistentLocalDateTime")
	private LocalDateTime scanTimestamp;

	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;

	@Override
	public String _pk() {
		return dataProduct._pk() + url;
	}

	public DataProduct getDataProduct() {
		return dataProduct;
	}

	public void setDataProduct(DataProduct dataProduct) {
		this.dataProduct = dataProduct;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public LocalDateTime getScanTimestamp() {
		return scanTimestamp;
	}

	public void setScanTimestamp(LocalDateTime scanTimestamp) {
		this.scanTimestamp = scanTimestamp;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setStartTimestamp(LocalDateTime startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public LocalDateTime getStartTimestamp() {
		return startTimestamp;
	}

	public void setEndTimestamp(LocalDateTime endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public LocalDateTime getEndTimestamp() {
		return endTimestamp;
	}

	public boolean hasChanged(UrlDataProduct updated) {

		boolean hasChanged;

		hasChanged = !getUrl().equals(updated.getUrl());
		hasChanged = !getDataProduct().equals(updated.getDataProduct())
				|| hasChanged;
		hasChanged = !getStartTimestamp().equals(updated.getStartTimestamp())
				|| hasChanged;
		hasChanged = !getEndTimestamp().equals(updated.getEndTimestamp())
				|| hasChanged;
		hasChanged = !isDeleted() && updated.isDeleted() || hasChanged;

		return hasChanged;
	}

	public static boolean isStopSignal(UrlDataProduct peek) {
		if (peek.url.equals("STOP")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static UrlDataProduct getStopSignal() {
		UrlDataProduct stop = new UrlDataProduct();
		stop.setUrl("STOP");
		return stop;
	}

}
