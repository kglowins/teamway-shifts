package io.github.kglowins.shifts.controllers;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static java.util.stream.Collectors.toList;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.controllers.dto.ShiftDTO;
import io.github.kglowins.shifts.services.ShiftService;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class ShiftsController {

    private static final String V1_SHIFTS = "/v1/shifts";
    private static final String V1_SHIFTS_ID = V1_SHIFTS + "/:id";

    private final ShiftService shiftService;

    private final Gson gson;

    @Inject
    public ShiftsController(ShiftService shiftService, Gson gson) {
        this.shiftService = shiftService;
        this.gson = gson;
        createGetShiftsEndpoint();
        createPostShiftEndpoint();
        createDeleteShiftEndpoint();
        createDeleteShiftsEndpoint();
        createGetShiftsOfEmployeeEndpoint();
    }

    private void createGetShiftsEndpoint() {
        get(V1_SHIFTS, (request, response) -> {
            response.type(JSON_UTF_8.toString());
            return shiftService.getShifts();
        }, gson::toJson);
    }

    private void createPostShiftEndpoint() {
        post(V1_SHIFTS, (request, response) -> {
            ShiftDTO shiftDTO = gson.fromJson(request.body(), ShiftDTO.class);
            shiftService.addShift(shiftDTO);
            response.status(SC_CREATED);
            return "";
        });
    }

    public void createDeleteShiftEndpoint() {
        delete(V1_SHIFTS_ID, (request, response) -> {
            response.status(SC_NO_CONTENT);
            Long id = Long.parseLong(request.params("id"));
            shiftService.removeShifts(List.of(id));
            response.status(SC_NO_CONTENT);
            return "";
        });
    }

    public void createDeleteShiftsEndpoint() {
        delete(V1_SHIFTS, (request, response) -> {
            List<Long> shiftIds = Stream.of(gson.fromJson(request.body(), Long[].class)).collect(toList());
            shiftService.removeShifts(shiftIds);
            response.status(SC_NO_CONTENT);
            return "";
        });
    }

    public void createGetShiftsOfEmployeeEndpoint() {
        get(V1_SHIFTS + "/employee/:id", (request, response) -> {
            Long id = Long.parseLong(request.params("id"));
            response.type(JSON_UTF_8.toString());
            return shiftService.getShiftsOfEmployee(id);
        }, gson::toJson);
    }
}
