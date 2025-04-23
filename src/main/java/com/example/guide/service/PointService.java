package com.example.guide.service;

import com.example.guide.model.Point;
import com.example.guide.repository.PointRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class PointService {
    @Autowired
    private PointRepository pointRepository;

    public Point addPoint(Point point) {
        return pointRepository.save(point);
    }

    public List<Point> addPoints(List<Point> points) {
        List<Point> saved = new ArrayList<>();
        for (Point point : points) {
            saved.add(addPoint(point));
        }
        return saved;
    }


    public Point getPointById(long id) {
        return pointRepository.findById(id).orElse(null);
    }

    public List<Point> getAllPoints() {
        return pointRepository.findAll();
    }

    public void deletePointById(long id) {
        pointRepository.deleteById(id);
    }
}
