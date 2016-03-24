package com.itba.edu.ar.model;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class Subcategory implements Parcelable {
	
	private Integer id;
	private String name;
	HashMap<String, String> attributes;

	public Subcategory() {}
	
	public Subcategory(Parcel in) {
		readFromParcel(in);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
	    public Subcategory createFromParcel(Parcel in) {
	        return new Subcategory(in);
	    }

	    public Subcategory[] newArray(int size) {
	        return new Subcategory[size];
	    }
	};
	
	private void readFromParcel(Parcel in) {
	    id = in.readInt();
	    name = in.readString();
	    attributes = in.readHashMap(HashMap.class.getClassLoader());
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String getAttribute(String attr) {
		if (attributes != null && attributes.containsKey(attr)) {
			return attributes.get(attr);
		}
		return "";
	}

	public void addAttribute(Attribute attr) {
		if (this.attributes == null) {
			attributes = new HashMap<String, String>();
		}
		attributes.put(attr.getName(), attr.getValues());
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
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeInt(id);
	    dest.writeString(name);
	    dest.writeMap(attributes);
	}
}
