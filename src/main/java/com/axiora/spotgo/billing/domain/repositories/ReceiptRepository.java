package com.axiora.spotgo.billing.domain.repositories;

import com.axiora.spotgo.billing.domain.model.aggregates.Receipt;

import java.util.List;
import java.util.Optional;

public interface ReceiptRepository {

    Optional<Receipt> findById(Long id);

    List<Receipt> findAll();

    List<Receipt> findAllByClientId(Long clientId);

    List<Receipt> findAllByBookingCode(String bookingCode);

    Receipt save(Receipt receipt);

    void deleteById(Long id);
}
