package com.brh.entities.cores;

import com.brh.entities.bases.SimplePKEntity;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "cities")
@AttributeOverride(name = "id", column = @Column(name = "city_id",columnDefinition = "SMALLINT UNSIGNED"))
@SuppressWarnings("unused")
public class City extends SimplePKEntity<Integer> {
    @Basic
    @Column(name = "city", columnDefinition = "VARCHAR(64)",nullable = false)
    private String city;

    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @JoinColumn(name = "country_id",columnDefinition = "SMALLINT UNSIGNED", referencedColumnName = "country_id", nullable = false)
    private Country country;

    public City(){
        super();
    }

    public City(String city) {
        this.city = city;
    }

    public City(String city, Country country){
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        if (this.country != null) { this.country.getCities().remove(this); }
        this.country = country;
        if(country!=null) { country.getCities().add(this); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        City city1 = (City) o;
        return city.equals(city1.city) && country.equals(city1.country);
    }

    @Override
    public int hashCode() {
        return city.hashCode()*31+country.hashCode();
    }

    @Override
    public String toString() {
        return "{\"super\":" + super.toString() +
                ",\"city\":\"" + city + "\"" +
                ",\"country\":" + country + "}";
    }
}