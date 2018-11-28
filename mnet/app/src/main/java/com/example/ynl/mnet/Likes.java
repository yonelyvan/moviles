package com.example.ynl.mnet;

public class Likes {
    private String user_id;
    private String estado;

    public Likes(){
    }

    public  Likes(String m_user_id, String m_estado){
        user_id = m_user_id;
        estado = m_estado;
    }

    public void set_user_id(String m_user_id){ user_id = m_user_id; }
    public void set_estado(String m_estado){ estado = m_estado; }

    public String get_user_id(){
        return user_id;
    }
    public String get_estado(){
        return estado;
    }


}
