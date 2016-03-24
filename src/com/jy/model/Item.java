package com.jy.model;


public class Item {
	private String id;
	private String name;
	private String item_link;
	private String image_link;
	private String price;
	private String brand_name;
	private String color;
	private String category_tree;
	
	private Item(){};

	public Item(String name, String item_link, String image_link, String price) {
		this.name = name;
		this.item_link = item_link;
		this.image_link = image_link;
		this.price = price;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItem_link() {
		return item_link;
	}

	public void setItem_link(String item_link) {
		this.item_link = item_link;
	}

	public String getImage_link() {
		return image_link;
	}


	public void setImage_link(String image_link) {
		this.image_link = image_link;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getCategory_tree() {
		return category_tree;
	}

	public void setCategory_tree(String category_tree) {
		this.category_tree = category_tree;
	}

}
