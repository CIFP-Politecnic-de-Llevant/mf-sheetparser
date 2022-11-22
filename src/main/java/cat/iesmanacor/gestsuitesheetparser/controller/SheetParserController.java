package cat.iesmanacor.gestsuitesheetparser.controller;

import cat.iesmanacor.common.model.Notificacio;
import cat.iesmanacor.common.model.NotificacioTipus;
import cat.iesmanacor.gestsuitesheetparser.restclient.CoreRestClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SheetParserController {

    @Autowired
    private CoreRestClient coreRestClient;

    @Autowired
    private Gson gson;



    @PostMapping("/draft")
    public ResponseEntity<List<List<String>>> getDraftAlumnes(@RequestBody String idsheet) throws GeneralSecurityException, IOException, MessagingException {
        List<List<String>> linies = coreRestClient.getSpreadsheetDataTable(idsheet);
        return new ResponseEntity<>(linies, HttpStatus.OK);
    }

    @PostMapping("/send")
    public ResponseEntity<Notificacio> sendEmailAlumnes(@RequestBody String json) throws GeneralSecurityException, IOException, MessagingException, MessagingException {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        String titol = jsonObject.get("titol").getAsString();
        String idsheet = jsonObject.get("idsheet").getAsString();
        int numHeaders = jsonObject.get("numHeaders").getAsInt();
        int numRowsAlumnes = jsonObject.get("numRowsAlumnes").getAsInt();
        int numColumnEmail = jsonObject.get("numColumnEmail").getAsInt();

        JsonObject object = new JsonObject();
        object.addProperty("spreadsheetId", idsheet);
        object.addProperty("sheetName", numHeaders);

        List<List<String>> linies = coreRestClient.getSpreadsheetDataTable(idsheet);

        int numAlumnes = (linies.size() - numHeaders) / numRowsAlumnes;
        int numColumnesTotal = linies.get(0).size();
        System.out.println("Lines" + linies.size() + " num alumnes" + numAlumnes);

        //Header
        List<List<String>> header = new ArrayList<>();

        for(int i=0; i<numHeaders; i++){
            header.add(linies.get(i));
        }

        System.out.println("header:"+header);

        //Alumnes
        List<List<List<String>>> alumnes = new ArrayList<>();
        for (int i = 0; i < numAlumnes; i++) {
            List<List<String>> alumne = new ArrayList<>();
            for(int j=0;j<numRowsAlumnes;j++){
                alumne.add(linies.get(numHeaders+(i*numRowsAlumnes)+j));
            }
            alumnes.add(alumne);
        }

        System.out.println("alumnes:"+alumnes);

        //E-mails
        String styleHeader = "border: solid 1px black; border-collapse:collapse; background: #CCCCCC; padding: 5px; text-align: center; vertical-align: center;";
        String styleAlumne = "border: solid 1px black; border-collapse:collapse; padding: 5px; text-align: right; vertical-align: center;";
        for(List<List<String>> alumne: alumnes){
            String missatge = "";
            String emailAlumne = "";

            missatge += "<table style=\"border-collapse:collapse; border-spacing: 0;\">";

            //Header
            for(List<String> filaHeader: header){
                missatge += "<tr>";
                int numColumnesHeader = 0;
                for(String columnaHeader: filaHeader){
                    missatge += "<th style=\""+styleHeader+"\">"+columnaHeader+"</th>";
                    numColumnesHeader++;
                }

                //Emplenam, si cal, les columnes restants (al full de càlcul buides)
                for(int i=numColumnesHeader; i<numColumnesTotal; i++ ){
                    missatge += "<th style=\""+styleHeader+"\"></th>";
                }
                missatge += "</tr>";
            }

            //Contingut
            for(List<String> filaAlumne: alumne){
                missatge += "<tr>";
                int numColumnesAlumne = 0;
                for(String columnaAlumne: filaAlumne){
                    missatge += "<td style=\""+styleAlumne+"\">"+columnaAlumne+"</td>";
                    numColumnesAlumne++;

                    if(numColumnesAlumne == numColumnEmail && columnaAlumne!=null && !columnaAlumne.isEmpty()){
                        emailAlumne = columnaAlumne;
                    }
                }

                //Emplenam, si cal, les columnes restants (al full de càlcul buides)
                for(int i=numColumnesAlumne; i<numColumnesTotal; i++ ){
                    missatge += "<td style=\""+styleAlumne+"\"></td>";
                }
                missatge += "</tr>";
            }

            missatge += "</table>";

            if(emailAlumne != null && !emailAlumne.isEmpty() && titol != null && !titol.isEmpty()){
                System.out.println("Email alumne"+emailAlumne+" titol:"+titol);

                JsonObject objectEmail = new JsonObject();
                objectEmail.addProperty("to", emailAlumne);
                objectEmail.addProperty("assumpte", titol);
                objectEmail.addProperty("missatge", missatge);

                coreRestClient.sendEmail(objectEmail.toString());
            }
        }


        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Correus electrònics enviats correctament.");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }
}