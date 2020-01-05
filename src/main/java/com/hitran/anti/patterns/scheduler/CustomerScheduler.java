package com.hitran.anti.patterns.scheduler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hitran.anti.patterns.data.CustomerRepository;
import com.hitran.anti.patterns.model.Customer;
import com.hitran.anti.patterns.model.Product;

@Component
public class CustomerScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerScheduler.class);

	private final CustomerRepository repository;

	public CustomerScheduler(CustomerRepository repository) {
		this.repository = repository;
	}

	@Scheduled(cron = "${notification-cron: 0 0/1 * * * *}")
	public void renewCustomer() {
		// Extract all renew of products
		List<Customer> renewList = repository.getAllCustomersForRenew();
		// Execute the renew
		processCustomerRenew(renewList, renewList != null && !renewList.isEmpty() ? renewList.size() : 0);

		// Extract all the notification data
		List<Customer> notificationList = repository.getAllCustomersForNotification();
		// Execute notifications
		processCustomerNotification(notificationList,
				notificationList != null && !notificationList.isEmpty() ? notificationList.size() : 0);

	}

	private void processCustomerRenew(List<Customer> renewList, int j) {

		// break statement for the recursive method
		if (j == 0) {
			return;
		}

		Customer customer = renewList.get(renewList.size() - j);

		Product currentProduct = customer.getProduct();
		String productName = currentProduct.getName();
		String customerName = customer.getName();

		// Logging message for easier debugging
		LOGGER.info("Started renew of product {} for customer {}", productName, customerName);

		BigDecimal balance = customer.getBalance();
		BigDecimal renewPrice = currentProduct.getRenewPrice();
		
		boolean isValid = balance.compareTo(renewPrice) >= 0;
		if (isValid) {
			// Executing renew of product if balance is enough
			executeRenewProduct(customer, currentProduct, balance, renewPrice);
		} else {
			customer.setProduct(null);
		}

		repository.save(customer);

		processCustomerRenew(renewList, j - 1);
	}

	private void executeRenewProduct(Customer customer, Product currentProduct, BigDecimal balance,
			BigDecimal renewPrice) {
		Integer renewalPeriod = currentProduct.getRenewPeriod();
		customer.setBalance(balance.subtract(renewPrice));
		currentProduct.setRenewDate(LocalDate.now().plusDays(Long.valueOf(renewalPeriod.longValue())));
	}

	private void processCustomerNotification(List<Customer> notificationList, int i) {

		// break statement for the recursive method
		if (i == 0) {
			return;
		}

		Customer customer = notificationList.get(notificationList.size() - i);

		// Declarations of all needed local variables
		Product currentProduct = customer.getProduct();
		String customerName = customer.getName();
		BigDecimal customerBalance = customer.getBalance();
		BigDecimal renewPrice = currentProduct.getRenewPrice();
		LocalDate renewDate = currentProduct.getRenewDate();
		Integer renewPeriod = currentProduct.getRenewPeriod();

		String notificationMessage = generateNotificationMessage(customerName, customerBalance, renewPrice, renewDate);

		sendNotification(notificationMessage);

		// Updating for next notification of the current product
		currentProduct.setNotificationDate(LocalDate.now().plusDays(Long.valueOf(renewPeriod.longValue())));

		// Save customer in DB
		repository.save(customer);

		processCustomerNotification(notificationList, i - 1);
	}

	private void sendNotification(String notificationMessage) {
		// check for proper values before notifying the client
		if (notificationMessage == null || notificationMessage.length() == 0) {
			throw new RuntimeException("Not able to generate notification message!");
		}

		LOGGER.info(notificationMessage);
	}

	private String generateNotificationMessage(String customerName, BigDecimal customerBalance, BigDecimal renewPrice,
			LocalDate renewDate) {
		// Please do not change this, there was known bug with
		// simple string concatenation
		StringBuilder sb = new StringBuilder();
		sb.append("Hello, ");
		sb.append(customerName);
		sb.append("! Your current balance is = ");
		sb.append(customerBalance);
		sb.append(", your product costs = ");
		sb.append(renewPrice);
		sb.append(" your payment date is ");
		sb.append(renewDate);
		sb.append("! Please make sure you have enough balance!");

		// validate proper string before returning it
		return sb != null && sb.toString().length() > 0 ? sb.toString() : "";
	}

}
