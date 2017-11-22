package com.finance.client.wheel;

import java.util.List;

/**
 * Created by Slingge on 2017/9/6 0006.
 */

public class CityBean {

    public String areaId;
    public String areaName;
    public List<citiesBean> cities;

    public class citiesBean {
        public String areaId;
        public String areaName;
        public List<countiesBean> counties;
    }

    public class countiesBean {
        public String areaId;
        public String areaName;
    }


}
