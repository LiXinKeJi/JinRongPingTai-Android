package com.finance.client.util;

import com.finance.client.model.ChangeInfoDao;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class DateComparator implements Comparator<ChangeInfoDao> {

	public int compare(ChangeInfoDao o1, ChangeInfoDao o2) {

		int flag = o2.getTime().compareTo(o1.getTime());
		return flag;
	}

}
