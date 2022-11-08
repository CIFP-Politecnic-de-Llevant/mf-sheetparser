package cat.iesmanacor.gestsuitesheetparser.restclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


@FeignClient(name = "core")
public interface CoreRestClient {

    @PostMapping("/google/sheets/getSpreadsheetDataTable")
    List<List<String>> getSpreadsheetDataTable(@RequestBody String idSheet) throws GeneralSecurityException, IOException;

    @PostMapping("/gsuite/sendemail")
    void sendEmail(@RequestBody String json) throws IOException, MessagingException, GeneralSecurityException;
}
