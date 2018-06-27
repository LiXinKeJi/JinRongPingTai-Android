package com.finance.client.model;


import java.io.Serializable;
import java.util.List;

/**
 * Created by Slingge on 2018/6/23 0023.
 */

public class StartImageModel implements Serializable{

    public String result;
    public String resultNote;
    public String image;
    public String url;
    public List<imgBean> imgList;

    public class imgBean implements Serializable{
        public String image;
        public String url;
    }

}
