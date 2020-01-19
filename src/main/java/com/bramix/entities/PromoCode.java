package com.bramix.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String phone;
    private String promoCode;

    public PromoCode(String phone, String promoCode) {
        this.phone = phone;
        this.promoCode = promoCode;
    }
    public PromoCode() {}
}
