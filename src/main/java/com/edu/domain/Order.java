package com.edu.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "productorder")
public class Order {
	@Id
	@GenericGenerator(name = "order-id-sequence",
			strategy = "com.edu.domain.generator.OrderIdentifierGenerator",
			parameters = {@Parameter(name = "max_digits", value = "8")})
	@GeneratedValue(generator = "order-id-sequence",
			strategy = GenerationType.TABLE)
	protected String id;

    private String name;

    private String date;
    
    private Status status;

    private BigDecimal totalAmount;

    public static String[] ALL_STATUS = {
    		"等待付款", "付款中", "已付款", "已取消", "已发货", "退款申请中", "已退款"
	};

    @OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "payment_id")
	private Payment payment;
    
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    @JsonIgnore
    private Customer customer;

	@JsonIgnore
	@ElementCollection
    @CollectionTable(name="ORDER_COURSEPRODUCT", joinColumns=@JoinColumn(name="ORDER_ID", referencedColumnName="ID"))
	@MapKeyJoinColumn(name="COURSEPRODUCT_ID", referencedColumnName="ID")
	@Column(name="COPIES_IN_ORDER")
	private Map<CourseProduct, Integer> courseProductsMap = new HashMap<>();
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public String getStatusText() {
		if (status == null) {
			return "未知订单状态";
		}
		if (status.ordinal() >= ALL_STATUS.length) {
			return "未知订单状态";
		}
		return ALL_STATUS[status.ordinal()];
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Status {
		CREATED,     // Order created at this app but not yet at wechat
        NOTPAY,      // equals to wechat NOTPAY
        PAID,        // equals to wechat SUCCESS
        CANCELLED,   // equals to wechat CLOSED
        DELIVERED,   // Paid and goods delivered
		REFUND_REQUESTED, // paid and request to refund
        REFUND       // equals to wechat REFUND
	}

	public Map<CourseProduct, Integer> getCourseProductsMap() {
		return courseProductsMap;
	}

	public void addCourseProduct(CourseProduct courseProduct, int quantity) {
		this.courseProductsMap.put(courseProduct, quantity);
	}

	public void updateourseProductQuantity(CourseProduct courseProduct, int quantity) {
		if (quantity > 0) {
			this.courseProductsMap.replace(courseProduct, quantity);
		} else {
			this.courseProductsMap.remove(courseProduct);
		}
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
}
