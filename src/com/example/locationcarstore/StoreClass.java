package com.example.locationcarstore;

public class StoreClass {

    public int sId;
    public String city;
    public String name;
    public String qualifi;
    public String addr;
    public String tel;
    public String credit;
    public String is4s;
    public String major;
    public int lat;
    public int lng;
    public double distance;

    StoreClass() {

    }

    StoreClass(int sId, String city, String name, String qualifi, String addr, String tel,
            String is4s, String major, int lat, int lng) {
        this.sId = sId;
        this.city = city;
        this.name = name;
        this.qualifi = qualifi;
        this.addr = addr;
        this.tel = tel;
        this.is4s = is4s;
        this.major = major;
        this.lat = lat;
        this.lng = lng;
    }

    public Integer getDistanceOrder() {
        return Integer.valueOf((int) (distance * 1E6));
    }

    public Integer get4SOrder() {
        return is4s.equals("ÊÇ") ? Integer.valueOf(0) : Integer.valueOf(1);
    }

    public Integer getCreditOrder() {
        int tmp = 4;
        if (credit.equals("AAA")) {
            tmp = 1;
        } else if (credit.equals("AA")) {
            tmp = 2;
        } else if (credit.equals("A")) {
            tmp = 3;
        }

        return Integer.valueOf(tmp);
    }

}
