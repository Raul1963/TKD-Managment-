package org.example.tkdmng;

import org.example.tkdmng.Controller.TKDController;
import org.example.tkdmng.Exceptions.DatabaseException;
import org.example.tkdmng.Service.TKD_Service;
import org.example.tkdmng.Ui.TKDUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TkdMngApplication {

	public static void main(String[] args) throws DatabaseException {
		SpringApplication.run(TkdMngApplication.class, args);
		TKDUI newUi = new TKDUI();
		TKD_Service tkdService = newUi.startRepo();
		TKDController tkdController = new TKDController(tkdService);
		newUi.setTkdController(tkdController);
		newUi.start();
	}

}
