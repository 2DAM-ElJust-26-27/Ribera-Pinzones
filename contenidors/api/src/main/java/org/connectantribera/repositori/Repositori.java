import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.connectantribera.infra.ConnexioBD;

public class Repositori {
    public List<?> llistarCategories(String tipus) throws SQLException {
       try (Connection connexio = configuracio.obrir()) {
            return switch (tipus) {
                case "cataleg" -> repositori.llistarCataleg(connexio);
                case "centres" -> repositori.llistarCentres(connexio);
                case "contactes" -> repositori.llistarContactes(connexio);
                case "entitats" -> repositori.llistarEntitats(connexio);
                case "horaris" -> repositori.llistarHoraris(connexio);
                case "serveis" -> repositori.llistarServeis(connexio);
                case "territori" -> repositori.llistarTerritori(connexio);
                default -> throw new IllegalArgumentException(
                    "Tipus desconegut: " + tipus
                );
            };
        }
    }
}
