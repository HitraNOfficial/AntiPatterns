package com.hitran.anti.patterns.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	private String name;
	private LocalDate purchaseDate;
	private LocalDate renewDate;
	private LocalDate notificationDate;
	private Integer renewPeriod;
	private BigDecimal renewPrice;
}
