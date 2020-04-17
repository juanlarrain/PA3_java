package logica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Multa;
import model.Respuesta;

public class Servicio {
    
    public ArrayList<Multa> getMultas() {
        ArrayList<Multa> lstMultas = new ArrayList<>();
        try {
            Connection con = Conexion.startConeccion();
            Statement statement = con.createStatement();
            String query = "SELECT * FROM multa ORDER BY monto DESC";
            ResultSet resultSet = statement.executeQuery(query);
            
            Multa objMulta;
            while(resultSet.next()) { // Se ejecuta la misma cantidad de veces que filas tiene la tabla
                objMulta = new Multa();
                String dni       = resultSet.getString("dni");
                String tipoMulta = resultSet.getString("tipo_multa");
                Double multa     = resultSet.getDouble("monto");
                String correo    = resultSet.getString("correo");
                int punto        = resultSet.getInt("punto");
                
                objMulta.setDni(dni);
                objMulta.setMulta(tipoMulta);
                objMulta.setMonto(multa);
                objMulta.setCorreo(correo);
                objMulta.setPunto(punto);
                lstMultas.add(objMulta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstMultas;
    }
    
    public Respuesta insertarMulta(Multa multa) {
        Respuesta rpta = new Respuesta();
        try {
            if(multa.getMonto() > 1000) {
                rpta.setCodigo(1);
                rpta.setMsj("La multa no puede ser mayor a 1000 soles");
                return rpta;
            }
            if(multa.getMulta().equalsIgnoreCase("Pico placa") && multa.getMonto() < 500 ) {
                rpta.setCodigo(2);
                rpta.setMsj("La multa para pico y placa no puede ser menor a 500");
                return rpta;
            }
            Connection con = Conexion.startConeccion();
            String query = "INSERT INTO `sat`.`multa` (`dni`, `tipo_multa`, `monto`, `correo`, `punto`) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, multa.getDni());
            ps.setString(2, multa.getMulta());
            ps.setDouble(3, multa.getMonto());
            ps.setString(4, multa.getCorreo());
            ps.setInt(5, multa.getPunto());
            ps.executeUpdate();
            rpta.setCodigo(0); // 0 = no error
            rpta.setMsj("Se registró la multa.");
        } catch (Exception e) {
            e.printStackTrace();
            rpta.setCodigo(-1);
            rpta.setMsj("Hubo un error al registrar la multa.");
        }
        return rpta;
    }
}