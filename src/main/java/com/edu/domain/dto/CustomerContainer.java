package com.edu.domain.dto;

import javax.validation.constraints.NotNull;

import com.edu.domain.Customer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CustomerContainer {
	@NotNull
	private String openCode;

	@NotNull
	private String name;

	@NotNull
	private String mobilePhone;

	@NotNull
	private String address;

	private Long verifyCodeId;

	private String verifyCode;

	private List<ChildContainer> children = new ArrayList<>();

	public CustomerContainer(Customer customer) {
		this.openCode = customer.getOpenCode();
		this.name = customer.getName();
		this.mobilePhone = customer.getMobilePhone();
		this.address = customer.getAddress();
		this.children = customer.getStudents().stream().map(x -> new ChildContainer(x))
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
