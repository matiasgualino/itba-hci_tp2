package com.itba.edu.ar.model;

public class Attribute {

	private String name;
	//Deberia ser un array
	private String values;

	public Attribute(String name, String values) {
		this.name = name;
		this.values = values;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValues() {
		return values;
	}
	
}
