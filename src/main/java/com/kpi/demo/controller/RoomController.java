package com.kpi.demo.controller;

import com.kpi.demo.dto.CheckDTO;
import com.kpi.demo.dto.ReportDTO;
import com.kpi.demo.dto.RoomDTO;
import com.kpi.demo.entity.Room;
import com.kpi.demo.entity.User;
import com.kpi.demo.service.CheckService;
import com.kpi.demo.service.ReportService;
import com.kpi.demo.service.RoomService;
import com.kpi.demo.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final UserService userService;
    private final ReportService reportService;
    private final CheckService checkService;

    private final static String FILEPATH = "src\\\\main\\\\resources\\\\images";

    public RoomController(RoomService roomService,
                          UserService userService,
                          ReportService reportService,
                          CheckService checkService) {
        this.roomService = roomService;
        this.userService = userService;
        this.reportService = reportService;
        this.checkService = checkService;
    }

    @GetMapping()
    public List<Room> getAllRoomsForUser(@RequestHeader("authorization") String bearer) {
        User user = getAuthorizedUserByHeader(bearer);
        return roomService.getAllRoomsForUser(user);
    }

    @PostMapping()
    public long createRoom(@RequestHeader("authorization") String bearer,
                           @RequestBody RoomDTO roomDTO) {
        User user = getAuthorizedUserByHeader(bearer);
        return roomService.createRoom(user, roomDTO);
    }

    @GetMapping("/{id}")
    public Room getRoomInfo(@RequestHeader("authorization") String bearer,
                            @PathVariable("id") long id) {
        User user = getAuthorizedUserByHeader(bearer);
        Room room = roomService.getRoomById(id);
        if (!roomService.isUserInRoom(user, room)) {
            throw new ForbiddenException();
        }
        return roomService.getRoomById(id);
    }

    @PostMapping("/{id}")
    public void joinRoom(@RequestHeader("authorization") String bearer,
                         @PathVariable("id") long id) {
        User user = getAuthorizedUserByHeader(bearer);
        Room room = roomService.getRoomById(id);
        if (room == null) {
            throw new ForbiddenException();
        }
        if (roomService.isUserInRoom(user, room)) {
            throw new AlreadyExistException();
        }
        roomService.addUserToRoom(user, id);
    }

    @PostMapping("/{id}/report")
    public void createReport(@RequestHeader("authorization") String bearer,
                             @PathVariable("id") long id,
                             @RequestBody ReportDTO reportDTO) {
        User user = getAuthorizedUserByHeader(bearer);
        Room room = roomService.getRoomById(id);
        if (!roomService.isUserInRoom(user, room)) {
            throw new ForbiddenException();
        }
        if (reportService.isReportExist(user, room, reportDTO)) {
            throw new AlreadyExistException();
        }
        reportService.createReport(user, room, reportDTO);
    }

    @PostMapping("/{id}/check")
    public void createCheck(@RequestHeader("authorization") String bearer,
                            @PathVariable("id") long id,
                            @RequestBody CheckDTO checkDTO) {
        User user = getAuthorizedUserByHeader(bearer);
        Room room = roomService.getRoomById(id);
        if (!roomService.isUserInRoom(user, room)) {
            throw new ForbiddenException();
        }
        if (checkService.isCheckExist(user, room, checkDTO)) {
            throw new AlreadyExistException();
        }
        checkService.createCheck(user, room, checkDTO);
    }

    private User getAuthorizedUserByHeader(String bearer) {
        String token = bearer.substring(bearer.indexOf(" ") + 1);
        User user = userService.getUserByToken(token);
        if (StringUtils.isEmpty(token) || user == null) {
            throw new UserController.UnauthorizedException();
        }
        return user;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class ForbiddenException extends RuntimeException {
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public static class AlreadyExistException extends RuntimeException {
    }
}
