package com.hitran.anti.patterns.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hitran.anti.patterns.model.Customer;
import com.hitran.anti.patterns.model.Product;

@Component
public class CustomerRepository {

	public static List<Customer> customers;

	static {
		LocalDate now = LocalDate.now();
		LocalDate yesterday = LocalDate.now().minusDays(1L);
		LocalDate tomorrow = LocalDate.now().plusDays(1L);
		LocalDate previousMonth = LocalDate.now().minusMonths(1L);
		LocalDate nextMonth = LocalDate.now().plusMonths(1L);

		customers = new ArrayList<Customer>();
		customers.add(new Customer("Jim Carrey", BigDecimal.valueOf(10000),
				new Product("Comedy", previousMonth, now, nextMonth, 30, BigDecimal.valueOf(1000))));
		customers.add(new Customer("Tom Hardy", BigDecimal.valueOf(100),
				new Product("Wrestke", previousMonth, tomorrow, yesterday, 30, BigDecimal.valueOf(500))));
		customers.add(new Customer("Tina Tennings", BigDecimal.valueOf(500),
				new Product("Fashion", previousMonth, now, nextMonth, 30, BigDecimal.valueOf(200))));
		customers.add(new Customer("Harry James", BigDecimal.valueOf(50),
				new Product("Stuff", previousMonth, yesterday, nextMonth, 30, BigDecimal.valueOf(200))));
		customers.add(new Customer("Jerry Leeds", BigDecimal.valueOf(1001),
				new Product("Cool Paper", now, nextMonth, nextMonth.minusDays(1L), 30, BigDecimal.valueOf(100))));
		customers.add(new Customer("Kevin Toms", BigDecimal.valueOf(50), null));
	}

	public void save(Customer customer) {
		// this is outside of scope (save in DB)
	}

	public List<Customer> getAllCustomersForNotification() {
		// This is representation of sample SELECT statement
		List<Customer> filtered = customers.stream().filter(
				c -> c.getProduct() != null && c.getProduct().getNotificationDate().compareTo(LocalDate.now()) <= 0)
				.collect(Collectors.toList());
		return filtered;
	}

	public List<Customer> getAllCustomersForRenew() {
		// This is representation of sample SELECT statement
		List<Customer> filtered = customers.stream()
				.filter(c -> c.getProduct() != null && c.getProduct().getRenewDate().compareTo(LocalDate.now()) <= 0)
				.collect(Collectors.toList());
		return filtered;
	}

}
