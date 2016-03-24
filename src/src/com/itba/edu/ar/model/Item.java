package com.itba.edu.ar.model;

import java.util.ArrayList;
import java.util.List;

public class Item {
	List<Product> products = new ArrayList<Product>();
	Integer quantity;
	Integer price;
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public void setProduct(Product product) {
		this.products.add(product);
	}
	public List<Product> getProducts() {
		return products;
	}
	
	
}
