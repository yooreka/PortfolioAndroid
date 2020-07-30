package com.example.portfolio;

public class Shop {
    public int shopid;
    public String shopname;
    public String	businesshour;
    public String	mobile;
    public String	roadaddress;
    public String	address;

    @Override
    public String toString() {
        return "Shop{" +
                "shopid=" + shopid +
                ", shopname='" + shopname + '\'' +
                ", businesshour='" + businesshour + '\'' +
                ", mobile='" + mobile + '\'' +
                ", roadaddress='" + roadaddress + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
