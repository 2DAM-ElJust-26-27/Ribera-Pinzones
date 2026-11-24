import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.connectantribera.infra.ConnexioBD;
import org.

public class Repositori {
    public List<?> llistarTipus(Connection connexio) throws SQLException {
        List<?> llista = new ArrayList<?>();
        String sql = "SELECT id, nom FROM ribera.categoria_servei ORDER BY nom";
        try (PreparedStatement sentencia = connexio.prepareStatement(sql)) {
            try (ResultSet rs = sentencia.executeQuery()) {
                while (rs.next()) {
                    llista.add(maparTipus(rs.getString("tipus")));
                }
            }
        }
        return llista;
    }
}
