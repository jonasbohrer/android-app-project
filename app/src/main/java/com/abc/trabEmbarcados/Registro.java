package com.abc.trabEmbarcados;

import java.io.Serializable;
import java.util.Date;

public class Registro implements Serializable{
    public String date;
    public String name;
    public Integer quantity;
    public String calories;

    Registro(String date, String name, Integer quantity, String calories) {
        this.date = date;
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
    }
}