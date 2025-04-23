package com.example.guide.service;

import com.example.guide.model.Path;
import com.example.guide.model.Point;
import com.example.guide.repository.PathRepository;
import com.example.guide.repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class PathService {
    private PathRepository pathRepository;
    private PointRepository pointRepository;

    public List<double[]> buildPath(Long startPoint, Long endPoint, int timePathMinutes) {
        List<List<Long>> response = findRoute(startPoint, endPoint, timePathMinutes);

        if (!response.isEmpty()) {
            response.set(0, List.of(0L, startPoint));
        }

        Map<Long, Point> pointsPosition = new HashMap<>();
        pointRepository.findAll().forEach(point -> pointsPosition.put(point.getID(), point));

        List<double[]> answerPath = new ArrayList<>();
        for (List<Long> value : response) {
            Long pointId = value.get(1);
            Point point = pointsPosition.get(pointId);
            if (point != null) {
                answerPath.add(new double[]{point.getLat(), point.getLon()});
            } else {
                throw new IllegalStateException("Точка с ID " + pointId + " не найдена в базе данных");
            }
        }
        return answerPath;
    }

    private List<List<Long>> findRoute(Long startId, Long finishId, int timePathMinutes) {
        int timePathSeconds = timePathMinutes * 60;

        List<Path> paths = pathRepository.findAll();

        Map<Long, Map<Long, Integer>> timeGraph = new HashMap<>();
        for (Path path : paths) {
            timeGraph.computeIfAbsent(path.getStartId(), k -> new HashMap<>())
                    .put(path.getEndId(), path.getTime());
            timeGraph.computeIfAbsent(path.getEndId(), k -> new HashMap<>())
                    .put(path.getStartId(), path.getTime());
        }

        Map<Long, Map<Long, Double>> matrixDistance = buildMinDistanceMatrix(paths);

        return dijkstra(startId, finishId, timePathSeconds, timeGraph, matrixDistance);
    }

    private Map<Long, Map<Long, Double>> buildMinDistanceMatrix(List<Path> paths) {
        Map<Long, Map<Long, Double>> matrix = new HashMap<>();
        Set<Long> vertices = new HashSet<>();
        for (Path path : paths) {
            vertices.add(path.getStartId());
            vertices.add(path.getEndId());
        }

        for (Long v1 : vertices) {
            matrix.put(v1, new HashMap<>());
            for (Long v2 : vertices) {
                matrix.get(v1).put(v2, Double.POSITIVE_INFINITY);
            }
            matrix.get(v1).put(v1, 0.0);
        }

        for (Path path : paths) {
            matrix.get(path.getStartId()).put(path.getEndId(), path.getLength());
            matrix.get(path.getEndId()).put(path.getStartId(), path.getLength());
        }

        for (Long k : vertices) {
            for (Long i : vertices) {
                for (Long j : vertices) {
                    if (matrix.get(i).get(k) != Double.POSITIVE_INFINITY &&
                            matrix.get(k).get(j) != Double.POSITIVE_INFINITY) {
                        double newDist = matrix.get(i).get(k) + matrix.get(k).get(j);
                        if (newDist < matrix.get(i).get(j)) {
                            matrix.get(i).put(j, newDist);
                        }
                    }
                }
            }
        }

        return matrix;
    }

    private List<List<Long>> dijkstra(Long start, Long finish, int timePath,
                                      Map<Long, Map<Long, Integer>> timeGraph,
                                      Map<Long, Map<Long, Double>> matrixDistance) {
        Deque<Object[]> queue = new ArrayDeque<>();
        queue.add(new Object[]{0, start, new ArrayList<List<Long>>(List.of(List.of(0L, 0L)))});

        int maxTime = 0;
        List<List<Long>> resultPath = new ArrayList<>();

        while (!queue.isEmpty()) {
            Object[] current = queue.pollFirst();
            int dist = (int) current[0];
            Long currentVertex = (Long) current[1];
            List<List<Long>> path = (List<List<Long>>) current[2];

            if (matrixDistance.getOrDefault(currentVertex, new HashMap<>())
                    .getOrDefault(finish, Double.POSITIVE_INFINITY) > timePath - dist) {
                continue;
            }

            if (path.size() > 25) {
                continue;
            }

            Map<Long, Integer> neighbors = timeGraph.getOrDefault(currentVertex, new HashMap<>());
            for (Map.Entry<Long, Integer> neighbor : neighbors.entrySet()) {
                Long weight = neighbor.getKey();
                int time = neighbor.getValue();

                boolean edgeUsed = path.stream().anyMatch(edge ->
                        (edge.get(0).equals(currentVertex) && edge.get(1).equals(weight)) ||
                                (edge.get(0).equals(weight) && edge.get(1).equals(currentVertex)));
                if (edgeUsed && neighbors.size() != 1) {
                    continue;
                }

                int newDist = dist + time;

                if (newDist > timePath) {
                    continue;
                }

                if (weight.equals(finish)) {
                    if (newDist > maxTime) {
                        maxTime = newDist;
                        resultPath = new ArrayList<>(path);
                        resultPath.add(List.of(currentVertex, weight));
                    }
                } else {
                    List<List<Long>> newPath = new ArrayList<>(path);
                    newPath.add(List.of(currentVertex, weight));
                    queue.add(new Object[]{newDist, weight, newPath});
                }
            }
        }

        return resultPath;
    }

    @Transactional
    public Path addPath(Path path) {
        // Проверка на null для входных данных
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (path.getStartId() == null || path.getEndId() == null) {
            throw new IllegalArgumentException("startId and endId must not be null: " + path);
        }
        if (path.getLength() == null || path.getTime() == null) {
            throw new IllegalArgumentException("length and time must not be null: " + path);
        }

        // Отладочный вывод
        System.out.println("Saving path: " + path);

        // Проверка существования точек
        Point startPoint = pointRepository.findById(path.getStartId())
                .orElseThrow(() -> new IllegalArgumentException("Точка с ID " + path.getStartId() + " не найдена"));
        pointRepository.findById(path.getEndId())
                .orElseThrow(() -> new IllegalArgumentException("Точка с ID " + path.getEndId() + " не найдена"));

        // Сохранение пути
        Path savedPath = pathRepository.save(path);

        // Добавление пути в коллекцию neighbors начальной точки
        if (startPoint.getNeighbors() == null) {
            startPoint.setNeighbors(new HashSet<>());
        }
        startPoint.getNeighbors().add(savedPath);
        pointRepository.save(startPoint);

        return savedPath;
    }

    public List<Path> addPaths(List<Path> paths) {
        List<Path> savedPaths = new ArrayList<>();
        if (paths == null) {
            throw new IllegalArgumentException("Paths cannot be null");
        }
        for (Path path : paths) {
            savedPaths.add(addPath(path));
        }
        return savedPaths;
    }

    public List<Path> getAllPaths() {
        return pathRepository.findAll();
    }

    public Path getPathById(Long id) {
        return pathRepository.findById(id).orElse(null);
    }

    public void deletePathById(Long id) {
        pathRepository.deleteById(id);
    }}