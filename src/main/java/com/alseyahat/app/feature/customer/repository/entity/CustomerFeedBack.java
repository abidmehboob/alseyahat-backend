package com.alseyahat.app.feature.customer.repository.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "customer_feed_back")
public class CustomerFeedBack implements Serializable {

    static final long serialVersionUID = -798992294484005022L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer", referencedColumnName = "customer_id")
    Customer customer;
	
	@Column
    String FeedBackHeading;
	
	@Column
    String FeedBack;


}
