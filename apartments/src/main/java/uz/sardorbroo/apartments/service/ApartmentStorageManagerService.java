package uz.sardorbroo.apartments.service;

import uz.sardorbroo.apartments.domain.Apartment;
import uz.sardorbroo.apartments.service.enumeration.Sort;

import java.util.List;
import java.util.Optional;

public interface ApartmentStorageManagerService {

    List<Apartment> getAll();

    void save(Apartment apartment);

    Optional<Apartment> remove(Long id);

    Optional<Apartment> update(Apartment updatedApartment);

    Optional<Apartment> find(Long id);

    void sortBy(Sort type);
}
