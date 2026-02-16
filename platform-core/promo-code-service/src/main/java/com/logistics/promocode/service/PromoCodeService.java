package com.logistics.promocode.service;

import com.logistics.promocode.dto.PromoCodeDTO;
import com.logistics.promocode.model.PromoCode;

import java.math.BigDecimal;
import java.util.List;

public interface PromoCodeService {
    PromoCode createPromoCode(PromoCodeDTO promoCodeDTO);
    List<PromoCode> getAllPromoCodes();
    PromoCode getPromoCodeByCode(String code);
    BigDecimal validatePromoCode(String code, String userId, BigDecimal orderValue);
    BigDecimal applyPromoCode(String code, String userId, String orderId, BigDecimal orderValue);
}
