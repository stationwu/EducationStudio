package com.edu.web.rest;

import com.edu.dao.CustomerRepository;
import com.edu.domain.Customer;
import com.edu.domain.Student;
import com.edu.domain.dto.ChildContainer;
import com.edu.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class StudentController {
	@Autowired
	private CustomerRepository repository;

	public static final String PATH = "/api/v1/Students";

	@GetMapping(path = PATH)
	public ResponseEntity<List<ChildContainer>> show(HttpSession session) {
		String openId = (String) session.getAttribute(Constant.SESSION_OPENID_KEY);
		Customer customer = repository.findOneByOpenCode(openId);
		Set<Student> students = customer.getStudents();
		List<ChildContainer> childContainers = students.stream().map(x -> new ChildContainer(x))
				.collect(Collectors.toCollection(ArrayList::new));
		return new ResponseEntity<>(childContainers, HttpStatus.OK);
	}

}
