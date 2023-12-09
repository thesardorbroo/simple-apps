package uz.sardorbroo.apartments.service.impl;

import uz.sardorbroo.apartments.domain.Apartment;
import uz.sardorbroo.apartments.service.ApartmentManagerService;
import uz.sardorbroo.apartments.service.ApartmentPrinterService;
import uz.sardorbroo.apartments.service.ApartmentStorageManagerService;
import uz.sardorbroo.apartments.service.utils.ScannerUtils;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Scanner;

public class ApartmentManagerServiceImpl implements ApartmentManagerService {
    private final static Scanner SCANNER = new Scanner(System.in);
    private final ApartmentStorageManagerService storageManager;
    private final ApartmentPrinterService printer;
    private Long apartmentSequence = 1L;

    public ApartmentManagerServiceImpl(ApartmentStorageManagerService storageManager, ApartmentPrinterService printer) {
        this.storageManager = storageManager;
        this.printer = printer;
        setMaxId();
    }

    public Optional<Apartment> get() {

        List<Apartment> apartments = storageManager.getAll();

        printer.print(apartments);

        printer.print("\n\nPlease input ID of apartment: ");

        long apartmentId = ScannerUtils.scan(SCANNER::nextLong, 0L);

        return find(apartmentId);
    }

    public Optional<Apartment> create() {

        Apartment apartment = createApartmentInstance();

        storageManager.save(apartment);

        return Optional.of(apartment);
    }

    public Optional<Apartment> update() {

        List<Apartment> apartments = storageManager.getAll();

        printer.print(apartments);

        printer.print("\n\nPlease input ID of apartment: ");

        long apartmentId = ScannerUtils.scan(SCANNER::nextLong, 0L);

        Optional<Apartment> apartmentOptional = find(apartmentId);

        if (apartmentOptional.isEmpty()) return Optional.empty();

        Apartment updatedApartment = createApartmentInstance();
        updatedApartment.setId(apartmentId);

        storageManager.update(updatedApartment);

        printer.print("Apartment is updated successfully:\n" + updatedApartment);
        return Optional.of(updatedApartment);
    }

    public Optional<Apartment> delete() {

        List<Apartment> apartments = storageManager.getAll();

        printer.print(apartments);

        printer.print("\n\nPlease input ID of apartment: ");

        long apartmentId = ScannerUtils.scan(SCANNER::nextLong, 0L);

        Optional<Apartment> apartmentOptional = find(apartmentId);

        if (apartmentOptional.isEmpty()) return Optional.empty();

        storageManager.remove(apartmentId);

        printer.print("Apartment is deleted successfully! ID: " + apartmentId);
        return apartmentOptional;
    }

    private Optional<Apartment> find(Long id) {

        Optional<Apartment> apartmentOptional = storageManager.find(id);
        if (apartmentOptional.isEmpty()) {
            printer.print("Apartment is not found! ID: " + id, System.err::println);
        } else {
            printer.print(apartmentOptional.get());
        }

        return apartmentOptional;
    }

    private Apartment createApartmentInstance() {

        printer.print("Please enter the city: ");
        String city = ScannerUtils.scan(SCANNER::next, "");

        printer.print("Please enter the region: ");
        String region = ScannerUtils.scan(SCANNER::next, "");

        printer.print("Please enter the type of apartment: ");
        String type = ScannerUtils.scan(SCANNER::next, "");

        printer.print("Please enter the count of rooms: ");
        Integer roomsCount = ScannerUtils.scan(SCANNER::nextInt, 0);

        printer.print("Please enter the price: ");
        Integer price = ScannerUtils.scan(SCANNER::nextInt, 0);

        return new Apartment(apartmentSequence++, city, region, type, roomsCount, price);
    }

    private void setMaxId() {

        OptionalLong maxIdOptional = storageManager.getAll().stream()
                .mapToLong(apartment -> Long.parseLong(String.valueOf(apartment.getId())))
                .max();

        apartmentSequence = maxIdOptional.isEmpty() ? 1L : maxIdOptional.getAsLong() + 1L;
    }
}
