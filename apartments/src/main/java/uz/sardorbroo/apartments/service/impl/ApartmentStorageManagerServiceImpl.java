package uz.sardorbroo.apartments.service.impl;

import uz.sardorbroo.apartments.domain.Apartment;
import uz.sardorbroo.apartments.service.ApartmentStorageManagerService;
import uz.sardorbroo.apartments.service.ApartmentStorageService;
import uz.sardorbroo.apartments.service.enumeration.Sort;
import uz.sardorbroo.apartments.service.utils.ApartmentConverter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApartmentStorageManagerServiceImpl implements ApartmentStorageManagerService {

    private final ApartmentStorageService apartmentStorageService;

    public ApartmentStorageManagerServiceImpl(ApartmentStorageService apartmentStorageService) {
        this.apartmentStorageService = apartmentStorageService;
    }

    @Override
    public List<Apartment> getAll() {
        return apartmentStorageService.readAll().stream()
                .map(ApartmentConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Apartment apartment) {
        List<Apartment> apartments = getAll();

        apartments.add(apartment);

        apartmentStorageService.write(apartments);
    }

    @Override
    public Optional<Apartment> remove(Long id) {
        List<Apartment> apartments = getAll();

        Optional<Apartment> apartmentOptional = apartments.stream()
                .filter(apartment -> Objects.equals(id, apartment.getId()))
                .findFirst();

        if (apartmentOptional.isEmpty()) {
            return Optional.empty();
        }

        apartments.remove(apartmentOptional.get());

        apartmentStorageService.write(apartments);

        return apartmentOptional;
    }

    @Override
    public Optional<Apartment> update(Apartment updatedApartment) {

        Optional<Apartment> apartmentOptional = find(updatedApartment.getId());

        if (apartmentOptional.isEmpty()) return Optional.empty();

        remove(apartmentOptional.get().getId());
        save(updatedApartment);

        return Optional.of(updatedApartment);
    }

    @Override
    public Optional<Apartment> find(Long id) {

        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("Invalid argument is passed! ID must not be null!");
        }

        return getAll().stream()
                .filter(apartment -> Objects.equals(id, apartment.getId()))
                .findFirst();
    }

    @Override
    public void sortBy(Sort type) {

        List<Apartment> sortedApartments = getAll().stream()
                .sorted(type.getComparator())
                .toList();

        apartmentStorageService.write(sortedApartments);
    }
}
