package com.safechat.ynl.safechat;

public class User {
    private String id;
    private String name;
    private String email;
    private String img_url;

    public User(){
    }

    public User(String u_id, String u_name, String u_email, String u_img_url){
        id = u_id;
        name = u_name;
        email = u_email;
        img_url= u_img_url;
    }



    public void set_id(String u_id ){ id = u_id; }
    public void set_name(String u_name ){ name = u_name; }
    public void set_email(String u_email ){ email = u_email; }
    public void set_img_url(String u_img_url ){ img_url = u_img_url; }


    public String get_id(){return id;}
    public String get_name(){return name;}
    public String get_email(){return email;}
    public String get_img_url(){return img_url;}


}
