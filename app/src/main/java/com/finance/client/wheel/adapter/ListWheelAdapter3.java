/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.finance.client.wheel.adapter;

import android.content.Context;


import com.finance.client.wheel.CityBean;

import java.util.List;

/**
 * The simple Array wheel adapter
 */
public class ListWheelAdapter3 extends AbstractWheelTextAdapter {

    // items
    private List<CityBean.countiesBean> CategoryList;

    /**
     * Constructor
     *
     * @param context      the current context
     * @param CategoryList the list
     */
    public ListWheelAdapter3(Context context, List<CityBean.countiesBean> CategoryList) {
        super(context);

        //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.CategoryList = CategoryList;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < CategoryList.size()) {
            String item = CategoryList.get(index).areaName;
            if (item instanceof CharSequence) {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return CategoryList.size();
    }
}
