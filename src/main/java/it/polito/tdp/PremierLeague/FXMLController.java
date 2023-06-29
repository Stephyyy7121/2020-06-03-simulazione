/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Opponente;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.TopPlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnTopPlayer"
    private Button btnTopPlayer; // Value injected by FXMLLoader

    @FXML // fx:id="btnDreamTeam"
    private Button btnDreamTeam; // Value injected by FXMLLoader

    @FXML // fx:id="txtK"
    private TextField txtK; // Value injected by FXMLLoader

    @FXML // fx:id="txtGoals"
    private TextField txtGoals; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    private boolean creaGrafo = false;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	String input = this.txtGoals.getText();
    	if (input =="") {
    		this.txtResult.setText("Non e' stato inserito un valore");
    	}
    	Double goal = 0.0;
    	try {
    		goal = Double.parseDouble(input);
    	}catch (NumberFormatException e) {
    		txtResult.setText("Non e' stato inserito un valore valido");
    		return ;
    	}
    	this.model.creaGrafo(goal);
    	this.creaGrafo = true;
    	this.txtResult.setText("Grafo Creato! \n#Vertici: "+ this.model.getNumVertici()+ "\n#Archi: " + this.model.getNumArchi()+"\n");
    }

    @FXML
    void doDreamTeam(ActionEvent event) {
    	
    	txtResult.clear();

    	if (!this.creaGrafo) {
    		txtResult.clear();
    		txtResult.setText("Non e' stato creato un grafo");
    	}
    	
    	String input = txtK.getText();
    	
    	int k = 0;
    
    	try {
    		k = Integer.parseInt(input);
    	}catch (NumberFormatException e) {
    		txtResult.appendText("Errore");
    		return;
    	}
    	
    	List<Player> dream  = this.model.getDreamTeam(k);
    	
    	for (Player p : dream) {
    		txtResult.appendText(p.toString() + "\n");
    	}

    }

    @FXML
    void doTopPlayer(ActionEvent event) {
    	
    	if (!this.creaGrafo) {
    		txtResult.clear();
    		txtResult.setText("Non e' stato creato un grafo");
    	}
    	TopPlayer top = this.model.getTopPlayer();
    	txtResult.appendText("Top player: " + top +"\n");
    	txtResult.appendText("Avversari: \n");
    	for (Opponente o : top.getOpponenti()) {
    		
    		txtResult.appendText(o.toString()+"\n");
    		
    	}
    	

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnTopPlayer != null : "fx:id=\"btnTopPlayer\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnDreamTeam != null : "fx:id=\"btnDreamTeam\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtK != null : "fx:id=\"txtK\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtGoals != null : "fx:id=\"txtGoals\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
