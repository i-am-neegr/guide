package com.example.guide.controller;

import com.example.guide.model.Path;
import com.example.guide.service.PathService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/path")
@RestController
@AllArgsConstructor
public class PathController {
    private PathService pathService;

    @PostMapping("add")
    public ResponseEntity<Path> addPath(@RequestBody Path path) {
        return ResponseEntity.ok(pathService.addPath(path));
    }

    @PostMapping("add-list")
    public ResponseEntity<List<Path>> addPathList(@RequestBody List<Path> pathList) {
        return ResponseEntity.ok(pathService.addPaths(pathList));
    }
    @GetMapping("path-{id}")
    public ResponseEntity<Path> getPath(@PathVariable Long id) {
        return ResponseEntity.ok(pathService.getPathById(id));
    }

    @GetMapping("all-points")
    public ResponseEntity<List<Path>> getAllPoints() {
        return ResponseEntity.ok(pathService.getAllPaths());
    }
}
