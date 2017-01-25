/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package watchdir;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcin
 */
public class ms_dbManager {

    private final String connect_string;
    private final String user;
    private final String password;
    private final String baza;
    private Connection conn;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat dateShort = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat rok_2 = new SimpleDateFormat("YY");
    DateFormat rok_4 = new SimpleDateFormat("yyyy");
    DateFormat miesi = new SimpleDateFormat("MM");

    private long aktDokID;

    public ms_dbManager(String connect_string, String user, String password, String baza) {
        this.connect_string = connect_string;
        this.user = user;
        this.password = password;
        this.baza = baza;

        //tworze połącznie do bazy danych 
        try {
            this.conn = dbConnect(connect_string + ";databaseName=" + baza, user, password);
            //aktDokID = new aktDokId(conn).wardok(); //pobieram aktualny DokId na jakim będę pracował 
        } catch (SQLException ex) {
            Logger.getLogger(ms_dbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getBaza() {
        return baza;
    }

    private Connection dbConnect(String db_connect_string,
            String db_userid,
            String db_password
    ) throws SQLException {

        Connection lacze = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            lacze = DriverManager.getConnection(db_connect_string,
                    db_userid, db_password);
            System.out.println("connected");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
        return lacze;

    }

    public void updateSql(String ss) throws SQLException {

        try {
            Statement st = this.conn.createStatement();
            System.out.println(ss);
            st.executeUpdate(ss);
        } catch (SQLException ex) {

            Logger.getLogger(ms_dbManager.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    public ResultSet zapySql(String ss) throws SQLException {

        Statement st;
        try {
            st = this.conn.createStatement();
        } catch (SQLException ex) {

            Logger.getLogger(ms_dbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        //System.out.println(ss);
        return st.executeQuery(ss);

    }

    //funkcja cene towarów 
    public String get_price(String towid) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select "
                + " Round(CenaDet*(1+(CAST( Stawka AS DECIMAL))/10000),2) as CenaDet,"
                + "CenaEw,CenaHurt "
                + "from Towar where TowId = " + towid);
        while (n.next()) {
            p = p + n.getString("CenaEw") + ";"
                    + n.getString("CenaDet") + ";"
                    + n.getString("CenaHurt") + ";";

        }

        byte ptext[] = p.getBytes(ISO_8859_1);
        String value = new String(ptext, UTF_8);
        return value;
    }

    public boolean check_price(String kod_kreskowy, String cena_det) throws SQLException {
        String p;
        ResultSet n;
        n = zapySql("use " + baza + " select "
                + " Round(CenaDet*(1+(CAST( Stawka AS DECIMAL))/10000),2) as CenaDet  "
                + "from Towar where Kod = '" + kod_kreskowy + "'");
        if (n.next()) {
            p = n.getString("CenaDet").replace(".0000000", "");
            System.out.println(p + " | " + cena_det);
            if (p.equals(cena_det)) {
                return true;
            }

        } else {
            System.out.println("brak towaru o kodzie " + kod_kreskowy);
            return true;
        }
        return false;
    }

    //funkcja zwraca listę kontrahentów 
    public String get_kontra(String lastUP) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select * from Kontrahent " + lastUP);
        while (n.next()) {
            p = p + n.getString("Nazwa") + ";" + n.getString("Ulica") + ";";

        }

        byte ptext[] = p.getBytes(ISO_8859_1);
        String value = new String(ptext, UTF_8);
        return value;
    }

    public boolean add_asort(String asort_name) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select nazwa  from asort  where nazwa = '" + asort_name.replace("'", "''") + "'");
        if (n.next()) {
            p = n.getString("Nazwa");

        } else {
            updateSql(" insert into asort(Nazwa,Marza,OpcjaMarzy,HurtRabat,OpcjaRabatu,NocNarzut,OpcjaNarzutu) values ('" + asort_name.replace("'", "''") + "',0,1,0,0,0,1)");
            return true;
        }
        return false;
    }

    //funkcja zwraca listę listę pozycji do walidacji  
    public void markAsValidated(String dokid) throws SQLException {

        updateSql("update dok set Opcja4 = 9 where dokid = " + dokid);
    }

    public boolean check_tow(String kod) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select nazwa  from towar  where kod = '" + kod + "'");
        if (n.next()) {
            p = n.getString("Nazwa");
            return true;

        }
        return false;
    }

    public int getTowIdByname(String tow_name) throws SQLException {

        ResultSet n;
        n = zapySql("use " + baza + " select TowId  from towar  where nazwa like upper('" + tow_name + "')");
        if (n.next()) {
            return n.getInt("TowId");

        }
        return 0;
    }

    public String getTowIdByOpis3(String Opis3) throws SQLException {

        ResultSet n;
        n = zapySql("use " + baza + " select TowId  from towar  where Opis3 =  '" + Opis3 + "'");
        if (n.next()) {
            return n.getString("TowId");

        } else {
            throw new SQLException();
        }

    }

    public String getTypeTowByOpis3(String Opis3) throws SQLException {

        ResultSet n;
        n = zapySql("use " + baza + " select TypTowaru  from towar  where Opis3 =  '" + Opis3 + "'");
        if (n.next()) {
            return n.getString("TypTowaru");

        } else {
            throw new SQLException();
        }

    }

    public String getTaxByOpis3(String Opis3) throws SQLException {

        ResultSet n;
        n = zapySql("use " + baza + " select Stawka  from towar  where Opis3 =  '" + Opis3 + "'");
        if (n.next()) {
            return n.getString("Stawka");

        } else {
            throw new SQLException();
        }

    }

    public void updOpis3TowById(int TowID, String Opis3) throws SQLException {

        updateSql("use " + baza + " update towar set Opis3 = " + Opis3 + ", Zmiana = getDate() where TowId = '" + TowID + "'");

    }

    public String getLastDokID() throws SQLException {

        ResultSet n;
        n = zapySql("use " + baza + " select (max (dokid ) ) as dokid  from dok ");
        if (n.next()) {

            return n.getString("dokid");

        }
        return "-1";
    }

    public boolean chkOrderInDB(int orderNumber) throws SQLException {
        String orde = String.valueOf(orderNumber);
        ResultSet n;
        n = zapySql("use " + baza + " select NrDok from dok where typdok = 49 and Aktywny = 1 and NrDok like '" + orde + "' or Nrdok like  '" + orde + " !!!'");
        if (n.next()) {

            return true;

        }
        return false;
    }

    public boolean insDokKontr(String DokId, String KontrId) throws SQLException {
        try {
            updateSql("use " + baza + " insert into DokKontr (DokId, KontrId) "
                    + " values (" + DokId + ","
                    + KontrId + ")");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean updateTowarChange(String TowId) throws SQLException {
        try {
            updateSql("use " + baza + " update Towar set Zmiana = getdate() where TowId = " + TowId);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /*
    update Istw set
 StanMag = StanMag + @P1
where MagId = 1 and TowId =
    
     */
    public boolean updateIstw(String Towid, String stock, String MagID, String TypTowaru) throws SQLException {
        //nie aktiualizuję jak typ towaru = 2 (Usługa)
        if (!TypTowaru.equals("2")) {
            try {
                updateSql("use " + baza + " update Istw set StanMag =  StanMag + " + stock
                        + " where  MagId = " + MagID + " and TowId = " + Towid);
            } catch (SQLException e) {
                return false;
            }
            return true;
        }
        return true;
    }

    public boolean insTekst(String DokId, String Znaczenie, String Tekst) throws SQLException {
        try {
            updateSql("use " + baza + " insert into TekstDok(DokId, Znaczenie, Tekst)"
                    + "values (" + DokId + ","
                    + Znaczenie + ","
                    + Tekst + ")");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /*
     */
    public String getEmailByNrDok(String NrDok) throws SQLException {
        ResultSet n;
        n = zapySql("use " + baza + " select email from Kontrahent where kontrid = (select kontrid from dokkontr where dokid = (select dokid from dok where NrDok like '" + NrDok + "'))");
        if (n.next()) {

            return n.getString("email");

        }

        return "@empty";
    }
}
