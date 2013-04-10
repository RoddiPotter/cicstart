package ca.ualberta.physics.cssdp.domain.catalogue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import ca.ualberta.physics.cssdp.dao.Persistent;
import ca.ualberta.physics.cssdp.model.Mnemonic;

@Entity
@Table(name = "catalogue_observatorygroup")
public class ObservatoryGroup extends Persistent {

	private static final long serialVersionUID = 0L;

	@Column(name = "ext_key", length = 50, nullable = false)
	@Type(type = "ca.ualberta.physics.cssdp.dao.MnemonicType")
	private Mnemonic externalKey;

	@OneToMany
	@JoinTable(
			name = "catalogue_observatorygroup_members", 
			joinColumns = @JoinColumn(name = "observatory_group_id"), 
			inverseJoinColumns = @JoinColumn(name = "observatory_id")
	)
	private List<Observatory> observatories = new ArrayList<Observatory>();

	@Column(name = "description", length = 1024, nullable = true)
	private String description;
	
	@Override
	public String _pk() {
		return externalKey._pk();
	}

	public Mnemonic getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(Mnemonic externalKey) {
		this.externalKey = externalKey;
	}

	public List<Observatory> getObservatories() {
		return observatories;
	}

	public void setObservatories(List<Observatory> observatories) {
		this.observatories = observatories;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
