package com.axiora.spotgo.billing.application.queryservices;

import com.axiora.spotgo.billing.domain.model.aggregates.Receipt;
import com.axiora.spotgo.billing.domain.model.queries.GetAllReceiptsQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetReceiptByIdQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetReceiptsByBookingCodeQuery;

import java.util.List;
import java.util.Optional;

public interface ReceiptQueryService {

    Optional<Receipt> handle(GetReceiptByIdQuery query);

    List<Receipt> handle(GetAllReceiptsQuery query);

    List<Receipt> handle(GetReceiptsByBookingCodeQuery query);
}
