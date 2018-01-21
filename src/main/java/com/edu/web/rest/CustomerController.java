package com.edu.web.rest;

import com.edu.dao.CustomerRepository;
import com.edu.dao.StudentRepository;
import com.edu.dao.VerifyCodeRepository;
import com.edu.domain.Customer;
import com.edu.domain.Student;
import com.edu.domain.VerifyCode;
import com.edu.domain.dto.ChildContainer;
import com.edu.domain.dto.CustomerContainer;
import com.edu.utils.Constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.List;

@RestController
public class CustomerController {
	@Autowired
	private CustomerRepository repository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private VerifyCodeRepository verifyCodeRepository;

	public static final String PATH = "/api/v1/Customer";
	
	public static final String DETAIL_PATH = PATH + "/Detail";

	public static final String SIGNUP_PATH = PATH + "/SignUp";
	
	public static final String ADD_CHILD_PATH = PATH + "/AddChild";

	@GetMapping(path = DETAIL_PATH)
	public ResponseEntity<CustomerContainer> getCustomer(HttpSession session) {
		String openId = (String) session.getAttribute(Constant.SESSION_OPENID_KEY);
		Customer customer = repository.findOneByOpenCode(openId);
		return new ResponseEntity<>(new CustomerContainer(customer), HttpStatus.OK);
	}

	@PostMapping(path = SIGNUP_PATH)
	public ResponseEntity<Customer> create(@RequestBody @Valid CustomerContainer customerDTO) {
		Customer customer;
		if (repository.isCustomerAlreadyRegistered(customerDTO.getOpenCode())) {
			customer = repository.findOneByOpenCode(customerDTO.getOpenCode());
		} else {
			/**
			 * 没有填写验证码
			 */
			Long id = customerDTO.getVerifyCodeId();
			if (null == id) {
				return new ResponseEntity<Customer>(HttpStatus.BAD_REQUEST);
			}

			/**
			 * 验证码过期或者没有找到
			 */
			VerifyCode verifyCode = verifyCodeRepository.findOneVerifyCodeById(id);

			if (null == verifyCode) {
				return new ResponseEntity<Customer>(HttpStatus.BAD_REQUEST);
			}

			if (!verifyCode.getCode().equals(customerDTO.getVerifyCode())) {
				return new ResponseEntity<Customer>(HttpStatus.BAD_REQUEST);
			}

			customer = new Customer(customerDTO.getOpenCode(), customerDTO.getName(), customerDTO.getMobilePhone(),
					customerDTO.getAddress());
			customer = repository.save(customer);

			for (ChildContainer childDTO : customerDTO.getChildren()) {
				Student student = new Student(childDTO.getChildName(), childDTO.getBirthday(), childDTO.getGender());
				student.setCustomer(customer);
				student = studentRepository.save(student);
				customer.addStudent(student);
			}
		}
		return new ResponseEntity<Customer>(customer, HttpStatus.OK);
	}

	@PostMapping(path = ADD_CHILD_PATH)
	public Customer addChild(@RequestBody @Valid List<ChildContainer> children, HttpSession session) {
		Customer customer = null;

		String openId = (String) session.getAttribute(Constant.SESSION_OPENID_KEY);

		if (openId != null) {
			customer = repository.findOneByOpenCode(openId);

			if (customer != null) {
				for (ChildContainer childDTO : children) {
					Student student = new Student(childDTO.getChildName(), childDTO.getBirthday(),
							childDTO.getGender());
					student.setCustomer(customer);
					student = studentRepository.save(student);
					customer.addStudent(student);
				}
			}
		}

		return customer;
	}

}
