package com.axiora.spotgo.billing.application.internal.queryservices;

import com.axiora.spotgo.billing.application.queryservices.ReceiptQueryService;
import com.axiora.spotgo.billing.domain.model.aggregates.Receipt;
import com.axiora.spotgo.billing.domain.model.queries.GetAllReceiptsQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetReceiptByIdQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetReceiptsByBookingCodeQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetReceiptsByClientIdQuery;
import com.axiora.spotgo.billing.domain.repositories.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReceiptQueryServiceImpl implements ReceiptQueryService {

    private final ReceiptRepository receiptRepository;

    public ReceiptQueryServiceImpl(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @Override
    public Optional<Receipt> handle(GetReceiptByIdQuery query) {
        return receiptRepository.findById(query.receiptId());
    }

    @Override
    public List<Receipt> handle(GetAllReceiptsQuery query) {
        return receiptRepository.findAll();
    }

    @Override
    public List<Receipt> handle(GetReceiptsByClientIdQuery query) {
        return receiptRepository.findAllByClientId(query.clientId());
    }

    @Override
    public List<Receipt> handle(GetReceiptsByBookingCodeQuery query) {
        return receiptRepository.findAllByBookingCode(query.bookingCode());
    }
}
