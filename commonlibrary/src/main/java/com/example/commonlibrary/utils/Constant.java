package com.example.commonlibrary.utils;

import com.example.commonlibrary.BuildConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Created by COOTEK on 2017/7/31.
 */


public class Constant {
    //    默认Base URL
    //    8990正式服端口、、、、、、、、8992测试服端口

    public static final String BASE_URL = BuildConfig.DEBUG ? "http://www.baidu.com" : "http://www.baidu.com";
    public static final String DESIGNED_WIDTH = "designed_width";
    public static final String DESIGNED_HEIGHT = "designed_height";
    public static final String TOKEN = "user_token";
    //    token过期code
    public static final List<Integer> tokenCodeList = Arrays.asList(402001, 402002, 402003);
    public static final String HEADER = "Authorization";
}
