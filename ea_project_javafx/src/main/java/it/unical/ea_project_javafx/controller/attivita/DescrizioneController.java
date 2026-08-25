package it.unical.ea_project_javafx.controller.attivita;

import it.unical.ea_project_javafx.dto.ActivityDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DescrizioneController implements  ActivitySectionController{

    @FXML
    private Label lblDescrizione;
    @FXML
    private Label lblNota;

    @Override
    public void setData(ActivityDTO activity) {

        if(activity.getDescription() !=null){
            System.out.println(activity.getDescription());
            lblDescrizione.setText(activity.getDescription());
        }

        if(activity.getNotes() !=null){
            lblNota.setText("NOTE AGGIUNTIVE: \n\n" + activity.getNotes());
        }


    }

}
