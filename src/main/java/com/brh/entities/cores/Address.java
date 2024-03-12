package com.brh.entities.cores;

import com.brh.entities.bases.SimplePKEntity;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "addresses")
@AttributeOverride(name = "id", column = @Column(name = "address_id",columnDefinition = "SMALLINT UNSIGNED")) //Permet seulement de changer le nom de l'attribut
@SuppressWarnings("unused")
public class Address extends SimplePKEntity<Integer> { //Integer is the concrete type matching the generic type ID in SimplePKEntity
    @Basic
    @Column(name = "address",nullable = false)
    private java.lang.String addressLine1;

    @Basic
    @Column(name = "address2")
    private java.lang.String addressLine2;

    @Basic
    @Column(name = "district",nullable = false)
    private java.lang.String district;

    @Basic
    @Column(name = "postal_code")
    private java.lang.String postalCode;

    @Basic
    @Column(name = "phone")
    private java.lang.String phone;

    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @JoinColumn(name = "city_id",columnDefinition = "SMALLINT UNSIGNED", referencedColumnName = "city_id", nullable = false)
    private City city;

    public Address(){
        super();
    }
    public Address(java.lang.String addressLine1) {
        this.addressLine1 = addressLine1;
    }


    public java.lang.String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(java.lang.String address) {
        this.addressLine1 = address;
    }


    public java.lang.String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(java.lang.String address2) {
        this.addressLine2 = address2;
    }


    public java.lang.String getDistrict() {
        return district;
    }

    public void setDistrict(java.lang.String district) {
        this.district = district;
    }


    public java.lang.String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(java.lang.String postalCode) {
        this.postalCode = postalCode;
    }

    public java.lang.String getPhone() {
        return phone;
    }

    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Address address = (Address) o;
        return addressLine1.equals(address.addressLine1) && Objects.equals(addressLine2, address.addressLine2) && district.equals(address.district) && postalCode.equals(address.postalCode) && phone.equals(address.phone) && city.equals(address.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), addressLine1, addressLine2, district, postalCode, phone);
    }

    @Override
    public java.lang.String toString() {
        return "{\"super\":" + super.toString() +
                ",\"addressLine1\":\"" + addressLine1 + "\"" +
                ",\"addressLine2\":\"" + addressLine2 + "\"" +
                ",\"district\":\"" + district + "\"" +
                ",\"postalCode\":\"" + postalCode + "\"" +
                ",\"phone\":\"" + phone + "\"" +
                ",\"city\":" + city + "}";
    }
}
