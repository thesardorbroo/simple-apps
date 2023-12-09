package uz.sardorbroo.apartments.service.enumeration;

import uz.sardorbroo.apartments.domain.Apartment;

import java.util.Comparator;

public enum Sort {

    ASC((o1, o2) -> o1.getId() > o2.getId() ? 1 : -1),

    DESC((o1, o2) -> o1.getId() > o2.getId() ? -1 : 1);

    private final Comparator<Apartment> comparator;

    Sort(Comparator<Apartment> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Apartment> getComparator() {
        return this.comparator;
    }
}
