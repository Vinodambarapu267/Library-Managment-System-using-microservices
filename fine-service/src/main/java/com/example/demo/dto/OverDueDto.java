package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverDueDto {
	private Long loanId;
	private Integer overDueDays;
	private Double totalAmount;

	public Double calculateDailyFine() {
		return totalAmount * 0.05 * overDueDays;
	}
}
