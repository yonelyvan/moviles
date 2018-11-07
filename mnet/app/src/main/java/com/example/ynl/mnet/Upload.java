package com.example.ynl.mnet;

public class Upload {
    private String mName;
    private String mImageUrl;

    public Upload(){
    }

    public  Upload(String name, String imageUrl){
        if(name.trim().equals("")){//eliminar espacion al inicio y final de un string
            name="NULL";
        }
        mName = name;
        mImageUrl = imageUrl;
    }

    public void setName(String name){
        mName = name;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }

    public String getName(){
        return mName;
    }

    public String getImageUrl(){
        return mImageUrl;
    }
}
