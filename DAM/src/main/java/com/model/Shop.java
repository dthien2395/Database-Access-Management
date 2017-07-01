package com.model;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by dthien on 6/30/2017.
 */

@Entity
@Table(name = "shops")
@XmlRootElement
public class Shop {

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;

    @Column(name = "emplNumber")
    private Integer emplNumber;

    @Column(name = "name")
    private String name;


    public Shop(){

    }
    public Shop(int id, int emplNumber, String name){
        this.id = id;
        this.emplNumber = emplNumber;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getEmplNumber() {
        return emplNumber;
    }

    public void setEmplNumber(int emplNumber) {
        this.emplNumber = emplNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", emplNumber='" + emplNumber + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
