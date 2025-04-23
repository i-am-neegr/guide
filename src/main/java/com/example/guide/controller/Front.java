package com.example.guide.controller;

import com.example.guide.service.PathService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@AllArgsConstructor
@Controller
public class Front {
    private PathService pathService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/answer")
    public String handleAnswer(
            @RequestParam("start") Long startId,
            @RequestParam("end") Long endId,
            @RequestParam("time") String time,
            Model model) {
        // Валидация
        if (startId == null || endId == null || time == null || time.isEmpty()) {
            model.addAttribute("error", "Поля 'start', 'end' и 'time' обязательны");
            return "error";
        }

        try {
            // Парсинг времени (HH:MM -> минуты)
            String[] timeParts = time.split(":");
            if (timeParts.length != 2) {
                throw new IllegalArgumentException("Неверный формат времени. Используйте HH:MM");
            }
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int timeInMinutes = hours * 60 + minutes;

            // Вызов buildPath
            List<double[]> route = pathService.buildPath(startId, endId, timeInMinutes);

            // Передача данных в модель
            model.addAttribute("route", route);

            return "result"; // Соответствует answer.html
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Ошибка в формате времени: используйте числа в формате HH:MM");
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка: " + e.getMessage());
            return "error";
        }
    }
}