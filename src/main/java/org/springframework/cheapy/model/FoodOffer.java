/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cheapy.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "food_offers")
public class FoodOffer extends Offer {
	
	private static final long serialVersionUID = 1L;

	//Plato específico
	@NotBlank(message = "Debe rellenar la comida a ofertar")
	private String food;

	@NotNull(message = "Debe rellenar el descuento que proporciona")
	@Range(min = 0, max = 100, message = "El descuento debe estar entre 0 y 100 %")
	private Integer discount;

	public String getFood() {
		return food;
	}

	public void setFood(String food) {
		this.food = food;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

}