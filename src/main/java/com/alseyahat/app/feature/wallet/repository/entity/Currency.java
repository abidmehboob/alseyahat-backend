package com.alseyahat.app.feature.wallet.repository.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "currency")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Currency {

	@Id
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "last_updated_by")
	private String lastUpdatedBy;

	@Column
	Date dateCreated;

	@Column
	Date lastUpdated;

	@PrePersist
	protected void prePersist() {
		lastUpdated = new Date();
		if (dateCreated == null) {
			dateCreated = new Date();
		}
	}
}
