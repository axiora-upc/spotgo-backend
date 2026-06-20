package com.axiora.spotgo.billing.interfaces.rest;

import com.axiora.spotgo.billing.application.commandservices.ReceiptCommandService;
import com.axiora.spotgo.billing.application.queryservices.ReceiptQueryService;
import com.axiora.spotgo.billing.domain.model.commands.DeleteReceiptCommand;
import com.axiora.spotgo.billing.domain.model.queries.GetAllReceiptsQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetReceiptByIdQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetReceiptsByBookingCodeQuery;
import com.axiora.spotgo.billing.interfaces.rest.resources.CreateReceiptResource;
import com.axiora.spotgo.billing.interfaces.rest.resources.ReceiptResource;
import com.axiora.spotgo.billing.interfaces.rest.transform.CreateReceiptCommandFromResourceAssembler;
import com.axiora.spotgo.billing.interfaces.rest.transform.ReceiptResourceFromEntityAssembler;
import com.axiora.spotgo.shared.application.result.ApplicationError;
import com.axiora.spotgo.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.axiora.spotgo.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/receipts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Receipts", description = "Endpoints for managing parking receipts")
public class ReceiptsController {

    private final ReceiptCommandService receiptCommandService;
    private final ReceiptQueryService receiptQueryService;

    public ReceiptsController(ReceiptCommandService receiptCommandService,
                              ReceiptQueryService receiptQueryService) {
        this.receiptCommandService = receiptCommandService;
        this.receiptQueryService = receiptQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a receipt", description = "Creates a new parking receipt for a completed session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Receipt created successfully",
                    content = @Content(schema = @Schema(implementation = ReceiptResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createReceipt(@Valid @RequestBody CreateReceiptResource resource) {
        var command = CreateReceiptCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = receiptCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ReceiptResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    @Operation(summary = "Get all receipts",
            description = "Returns all receipts. Optional query param: bookingCode to filter by booking code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ReceiptResource.class)))
    })
    public ResponseEntity<List<ReceiptResource>> getAllReceipts(
            @RequestParam(required = false)
            @Parameter(description = "Filter receipts by booking code")
            String bookingCode
    ) {
        var receipts = (bookingCode != null && !bookingCode.isBlank())
                ? receiptQueryService.handle(new GetReceiptsByBookingCodeQuery(bookingCode))
                : receiptQueryService.handle(new GetAllReceiptsQuery());
        var result = receipts.stream()
                .map(ReceiptResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{receiptId}")
    @Operation(summary = "Get receipt by ID", description = "Returns a specific receipt by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt found",
                    content = @Content(schema = @Schema(implementation = ReceiptResource.class))),
            @ApiResponse(responseCode = "404", description = "Receipt not found")
    })
    public ResponseEntity<?> getReceiptById(
            @PathVariable
            @Parameter(description = "Receipt unique identifier", example = "1", required = true)
            Long receiptId
    ) {
        var query = new GetReceiptByIdQuery(receiptId);
        var receipt = receiptQueryService.handle(query);
        if (receipt.isEmpty()) {
            var error = ApplicationError.notFound("Receipt", "Receipt with ID %d not found".formatted(receiptId));
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(error);
        }
        return ResponseEntity.ok(ReceiptResourceFromEntityAssembler.toResourceFromEntity(receipt.get()));
    }

    @DeleteMapping("/{receiptId}")
    @Operation(summary = "Delete a receipt", description = "Deletes a receipt by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Receipt deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Receipt not found")
    })
    public ResponseEntity<?> deleteReceipt(
            @PathVariable
            @Parameter(description = "Receipt unique identifier", example = "1", required = true)
            Long receiptId
    ) {
        var result = receiptCommandService.handle(new DeleteReceiptCommand(receiptId));
        if (result.isFailure()) {
            var error = ApplicationError.notFound("Receipt", "Receipt with ID %d not found".formatted(receiptId));
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(error);
        }
        return ResponseEntity.noContent().build();
    }
}
