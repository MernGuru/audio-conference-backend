package com.brh.entities.cores;

import com.brh.entities.bases.SimplePKEntity;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "countries")
@AttributeOverride(name = "id", column = @Column(name = "country_id",columnDefinition = "SMALLINT UNSIGNED"))
@SuppressWarnings("unused")
public class Country extends SimplePKEntity<Integer> {
    @Basic
    @Column(name = "country")
    private String country;

    @XmlTransient
    @JsonbTransient
    @OneToMany(mappedBy = "country",fetch = FetchType.EAGER)
    private final Set<City> cities = new HashSet<>();

    public Country(){
        super();
    }

    public Country(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    Set<City> getCities() {
        return cities;
    }

    public void addCity(City city) {
        city.setCountry(this);
    }

    public void removeCity(City city) {
        city.setCountry(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Country country1 = (Country) o;
        return country.equals(country1.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), country);
    }

    @Override
    public String toString() {
        return "{\"super\":" + super.toString() +
                ",\"country\":\"" + country + "\"}";
    }
}