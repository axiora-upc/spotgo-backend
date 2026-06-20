package com.axiora.spotgo.billing.application.commandservices;

import com.axiora.spotgo.billing.domain.model.aggregates.Receipt;
import com.axiora.spotgo.billing.domain.model.commands.CreateReceiptCommand;
import com.axiora.spotgo.billing.domain.model.commands.DeleteReceiptCommand;
import com.axiora.spotgo.shared.application.result.ApplicationError;
import com.axiora.spotgo.shared.application.result.Result;

public interface ReceiptCommandService {

    Result<Receipt, ApplicationError> handle(CreateReceiptCommand command);

    Result<Void, ApplicationError> handle(DeleteReceiptCommand command);
}
