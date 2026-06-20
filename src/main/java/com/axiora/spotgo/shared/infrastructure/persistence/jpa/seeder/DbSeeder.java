package com.axiora.spotgo.shared.infrastructure.persistence.jpa.seeder;

import com.axiora.spotgo.parking.domain.model.aggregates.Blueprint;
import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import com.axiora.spotgo.parking.domain.model.aggregates.Parking;
import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;
import com.axiora.spotgo.parking.domain.model.valueobjects.Coordinates;
import com.axiora.spotgo.parking.domain.model.valueobjects.ReservationStatus;
import com.axiora.spotgo.parking.domain.model.valueobjects.SpotStatus;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.BlueprintRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.DetectedSpotRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ParkingRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ReservationRepository;
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
import java.util.HashMap;
import java.util.Map;

@Component
public class DbSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DbSeeder.class);

    private final ParkingRepository parkingRepository;
    private final BlueprintRepository blueprintRepository;
    private final DetectedSpotRepository detectedSpotRepository;
    private final ReservationRepository reservationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.seeder.reset-before-seed:false}")
    private boolean resetBeforeSeed;

    public DbSeeder(ParkingRepository parkingRepository,
                    BlueprintRepository blueprintRepository,
                    DetectedSpotRepository detectedSpotRepository,
                    ReservationRepository reservationRepository) {
        this.parkingRepository = parkingRepository;
        this.blueprintRepository = blueprintRepository;
        this.detectedSpotRepository = detectedSpotRepository;
        this.reservationRepository = reservationRepository;
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

            log.info("seed completed successfully");
        } catch (Exception e) {
            log.error("seed failed: {}", e.getMessage(), e);
        }
    }

    private final Map<String, Long> parkingIdMap = new HashMap<>();

    private void seedParkings(JsonNode parkings) {
        if (parkings == null || !parkings.isArray()) {
            log.warn("no parkings found in db.json");
            return;
        }
        log.info("seeding {} parkings...", parkings.size());
        for (var node : parkings) {
            var parking = new Parking(
                    node.get("name").asText(),
                    node.get("address").asText(),
                    node.get("totalSpaces").asInt(),
                    node.has("rating") && !node.get("rating").isNull()
                            ? node.get("rating").asDouble() : null,
                    node.has("pricePerHour") && !node.get("pricePerHour").isNull()
                            ? node.get("pricePerHour").asDouble() : null
            );
            var saved = parkingRepository.save(parking);
            parkingIdMap.put(node.get("id").asText(), saved.getId());
        }
    }

    private final Map<String, Long> blueprintIdMap = new HashMap<>();

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
                    node.has("dataUrl") ? node.get("dataUrl").asText("") : "",
                    parkingId
            );
            var saved = blueprintRepository.save(blueprint);
            blueprintIdMap.put(node.get("id").asText(), saved.getId());
        }
    }

    private final Map<String, Map<String, Long>> spotIdMap = new HashMap<>();

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

            var x = node.has("x_pct") ? node.get("x_pct").asDouble() : 0.0;
            var y = node.has("y_pct") ? node.get("y_pct").asDouble() : 0.0;
            var coordinates = new Coordinates(x, y);

            var detectedSpot = new DetectedSpot(coordinates, blueprintId);

            var status = node.has("status") ? node.get("status").asText("available") : "available";
            switch (status) {
                case "occupied" -> detectedSpot.updateStatus(SpotStatus.OCCUPIED);
                case "maintenance" -> detectedSpot.updateStatus(SpotStatus.MAINTENANCE);
                default -> detectedSpot.updateStatus(SpotStatus.FREE);
            }

            var saved = detectedSpotRepository.save(detectedSpot);

            var row = node.get("row").asInt();
            var col = node.get("col").asInt();
            var key = row + ":" + col;
            spotIdMap.computeIfAbsent(node.get("blueprintId").asText(), k -> new HashMap<>())
                    .put(key, saved.getId());
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
            var spotStr = node.has("spot") ? node.get("spot").asText("") : "";
            var spotId = resolveSpotId(node, spotStr);
            if (spotId == null) {
                log.warn("could not resolve spotId for reservation {}, skipping", node.get("code").asText());
                continue;
            }

            var startTime = parseDateTime(node.get("startDate").asText());
            var endTime = parseDateTime(node.get("endDate").asText());

            var reservation = new Reservation(
                    node.get("code").asText(),
                    spotId,
                    startTime,
                    endTime
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

    private Long resolveSpotId(JsonNode reservationNode, String spotStr) {
        if (spotStr.isEmpty()) return null;

        var row = spotStr.charAt(0) - 'A';
        var col = Integer.parseInt(spotStr.substring(1)) - 1;
        var key = row + ":" + col;

        var blueprintId = reservationNode.has("parkingId")
                ? blueprintIdMap.entrySet().stream()
                .filter(e -> {
                    var parkingId = parkingIdMap.get(reservationNode.get("parkingId").asText());
                    var bp = blueprintRepository.findById(e.getValue());
                    return bp.isPresent() && bp.get().getParkingId().equals(parkingId);
                })
                .map(Map.Entry::getKey)
                .findFirst().orElse(null)
                : null;

        if (blueprintId == null) return null;

        var spots = spotIdMap.get(blueprintId);
        return spots != null ? spots.get(key) : null;
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
