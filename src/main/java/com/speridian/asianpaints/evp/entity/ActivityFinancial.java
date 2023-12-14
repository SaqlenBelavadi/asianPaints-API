package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_ACTIVITY_FINANCIAL" )
public class ActivityFinancial extends AbstractEntity {

	@Column(name="MATERIAL_OR_CREATIVE_EXPENSE")
	private String materialOrCreativeExpense;

	@Column(name="LOGISTIC_EXPENSE")
	private String logisticExpense;

	@Column(name="GRATIFICATION_EXPENSE")
	private String gratificationExpense;

	@Column(name="OTHER_EXPENSE")
	private String otherExpense;

	@Column(name="ACTUAL_MATERIAL_EXPENSE")
	private String actualMaterialExpense;

	@Column(name="ACTUAL_LOGISTIC_EXPENSE")
	private String actualLogisticExpense;

	@Column(name="ACTUAL_GRATIFICATION_EXPENSE")
	private String actualGratificationExpense;

	@Column(name="ACTUAL_OTHER_EXPENSE")
	private String actualOtherExpense;
	
	

}
