package com.axiora.spotgo.monitoring.application.internal.queryservices;

import com.axiora.spotgo.monitoring.domain.model.aggregates.Employee;
import com.axiora.spotgo.monitoring.domain.model.aggregates.OccupancyByHour;
import com.axiora.spotgo.monitoring.domain.model.aggregates.WeeklyTrend;
import com.axiora.spotgo.monitoring.domain.model.queries.GetAllEmployeesQuery;
import com.axiora.spotgo.monitoring.domain.model.queries.GetAllOccupancyByHourQuery;
import com.axiora.spotgo.monitoring.domain.model.queries.GetAllWeeklyTrendsQuery;
import com.axiora.spotgo.monitoring.domain.model.queries.GetOccupancyByHourByParkingIdQuery;
import com.axiora.spotgo.monitoring.domain.model.queries.GetWeeklyTrendsByParkingIdQuery;

import java.util.List;

public interface MonitoringQueryService {
    List<Employee> handle(GetAllEmployeesQuery query);
    List<OccupancyByHour> handle(GetAllOccupancyByHourQuery query);
    List<OccupancyByHour> handle(GetOccupancyByHourByParkingIdQuery query);
    List<WeeklyTrend> handle(GetAllWeeklyTrendsQuery query);
    List<WeeklyTrend> handle(GetWeeklyTrendsByParkingIdQuery query);
}
