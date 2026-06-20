package com.axiora.spotgo.shared.infrastructure.persistence.jpa.seeder;

import com.axiora.spotgo.parking.domain.model.aggregates.Blueprint;
import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import com.axiora.spotgo.parking.domain.model.aggregates.Parking;
import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;
import com.axiora.spotgo.parking.domain.model.valueobjects.ReservationStatus;
import com.axiora.spotgo.parking.domain.model.valueobjects.SpotStatus;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.BlueprintRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.DetectedSpotRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ParkingRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ReservationRepository;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.entities.ClientPlanPersistenceEntity;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.entities.ReceiptPersistenceEntity;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.repositories.ClientPlanPersistenceRepository;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.repositories.ReceiptPersistenceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class DbSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DbSeeder.class);

    private final ParkingRepository parkingRepository;
    private final BlueprintRepository blueprintRepository;
    private final DetectedSpotRepository detectedSpotRepository;
    private final ReservationRepository reservationRepository;
    private final ClientPlanPersistenceRepository clientPlanRepository;
    private final SubscriptionPersistenceRepository subscriptionRepository;
    private final ReceiptPersistenceRepository receiptRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.seeder.reset-before-seed:false}")
    private boolean resetBeforeSeed;

    public DbSeeder(ParkingRepository parkingRepository,
                    BlueprintRepository blueprintRepository,
                    DetectedSpotRepository detectedSpotRepository,
                    ReservationRepository reservationRepository,
                    ClientPlanPersistenceRepository clientPlanRepository,
                    SubscriptionPersistenceRepository subscriptionRepository,
                    ReceiptPersistenceRepository receiptRepository) {
        this.parkingRepository = parkingRepository;
        this.blueprintRepository = blueprintRepository;
        this.detectedSpotRepository = detectedSpotRepository;
        this.reservationRepository = reservationRepository;
        this.clientPlanRepository = clientPlanRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.receiptRepository = receiptRepository;
    }

    @Override
    public void run(String... args) {
        log.info("database connection OK, seeding seed data...");

        try {
            if (resetBeforeSeed) {
                log.info("reset-before-seed is true, wiping tables...");
                reservationRepository.deleteAll();
                detectedSpotRepository.deleteAll();
                blueprintRepository.deleteAll();
                parkingRepository.deleteAll();
                receiptRepository.deleteAll();
                subscriptionRepository.deleteAll();
                clientPlanRepository.deleteAll();
                log.info("tables wiped successfully");
            } else if (parkingRepository.count() > 0) {
                log.info("tables already contain data, skipping seed");
                return;
            }

            var resource = new ClassPathResource("db.json");

            if (!resource.exists()) {
                log.error("db.json not found at classpath location, skipping seed");
                return;
            }

            var root = objectMapper.readTree(resource.getInputStream());

            seedParkings(root.get("parkings"));
            seedBlueprints(root.get("blueprints"));
            seedDetectedSpots(root.get("detectedSpots"));
            seedReservations(root.get("reservations"));
            seedClientPlans(root.get("clientPlans"));
            seedSubscriptions(root.get("subscriptions"));
            seedReceipts(root.get("receipts"));

            log.info("seed completed successfully");
        } catch (Exception e) {
            log.error("seed failed: {}", e.getMessage(), e);
        }
    }

    private final Map<String, Long> parkingIdMap = new HashMap<>();
    private final Map<String, Long> blueprintIdMap = new HashMap<>();
    private final Map<String, Long> clientPlanIdMap = new HashMap<>();

    private void seedParkings(JsonNode parkings) {
        if (parkings == null || !parkings.isArray()) {
            log.warn("no parkings found in db.json");
            return;
        }
        log.info("seeding {} parkings...", parkings.size());
        for (var node : parkings) {
            var parking = new Parking(
                    extractNumericId(node.has("adminId") ? node.get("adminId").asText("") : ""),
                    node.get("name").asText(),
                    node.get("address").asText(),
                    node.get("city").asText(),
                    node.get("totalSpaces").asInt(),
                    nullableInt(node, "availableSpaces"),
                    nullableInt(node, "totalFloors"),
                    nullableDouble(node, "averageOccupancy"),
                    nullableDouble(node, "occupancyTrendPercent"),
                    nullableText(node, "peakHour"),
                    nullableDouble(node, "totalRevenue"),
                    nullableText(node, "systemStatus"),
                    node.has("rating") && !node.get("rating").isNull() ? node.get("rating").asDouble() : null,
                    node.has("pricePerHour") && !node.get("pricePerHour").isNull() ? node.get("pricePerHour").asDouble() : null,
                    nullableDouble(node, "revenueTrendPercent"),
                    nullableInt(node, "totalCapacity"),
                    nullableDouble(node, "efficiencyIndex")
            );
            var saved = parkingRepository.save(parking);
            parkingIdMap.put(node.get("id").asText(), saved.getId());
        }
    }

    private void seedBlueprints(JsonNode blueprints) {
        if (blueprints == null || !blueprints.isArray()) {
            log.warn("no blueprints found in db.json");
            return;
        }
        log.info("seeding {} blueprints...", blueprints.size());
        for (var node : blueprints) {
            var parkingId = parkingIdMap.get(node.get("parkingId").asText());
            if (parkingId == null) {
                log.warn("parkingId {} not found, skipping blueprint", node.get("parkingId").asText());
                continue;
            }

            var blueprint = new Blueprint(
                    extractNumericId(node.has("adminId") ? node.get("adminId").asText("") : ""),
                    parkingId,
                    node.has("name") ? node.get("name").asText("") : "",
                    node.has("dataUrl") ? node.get("dataUrl").asText("") : ""
            );
            var saved = blueprintRepository.save(blueprint);
            blueprintIdMap.put(node.get("id").asText(), saved.getId());
        }
    }

    private void seedDetectedSpots(JsonNode detectedSpots) {
        if (detectedSpots == null || !detectedSpots.isArray()) {
            log.warn("no detectedSpots found in db.json");
            return;
        }
        log.info("seeding {} detectedSpots...", detectedSpots.size());
        for (var node : detectedSpots) {
            var blueprintId = blueprintIdMap.get(node.get("blueprintId").asText());
            if (blueprintId == null) {
                log.warn("blueprintId {} not found, skipping detectedSpot", node.get("blueprintId").asText());
                continue;
            }

            var parkingId = node.has("parkingId") ? parkingIdMap.get(node.get("parkingId").asText()) : null;

            var statusStr = node.has("status") ? node.get("status").asText("available") : "available";
            var status = SpotStatus.fromDisplayName(statusStr);

            var detectedSpot = new DetectedSpot(
                    node.has("localId") ? node.get("localId").asInt() : null,
                    blueprintId,
                    parkingId,
                    node.has("row") ? node.get("row").asInt() : null,
                    node.has("col") ? node.get("col").asInt() : null,
                    node.has("x_pct") ? node.get("x_pct").asDouble() : 0.0,
                    node.has("y_pct") ? node.get("y_pct").asDouble() : 0.0,
                    node.has("w_pct") ? node.get("w_pct").asDouble() : 0.0,
                    node.has("h_pct") ? node.get("h_pct").asDouble() : 0.0,
                    status
            );

            detectedSpotRepository.save(detectedSpot);
        }
    }

    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private void seedReservations(JsonNode reservations) {
        if (reservations == null || !reservations.isArray()) {
            log.warn("no reservations found in db.json");
            return;
        }
        log.info("seeding {} reservations...", reservations.size());
        for (var node : reservations) {
            var parkingId = parkingIdMap.get(node.get("parkingId").asText());
            if (parkingId == null) {
                log.warn("parkingId {} not found, skipping reservation", node.get("parkingId").asText());
                continue;
            }

            var startDate = parseDateTime(node.get("startDate").asText());
            var endDate = parseDateTime(node.get("endDate").asText());

            var reservation = new Reservation(
                    extractNumericId(node.get("clientId").asText()),
                    parkingId,
                    node.get("code").asText(),
                    node.get("spot").asText(),
                    startDate,
                    endDate,
                    node.has("amount") && !node.get("amount").isNull() ? node.get("amount").asDouble() : null,
                    node.has("baseAmount") && !node.get("baseAmount").isNull() ? node.get("baseAmount").asDouble() : null,
                    node.has("rating") && !node.get("rating").isNull() ? node.get("rating").asDouble() : null
            );

            var status = node.has("status") ? node.get("status").asText("active") : "active";
            switch (status) {
                case "completed" -> reservation.updateStatus(ReservationStatus.COMPLETED);
                case "cancelled" -> reservation.updateStatus(ReservationStatus.CANCELLED);
                default -> reservation.updateStatus(ReservationStatus.ACTIVE);
            }

            reservationRepository.save(reservation);
        }
    }

    private void seedClientPlans(JsonNode clientPlans) {
        if (clientPlans == null || !clientPlans.isArray()) {
            log.warn("no clientPlans found in db.json");
            return;
        }
        log.info("seeding {} clientPlans...", clientPlans.size());
        for (var node : clientPlans) {
            var entity = new ClientPlanPersistenceEntity();
            entity.setType(node.get("type").asText());
            entity.setName(node.get("name").asText());
            entity.setMonthlyPrice(node.get("monthlyPrice").asDouble());
            entity.setDescription(node.get("description").asText());
            entity.setReservationsPerMonth(
                    node.has("reservationsPerMonth") && !node.get("reservationsPerMonth").isNull()
                            ? node.get("reservationsPerMonth").asInt() : null
            );
            entity.setDiscountPercent(node.get("discountPercent").asDouble());

            var features = new ArrayList<String>();
            if (node.has("features") && node.get("features").isArray()) {
                for (var f : node.get("features")) {
                    features.add(f.asText());
                }
            }
            entity.setFeatures(features);

            var saved = clientPlanRepository.save(entity);
            clientPlanIdMap.put(node.get("id").asText(), saved.getId());
        }
    }

    private void seedSubscriptions(JsonNode subscriptions) {
        if (subscriptions == null || !subscriptions.isArray()) {
            log.warn("no subscriptions found in db.json");
            return;
        }
        log.info("seeding {} subscriptions...", subscriptions.size());
        for (var node : subscriptions) {
            var planId = clientPlanIdMap.get(node.get("planId").asText());
            if (planId == null) {
                log.warn("planId {} not found, skipping subscription", node.get("planId").asText());
                continue;
            }

            var entity = new SubscriptionPersistenceEntity();
            entity.setClientId(extractNumericId(node.get("clientId").asText()));
            entity.setPlanId(planId);
            entity.setStatus(node.get("status").asText());
            entity.setRenewsOn(node.get("renewsOn").asText());
            entity.setPricePerMonth(node.get("pricePerMonth").asDouble());
            entity.setSessions(node.get("sessions").asInt());
            entity.setSavedThisMonth(node.get("savedThisMonth").asDouble());
            entity.setSavingsMonth(node.has("savingsMonth") ? node.get("savingsMonth").asText() : null);
            entity.setMemberSince(node.get("memberSince").asText());
            entity.setAutoRenewal(node.get("autoRenewal").asBoolean());
            entity.setPaymentMethodLastFour(node.get("paymentMethodLastFour").asText());
            entity.setPaymentMethodExpiry(node.get("paymentMethodExpiry").asText());

            subscriptionRepository.save(entity);
        }
    }

    private void seedReceipts(JsonNode receipts) {
        if (receipts == null || !receipts.isArray()) {
            log.warn("no receipts found in db.json");
            return;
        }
        log.info("seeding {} receipts...", receipts.size());
        for (var node : receipts) {
            var entity = new ReceiptPersistenceEntity();
            entity.setClientId(extractNumericId(node.get("clientId").asText()));
            entity.setInvoiceNumber(node.get("invoiceNumber").asText());
            entity.setLocationName(node.get("locationName").asText());
            entity.setDate(node.get("date").asText());
            entity.setDurationHours(node.get("durationHours").asInt());
            entity.setDurationMinutes(node.get("durationMinutes").asInt());
            entity.setPaymentMethod(node.get("paymentMethod").asText());
            entity.setBookingCode(node.get("bookingCode").asText());
            entity.setAmount(node.get("amount").asDouble());
            entity.setStatus(node.get("status").asText());

            receiptRepository.save(entity);
        }
    }

    private Long extractNumericId(String prefixedId) {
        if (prefixedId == null || prefixedId.isBlank()) return null;
        try {
            return Long.parseLong(prefixedId.replaceAll("^[a-zA-Z]+-", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer nullableInt(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asInt() : null;
    }

    private Double nullableDouble(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asDouble() : null;
    }

    private String nullableText(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }

    private LocalDateTime parseDateTime(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, ISO_FORMAT);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e2) {
                log.warn("could not parse date '{}', using current time", dateStr);
                return LocalDateTime.now();
            }
        }
    }
}
