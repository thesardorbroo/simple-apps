package uz.sardorbroo.apartments.service;

import uz.sardorbroo.apartments.domain.Apartment;

import java.util.List;
import java.util.function.Consumer;

public interface ApartmentPrinterService {

    void print(List<Apartment> apartments);

    void print(Apartment apartment);

    void print(String message);

    void print(String message, Consumer<String> consumer);
}
