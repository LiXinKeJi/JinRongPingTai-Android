package com.finance.client.adapter;


import com.yhrun.alchemy.View.wheel.WheelAdapter;

import java.util.List;

public class ArrayWheelAdapter implements WheelAdapter {
	
	/** The default items length */
	public static final int DEFAULT_LENGTH = -1;
	
	// items
	private List<String> items;
	// length
	private int length;

	/**
	 * Constructor
	 * @param items the items
	 * @param length the max items length
	 */
	public ArrayWheelAdapter(List<String> items, int length) {
		this.items = items;
		this.length = length;
	}
	
	/**
	 * Contructor
	 * @param items the items
	 */
	public ArrayWheelAdapter(List<String> items) {
		this(items, DEFAULT_LENGTH);
	}

	@Override
	public String getItem(int index) {
		if (index >= 0 && index < items.size()) {
			String str = items.get(index);
			if(str.length() > 3){
				return str.substring(0,3);
			}
			return items.get(index);
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		return items.size();
	}

	@Override
	public int getMaximumLength() {
		return length;
	}

}
