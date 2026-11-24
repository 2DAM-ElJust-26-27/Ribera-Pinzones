import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.connectantribera.infra.ConnexioBD;

public class Repositori {
    late final String urlBase;
    late HealthyApi api;

    Repositori(this.urlBase) {
        this.urlBase = urlBase;
        this.api = HealthyApi(urlBase);
    }

    Future <List<Cataleg>> llistarCataleg() async {
        return (await api.llistarCataleg()).map(((cataleg) {
            return Cataleg.fromJson(CatalegJson, urlBase);
        })).toList();
    }


}
