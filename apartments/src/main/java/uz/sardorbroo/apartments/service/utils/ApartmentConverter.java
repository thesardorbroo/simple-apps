package uz.sardorbroo.apartments.service.utils;

import uz.sardorbroo.apartments.domain.Apartment;

import java.util.Objects;

public class ApartmentConverter {
    private final static Integer APARTMENT_PIECES_COUNT = 6;
    private final static String REGEX = ";";

    public static Apartment convert(String apartmentAsString) {

        if (Objects.isNull(apartmentAsString) || apartmentAsString.isEmpty() || apartmentAsString.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid argument is passed! String Apartment instance must not be null!");
        }

        String[] pieces = apartmentAsString.split(REGEX);
        if (!Objects.equals(APARTMENT_PIECES_COUNT, pieces.length)) {
            throw new IllegalArgumentException("Pieces of string is not matched to pieces of Apartment!");
        }

        Apartment instance = new Apartment();
        instance.setId(Long.parseLong(pieces[0]));
        instance.setCity(pieces[1]);
        instance.setRegion(pieces[2]);
        instance.setType(pieces[3]);
        instance.setRoomsCount(Integer.parseInt(pieces[4]));
        instance.setPrice(Integer.parseInt(pieces[4]));

        return instance;
    }

    public static String convert(Apartment apartment) {

        if (Objects.isNull(apartment)) {
            throw new IllegalArgumentException("Invalid argument is passed! Apartment instance must not be null!");
        }

        return apartment.getId() + REGEX +
                apartment.getCity() + REGEX +
                apartment.getRegion() + REGEX +
                apartment.getType() + REGEX +
                apartment.getRoomsCount() + REGEX +
                apartment.getPrice();
    }
}
