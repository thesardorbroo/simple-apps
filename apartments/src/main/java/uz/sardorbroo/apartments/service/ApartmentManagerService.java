package uz.sardorbroo.apartments.service;

import uz.sardorbroo.apartments.domain.Apartment;

import java.util.Optional;

public interface ApartmentManagerService {

    Optional<Apartment> get();

    Optional<Apartment> create();

    Optional<Apartment> update();

    Optional<Apartment> delete();
}
