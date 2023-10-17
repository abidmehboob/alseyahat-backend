package com.alseyahat.app.feature.wallet.repository.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "transaction")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "Transaction globalId must not be empty")
	@NotNull(message = "Transaction globalId must be provided")
	@Column(name = "global_id", nullable = false)
	private String globalId;

	@NotNull(message = "Trnansaction typeId must be provided")
	@ManyToOne
	@JoinColumn(name = "type_id")
	private TransactionType type;

	@NotNull(message = "Transaction amount must be provided")
	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@NotNull(message = "Transaction wallet must be provided")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;

	@NotNull(message = "Transaction currency must be provided")
	@ManyToOne
	@JoinColumn(name = "currency_id")
	private Currency currency;

	@Column(name = "description")
	String description;

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
