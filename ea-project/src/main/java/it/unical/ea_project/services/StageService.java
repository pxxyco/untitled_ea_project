package it.unical.ea_project.services;

import it.unical.ea_project.domain.Stage;
import it.unical.ea_project.domain.Stage.StageCategory;
import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.repositories.StageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StageService {

    private final StageRepository stageRepository;

    public StageService(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    public List<Stage> getItineraryForTrip(Trip trip) {
        return stageRepository.findByTripOrderByDayAscOrderInDayAsc(trip);
    }

    public List<Stage> getStagesByTripAndCategory(Trip trip, StageCategory category) {
        return stageRepository.findByTripAndCategory(trip, category);
    }

    public List<Stage> getStagesForDay(Trip trip, Integer day) {
        return stageRepository.findByTripAndDayOrderByOrderInDayAsc(trip, day);
    }

    @Transactional
    public Stage addStageToTrip(Trip trip, Stage stage) {
        trip.addStage(stage);
        return stageRepository.save(stage);
    }

    @Transactional
    public void removeStageFromTrip(Trip trip, Stage stage) {
        trip.removeStage(stage);
        stageRepository.delete(stage);
    }
}
