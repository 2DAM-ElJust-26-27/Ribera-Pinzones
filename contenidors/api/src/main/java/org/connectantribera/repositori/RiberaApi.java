import org.connectantribera.infra.ConnexioBD;
import org.connectantribera.repository.Repositori;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;

public class RiberaApi {
    String urlBase;
    RiberaApi(this.urlBase) {
        this.urlBase = urlBase;
    }

    Future <List<dynamic>> getCataleg() async {
        String url = '$urlBase/api/cataleg';
        return await getcataleg(url);
    }
}
