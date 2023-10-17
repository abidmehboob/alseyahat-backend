package com.alseyahat.app.feature.hotel.repository.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Getter
@Setter
@Entity
@Table(name = "restaurantMenu")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantMenu implements Serializable {

	static final long serialVersionUID = -798991492884005022L;

	@Id
	@Column(name = "menu_id", nullable = false)
	String menuId = UUID.randomUUID().toString().replace("-", "");

	@Column
	String name;
	
	@Column
	String description;
	
	@Column
	String image;
	
	@ManyToOne
    @JoinColumn(name = "hotel_id")
	Hotel hotel;
	  
	@Column(nullable = false)
	String charges;

	@Column
	boolean isEnabled = true;

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
