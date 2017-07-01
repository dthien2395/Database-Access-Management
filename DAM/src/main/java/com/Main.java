package com;

import com.MyDAM.MySQLRepository;
import com.MyDAM.ShopRepository;

/**
 * Created by dthien on 6/30/2017.
 */
public class Main {
    public static void main(String[] params) {
        ShopRepository shopRepository = new ShopRepository();
        System.out.println(shopRepository.findAll());
//        System.out.println(shopRepository.findById(7L));
    }
}
