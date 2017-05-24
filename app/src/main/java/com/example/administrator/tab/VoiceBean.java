package com.example.administrator.tab;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/28.
 * 作用是：
 */
public class VoiceBean {
    public ArrayList<WSBean> ws;
    public  class  WSBean{
        public ArrayList<CWBean> cw;
    }
    public class CWBean{
        public String w;

    }
}
