package com.example.y.libb;

public class ConcretePrototype extends Prototype {

    @Override
    public Object my_clone(){
          ConcretePrototype obj2 = new ConcretePrototype();
          obj2.setUsername(this.getUsername());
          obj2.setUrl(this.getUrl());
          obj2.setRepositorios(this.getRepositorios());
        obj2.setUpdate(this.getUpdates());
        return obj2;

    }


}
