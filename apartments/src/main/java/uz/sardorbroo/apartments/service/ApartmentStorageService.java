package uz.sardorbroo.apartments.service;

import uz.sardorbroo.apartments.domain.Apartment;

import java.util.List;

public interface ApartmentStorageService {

    void write(List<Apartment> apartments);

    List<String> readAll();
}
