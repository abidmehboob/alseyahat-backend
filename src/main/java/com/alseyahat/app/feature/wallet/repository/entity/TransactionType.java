package com.alseyahat.app.feature.wallet.repository.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.PrePersist;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "transaction_type")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionType {

	@Id
	@Column(name = "id", nullable = false, unique = true)
	private String id;

	@Column(name = "description")
	private String description;

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
