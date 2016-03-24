package com.itba.edu.ar.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class Product implements Parcelable {
	private Integer id;
	String name;
	Integer price;
	List<String> imageUrl;
	Map<String, String> attributes;
	Category category;
	Subcategory subcategory;

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Product createFromParcel(Parcel in) {
			return new Product(in);
		}

		public Product[] newArray(int size) {
			return new Product[size];
		}
	};

	public Product() {
	}

	public Product(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		id = in.readInt();
		name = in.readString();
		price = in.readInt();
		imageUrl = in.readArrayList(List.class.getClassLoader());
		attributes = in.readHashMap(HashMap.class.getClassLoader());
		category = in.readParcelable(Category.class.getClassLoader());
		subcategory = in.readParcelable(Subcategory.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(price);
		dest.writeList(imageUrl);
		dest.writeMap(attributes);
		dest.writeParcelable(category, PARCELABLE_WRITE_RETURN_VALUE);
		dest.writeParcelable(subcategory, PARCELABLE_WRITE_RETURN_VALUE);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Subcategory getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(Subcategory subcategory) {
		this.subcategory = subcategory;
	}

	public void addImageUrl(String url) {
		if (this.imageUrl == null) {
			imageUrl = new ArrayList<String>();
		}
		imageUrl.add(url);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String getAttribute(String attr) {
		if (attributes != null && attributes.containsKey(attr)) {
			return attributes.get(attr);
		}
		return null;
	}

	public List<String> getImageUrl() {
		return imageUrl;
	}

	public void addAttribute(Attribute attr) {
		if (this.attributes == null) {
			attributes = new HashMap<String, String>();
		}
		attributes.put(attr.getName(), attr.getValues());
	}
}
