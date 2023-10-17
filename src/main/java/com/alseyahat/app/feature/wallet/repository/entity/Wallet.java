package com.alseyahat.app.feature.wallet.repository.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "wallet")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull(message = "User Id must be provided")
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "user_type")
	private String userType;

	@Min(0)
	@Column(name = "balance", nullable = false)
	@NotNull(message = "Wallet balance must be provided")
	private BigDecimal balance;

	@NotNull(message = "Wallet currency must be provided")
	@ManyToOne
	@JoinColumn(name = "currency_id")
	private Currency currency;

	@Column(name = "last_updated_by")
	private String lastUpdatedBy;

	@OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
	private List<Transaction> transactions;
	
	@Column
	private String paymentReceipt;

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
