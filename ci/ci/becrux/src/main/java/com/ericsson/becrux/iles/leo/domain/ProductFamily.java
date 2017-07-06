package com.ericsson.becrux.iles.leo.domain;

public class ProductFamily {

    public static final ProductFamily SOFTWARE = new ProductFamily(1l, "Software");
    public static final ProductFamily HARDWARE = new ProductFamily(2l, "Hardware");
    public static final ProductFamily OTHER = new ProductFamily(3l, "Other");
    public static final ProductFamily PLATFORM = new ProductFamily(4l, "Platform");

    public long id;
    public String name;

    private ProductFamily(long id, String name) {
        this.id = id;
        this.name = name;
    }

}
