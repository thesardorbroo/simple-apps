package uz.sardorbroo.apartments.domain;

import java.util.Objects;

public class Apartment {

    private Long id;
    private String city;
    private String region;
    private String type;
    private Integer roomsCount;
    private Integer price;

    public Apartment() {
    }

    public Apartment(Long id, String city, String region, String type, Integer roomsCount, Integer price) {
        this.id = id;
        this.city = city;
        this.region = region;
        this.type = type;
        this.roomsCount = roomsCount;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRoomsCount() {
        return roomsCount;
    }

    public void setRoomsCount(Integer roomsCount) {
        this.roomsCount = roomsCount;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", type='" + type + '\'' +
                ", roomsCount=" + roomsCount +
                ", price=" + price +
                '}';
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Apartment)) return false;

        Apartment apartment = (Apartment) obj;
        return Objects.equals(this.getId(), apartment.getId());
    }
}
