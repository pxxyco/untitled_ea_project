package it.unical.ea_project.service;

import it.unical.ea_project.domain.Stage;
import it.unical.ea_project.domain.Stage.StageCategory;
import it.unical.ea_project.domain.Trip;
import java.util.List;

public interface StageService {

    List<Stage> getItineraryForTrip(Trip trip);

    List<Stage> getStagesByTripAndCategory(Trip trip, StageCategory category);

    List<Stage> getStagesForDay(Trip trip, Integer day);

    Stage addStageToTrip(Trip trip, Stage stage);

    void removeStageFromTrip(Trip trip, Stage stage);

}
