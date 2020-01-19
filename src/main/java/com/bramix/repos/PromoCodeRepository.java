package com.bramix.repos;

import com.bramix.entities.PromoCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PromoCodeRepository extends CrudRepository <PromoCode,Integer> {
    //@Query("SELECT p.phone from PromoCode p where p.phone = :phone and w.type = :type")
    //PromoCode getPromoCode (String phone);
    PromoCode getPromoCodeByPhone(String phone);
    boolean existsByPhone(String phone);
    @Transactional
    void deleteByPhone(String phone);
}
