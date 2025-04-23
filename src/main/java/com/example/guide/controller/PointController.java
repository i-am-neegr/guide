package com.example.guide.controller;

import com.example.guide.model.Point;
import com.example.guide.service.PointService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/point")
@AllArgsConstructor
@RestController
public class PointController {
    @Autowired
    private PointService pointService;

    @PostMapping("add")
    public ResponseEntity<Point> addPoint(@RequestBody Point point) {
        return ResponseEntity.ok(pointService.addPoint(point));
    }

    @GetMapping("point-{id}")
    public ResponseEntity<Point> getPoint(@PathVariable long id) {
        return ResponseEntity.ok(pointService.getPointById(id));
    }

    @GetMapping("all-points")
    public ResponseEntity<List<Point>> getAllPoints() {
        return ResponseEntity.ok(pointService.getAllPoints());
    }

    @DeleteMapping("point-{id}")
    public void deletePoint(@PathVariable long id) {
        pointService.deletePointById(id);
    }


}
