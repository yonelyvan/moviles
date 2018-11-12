package com.example.ynl.mnet;

import java.util.Date;

public class Post {
    private String user_id;
    private String img_url;
    private long unix_time;
    private String unix_time_str;
    private String comment;

    public Post(){
    }

    public  Post(String m_user_id, String m_img_url, long m_unix_time, String m_comment){
        user_id = m_user_id;
        img_url = m_img_url;
        unix_time = m_unix_time;
        unix_time_str = Long.toString(m_unix_time);
        comment = m_comment;
    }

    public void set_user_id(String m_user_id){
        user_id = m_user_id;
    }

    public void set_img_url(String m_img_url){
        img_url = m_img_url;
    }

    public void set_date(long m_unix_time){
        unix_time = m_unix_time;
    }

    public void set_date(String m_unix_time_str){
        unix_time_str = m_unix_time_str;
    }

    public  void set_comment(String m_comment){
        comment = m_comment;
    }

    public  String get_user_id(){
        return user_id;
    }

    public String get_img_url(){
        return img_url;
    }

    public  long get_unix_time(){
        return unix_time;
    }

    public String getUnix_time_str(){ return unix_time_str;}

    public String get_comment(){
        return comment;
    }

}
