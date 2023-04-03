/**
 * Write a description of class ProgramBarang here.
 *
 * @author Dwiki Herdiansyah - 201943500183
 * @version 20210228
 */

// package
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.dwiki.CommandLineTable;
public class ProgramBarang
{
    // inisiasi global variables
    private static Connection db;
    private static int current_page = 1;
    private static String message = "";
    
    /**
     * fungsi clear screen
     */
    private static void clrscr()
    {
        System.out.print('\u000C');
    }
    
    
    /**
     * fungsi connect ke database
     * kalau kosong maka database akan dibuat.
     * @param String db_name
     * return Connection atau null dan exit program jika error
     */
    private static void database(String db_name)
    {
       try {
           // inisiasi database
           Class.forName("org.sqlite.JDBC");
           db = DriverManager.getConnection("jdbc:sqlite:"+db_name);
       } catch (SQLException e) {
           // munculkan error sql exception
           System.err.println(e.getMessage());
       } catch (Exception e) {
           // munculkan error exception
           System.err.println(e.getMessage());
       } finally { 
           // jalankan fungsi migrasi table database
           migration();
       }
    }
    
    /**
     * Disconnect database
     * return Connection
     */
    private static void disconnect()
    {
        try {
            if (db != null) {
                db.close();
                db = null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Fungsi migrasi
     * untuk membuat table jika di database belum ada.
     */
    private static void migration()
    {
        // menjalankan fungsi buat table database
        tableBarang();
        tableBarangStok();
        tableTransaksiBarang();
        tableTransaksiBarangDetail();
    }
    
    /**
     * Fungsi cek table database ada atau tidak
     * @param String tb_name
     * @return boolean
     */
    private static boolean checkExistTable(String tb_name)
    {
        boolean ret = false;
        try {
            DatabaseMetaData dbm = db.getMetaData();
            ResultSet tbl = dbm.getTables(null, null, tb_name, null);
            if (tbl.next()) {
                ret = true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return ret;
    }
    
    /**
     * Fungsi buat table barang
     */
    private static void tableBarang()
    {
        String table = "t_barang";
        String sql   = "";
        try {
            // cek apakah table t_barang ada.
            if (checkExistTable(table) == false) {
                sql += "create table "+table+"("
                    +   "kode varchar(10) unique not null,"
                    +   "nama text default null,"
                    +   "harga numeric default 0,"
                    +   "created_at timestamp default current_timestamp,"
                    +   "updated_at timestamp null,"
                    +   "deleted_at timestamp default null"
                    + ");";
            }
            
            // jika variable sql tidak kosong maka jalankan fungsi buat table t_barang
            if (sql != "") {
                Statement stat = db.createStatement();
                stat.executeUpdate(sql);
                stat.close();
            }
        } catch (SQLException e) {
            //munculkan error sql exception
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Fungsi buat table stok barang
     */
    private static void tableBarangStok()
    {
        String table = "t_barang_stok";
        String sql   = "";
        try {
            // cek apakah table t_barang_stok ada.
            if (checkExistTable(table) == false) {
                sql += "create table "+table+"("
                    +   "t_barang_kode varchar(10) not null,"
                    +   "jumlah integer default 0,"
                    +   "created_at timestamp default current_timestamp"
                    + ");";
            }
            
            // jika variable sql tidak kosong maka jalankan fungsi buat table t_barang_stok
            if (sql != "") {
                Statement stat = db.createStatement();
                stat.executeUpdate(sql);
                stat.close();
            }
        } catch (SQLException e) {
            //munculkan error sql exception
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Fungsi buat table transaksi barang
     */
    private static void tableTransaksiBarang()
    {
        String table = "t_transaksi_barang";
        String sql   = "";
        try {
            // cek apakah table t_transaksi_barang ada.
            if (checkExistTable(table) == false) {
                sql += "create table "+table+"("
                    +   "struk varchar(100) unique not null,"
                    +   "created_at timestamp default current_timestamp"
                    + ");";
            }
            
            // jika variable sql tidak kosong maka jalankan fungsi buat table t_transaksi_barang
            if (sql != "") {
                Statement stat = db.createStatement();
                stat.executeUpdate(sql);
                stat.close();
            }
        } catch (SQLException e) {
            //munculkan error sql exception
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Fungsi buat table detail transaksi barang
     */
    private static void tableTransaksiBarangDetail()
    {
        String table = "t_transaksi_barang_detail";
        String sql   = "";
        try {
            // cek apakah table t_transaksi_barang ada.
            if (checkExistTable(table) == false) {
                sql += "create table "+table+"("
                    +   "t_transaksi_barang_struk varchar(100) not null,"
                    +   "t_barang_kode varchar(10) not null,"
                    +   "jumlah integer default 0,"
                    +   "created_at timestamp default current_timestamp"
                    + ");";
            }
            
            // jika variable sql tidak kosong maka jalankan fungsi buat table t_transaksi_barang
            if (sql != "") {
                Statement stat = db.createStatement();
                stat.executeUpdate(sql);
                stat.close();
            }
        } catch (SQLException e) {
            //munculkan error sql exception
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Fungsi cek kode barang ada atau tidak
     * @param String kode_barang
     * return boolean
     */
    private static boolean checkItemCode(String kode_barang) {
        boolean ret = true;
        try {
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("select * from t_barang where upper(kode) = '"+kode_barang+"'");
            
            if(!rs.next()) {
                ret = false;
            }
        } catch (SQLException e) {
            ret = false;
        }
        
        return ret;
    }
    
    /**
     * Fungsi untuk menampilkan notifikasi
     */
    private static void getNotification()
    {
        if(message != "") {
            System.out.println("Notifikasi: "+message);
            message = "";
        }
    }
    
    /**
     * Fungsi untuk membuat notifikasi
     * @param String msg
     * return this message
     */
    private static void setNotification(String msg)
    {
        message = msg;
    }
    
    /**
     * Fungsi untuk menampilkan tanggal hari ini
     * @param String format
     * return String
     */
    private static String currentDatetime(String format)
    {
        // yyyy-MM-dd HH:mm:ss
        return new SimpleDateFormat(format).format(new Date());
    }
    
    
    /**
     * fungsi main
     */
    public static void main (String[] args)
    {
        clrscr();
        database("uap.db"); // menjalankan fungsi konek database
        pages(current_page);
    }
    
    /**
     * Fungsi pilih opsi menu
     */
    private static void chooseMenuOption()
    {
        Scanner inp = new Scanner(System.in);
        int selectedOption;
        
        System.out.println();
        System.out.print("Pilih Opsi Menu: ");
        selectedOption = inp.nextInt();
        
        // menjalankan fungsi pages untuk menuju menu yang dipilih
        pages(selectedOption);
    }
    
    /**
     * Fungsi untuk kembali ke halaman sebelumnya
     * @param int page
     * return halaman sebelumnya
     */
    private static void backOption(int page)
    {
        Scanner inp = new Scanner(System.in);
        
        System.out.println();
        System.out.print("Tekan \"ENTER\" untuk kembali.");
        inp.nextLine();
        pages(page);
    }
    
    /**
     * Fungsi opsi untuk melanjutkan program
     * return boolean atau null jika salah
     */
    private static int chooseOptToContinue()
    {
        Scanner inp = new Scanner(System.in);
        String selectedOption;
        int ret = 0;
        
        System.out.print("Ketik \"Y\" untuk melanjutkan atau \"T\" untuk kembali: ");
        selectedOption = inp.next();
        selectedOption = selectedOption.toLowerCase(); 
        
        switch(selectedOption)
        {
            case "y":
                ret = 1;
            break;
            
            case "t":
                ret = 2;
            break;
            
            default:
                ret = 0;
        }
        
        return ret;
    }
    
    /**
     * Fungsi pindah halaman
     * @param page
     * return fungsi page
     */
    private static void pages(int page)
    {
        switch(page) {
            case 1:
                current_page = page;
                listMenuOptPage();
            break;
            
            case 2:
                current_page = page;
                listItemPage();
            break;
            
            case 3:
                current_page = page;
                addItemPage();
            break;
            
            case 4:
                current_page = page;
                changeItemPage();
            break;
            
            case 5:
                current_page = page;
                deleteItemPage();
            break;
            
            case 6:
                current_page = page;
                addStockItemPage();
            break;
            
            case 7:
                current_page = page;
                transItemPage();
            break;
            
            case 8:
                current_page = page;
                historyTransItemPage();
            break;
            
            case 0:
                exitPage();
            break;
            
            default:
                setNotification("Opsi menu tidak ditemukan.");
                pages(current_page);
        }
    }
    
    /**
     * Halama Daftar Opsi Menu
     */
    private static void listMenuOptPage()
    {
        clrscr(); // clear screen
        getNotification(); // fungsi notifikasi
        
        // table opsi
        CommandLineTable cliTable = new CommandLineTable();
        cliTable.setShowVerticalLines(true);
        cliTable.setHeaders("No", "Opsi Menu");
        cliTable.addRow("1", "Daftar Opsi Menu");
        cliTable.addRow("2", "Daftar Barang");
        cliTable.addRow("3", "Tambah Barang");
        cliTable.addRow("4", "Ubah Barang");
        cliTable.addRow("5", "Hapus Barang");
        cliTable.addRow("6", "Tambah Stok Barang");
        cliTable.addRow("7", "Transaksi Barang");
        cliTable.addRow("8", "Riwayat Transaksi Barang");
        cliTable.addRow("0", "Keluar Aplikasi");
        cliTable.print();
        
        chooseMenuOption();
    }
    
    /**
     * Tampilan table daftar barang
     */
    private static void tableItem()
    {
        // table opsi
        CommandLineTable cliTable = new CommandLineTable();
        cliTable.setShowVerticalLines(true);
        cliTable.setHeaders("Kode Barang", "Nama Barang", "Harga Barang", "Stok Barang");
        
        try {
            String sql      = "select "
                            + "a.kode, "
                            + "a.nama, "
                            + "a.harga, "
                            + "case "
                            + "when "
                            + "(select sum(jumlah) from t_barang_stok where t_barang_kode = a.kode) is not null "
                            + "and (select sum(jumlah) from t_transaksi_barang_detail where t_barang_kode = a.kode) is not null "
                            + "then (select sum(jumlah) from t_barang_stok where t_barang_kode = a.kode) - (select sum(jumlah) from t_transaksi_barang_detail where t_barang_kode = a.kode) "
                            + "when "
                            + "(select sum(jumlah) from t_barang_stok where t_barang_kode = a.kode) is not null "
                            + "and (select sum(jumlah) from t_transaksi_barang_detail where t_barang_kode = a.kode) is null "
                            + "then (select sum(jumlah) from t_barang_stok where t_barang_kode = a.kode) "
                            + "when "
                            + "(select sum(jumlah) from t_barang_stok where t_barang_kode = a.kode) is null "
                            + "and (select sum(jumlah) from t_transaksi_barang_detail where t_barang_kode = a.kode) is not null "
                            + "then 0 - (select sum(jumlah) from t_transaksi_barang_detail where t_barang_kode = a.kode) "
                            + "else 0 "
                            + "end as stok "
                            + "from t_barang a "
                            + "where a.deleted_at is null "
                            + "order by a.nama asc;";
            Statement st    = db.createStatement();
            ResultSet rs    = st.executeQuery(sql);
            while (rs.next()) {
                cliTable.addRow(rs.getString("kode"), rs.getString("nama"), Integer.toString(rs.getInt("harga")), Integer.toString(rs.getInt("stok")));
            }
        } catch (SQLException e) {
            // munculkan error sql exception
            System.err.println(e.getMessage());
        }
        
        cliTable.print();
    }
    
    /**
     * Tampilan table daftar transaksi
     */
    private static void tableTransaction()
    {
        // table opsi
        CommandLineTable cliTable = new CommandLineTable();
        cliTable.setShowVerticalLines(true);
        cliTable.setHeaders("Nomor Struk", "Kode Barang", "Nama Barang", "Jumlah Yang Dibeli", "Tanggal Transaksi");
        
        try {
            String sql      = "select "
                            + "a.t_transaksi_barang_struk as nomor_struk, "
                            + "a.t_barang_kode as kode_barang, "
                            + "b.nama as nama_barang, "
                            + "a.jumlah, "
                            + "a.created_at "
                            + "from t_transaksi_barang_detail a "
                            + "left join t_barang b on b.kode = a.t_barang_kode "
                            + "order by a.created_at desc;";
            Statement st    = db.createStatement();
            ResultSet rs    = st.executeQuery(sql);
            while (rs.next()) {
                cliTable.addRow(
                    rs.getString("nomor_struk"), 
                    rs.getString("kode_barang"), 
                    rs.getString("nama_barang"), 
                    Integer.toString(rs.getInt("jumlah")), 
                    rs.getString("created_at")
               );
            }
        } catch (SQLException e) {
            // munculkan error sql exception
            System.err.println(e.getMessage());
        }
        
        cliTable.print();
    }
    
    /**
     * Halaman Daftar Barang
     */
    private static void listItemPage() 
    {
        clrscr(); // clear screen
        getNotification(); // notifikasi
        
        //table daftar barang
        tableItem();
        
        // enter untuk kembali kehalaman sebelumnya
        backOption(1);
    }
    
    /**
     * Halaman Tambah Barang
     */
    private static void addItemPage()
    {
        clrscr(); // clear screen
        getNotification(); // notifikasi
        
        int choose = chooseOptToContinue();
        
        if (choose == 0) {
            setNotification("Opsi tidak ditemukan");
            pages(current_page);
        } else if (choose == 2) {
            pages(1);
        }
        
        clrscr(); // clear screen
        CommandLineTable cliTable = new CommandLineTable();
        cliTable.addRow("-- Tambah Barang --");
        cliTable.print();
        
        Scanner inp = new Scanner(System.in);
        String kode_barang, nama_barang;
        int harga_barang;
        
        System.out.println();
        System.out.print("Masukan Kode Barang: "); kode_barang = inp.nextLine();
        System.out.print("Masukan Nama Barang: "); nama_barang = inp.nextLine();
        System.out.print("Masukan Harga Barang: "); harga_barang = inp.nextInt();
        
        try {
            PreparedStatement ps = db.prepareStatement("insert into t_barang (kode,nama,harga) values (?,?,?)");
            ps.setString(1, kode_barang);
            ps.setString(2, nama_barang);
            ps.setInt(3, harga_barang);
            ps.executeUpdate();
            ps.close();
            
            setNotification("Data barang berhasil ditambah");
        } catch (SQLException e) {
            setNotification(e.getMessage());
        } finally {
            pages(current_page);
        }
    }
    
    /**
     * Halaman Ubah Barang
     */
    private static void changeItemPage()
    {
        clrscr(); // clear screen
        getNotification(); // notifikasi
        
        int choose = chooseOptToContinue();
        
        if (choose == 0) {
            setNotification("Opsi tidak ditemukan");
            pages(current_page);
        } else if (choose == 2) {
            pages(1);
        }
        
        clrscr(); // clear screen
        CommandLineTable cliTable = new CommandLineTable();
        cliTable.addRow("-- Ubah Barang --");
        cliTable.print();
        
        System.out.println();
        tableItem();
        System.out.println();
        
        Scanner inp = new Scanner(System.in);
        String kode_barang, nama_barang = "";
        int harga_barang = 0;
        
        System.out.print("Masukan kode barang: "); kode_barang = inp.nextLine().toUpperCase();
        
        // cek apakah kode ada atau tidak
        boolean checkCode = checkItemCode(kode_barang);
        if(checkCode == false) {
            setNotification("Kode barang tidak ditemukan.");
            pages(current_page);
        }
        
        try {
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("select nama, harga from t_barang where upper(kode) = '"+kode_barang+"'");
            while(rs.next()) {
                System.out.print("Masukan nama baru ["+rs.getString("nama")+"]: "); nama_barang = inp.nextLine();
                System.out.print("Masukan harga baru ["+rs.getInt("harga")+"]: "); harga_barang = inp.nextInt();
                nama_barang = nama_barang.isEmpty() ? rs.getString("nama") : nama_barang;
            }
        } catch (SQLException e) {
            setNotification(e.getMessage());
            pages(current_page);
        }
        
        try {
            PreparedStatement ps = db.prepareStatement("update t_barang set nama = ?, harga = ?, updated_at = ? where upper(kode) = ?");
            ps.setString(1, nama_barang);
            ps.setInt(2, harga_barang);
            ps.setString(3, currentDatetime("yyyy-MM-dd HH:mm:ss"));
            ps.setString(4, kode_barang);
            ps.executeUpdate();
            ps.close();
            
            setNotification("Data barang berhasil diubah");
        } catch (SQLException e) {
            setNotification(e.getMessage());
        } finally {
            pages(current_page);
        }
    }
    
    /**
     * Halaman Hapus Barang
     */
    private static void deleteItemPage()
    {
        clrscr(); // clear screen
        getNotification(); // notifikasi
        
        int choose = chooseOptToContinue();
        
        if (choose == 0) {
            setNotification("Opsi tidak ditemukan");
            pages(current_page);
        } else if (choose == 2) {
            pages(1);
        }
        
        clrscr(); // clear screen
        CommandLineTable cliTable = new CommandLineTable();
        cliTable.addRow("-- Hapus Barang --");
        cliTable.print();
        
        System.out.println();
        tableItem();
        System.out.println();
        
        Scanner inp = new Scanner(System.in);
        String kode_barang;
        
        System.out.print("Masukan kode barang: "); kode_barang = inp.nextLine().toUpperCase();
        
        // cek apakah kode ada atau tidak
        boolean checkCode = checkItemCode(kode_barang);
        if(checkCode == false) {
            setNotification("Kode barang tidak ditemukan.");
            pages(current_page);
        }
        
        try {
            PreparedStatement ps = db.prepareStatement("update t_barang set updated_at = ?, deleted_at = ? where upper(kode) = ?");
            ps.setString(1, currentDatetime("yyyy-MM-dd HH:mm:ss"));
            ps.setString(2, currentDatetime("yyyy-MM-dd HH:mm:ss"));
            ps.setString(3, kode_barang);
            ps.executeUpdate();
            ps.close();
            
            setNotification("Data barang berhasil dihapus");
        } catch (SQLException e) {
            setNotification(e.getMessage());
        } finally {
            pages(current_page);
        }
    }
    
    /**
     * Halaman Tambah Stok Barang
     */
    private static void addStockItemPage()
    {
        clrscr(); // clear screen
        getNotification(); // notifikasi
        
        int choose = chooseOptToContinue();
        
        if (choose == 0) {
            setNotification("Opsi tidak ditemukan");
            pages(current_page);
        } else if (choose == 2) {
            pages(1);
        }
        
        clrscr(); // clear screen
        CommandLineTable cliTable = new CommandLineTable();
        cliTable.addRow("-- Tambah Stok Barang --");
        cliTable.print();
        
        System.out.println();
        tableItem();
        System.out.println();
        
        Scanner inp = new Scanner(System.in);
        String kode_barang;
        int jumlah_barang;
        
        System.out.print("Masukan kode barang: "); kode_barang = inp.nextLine().toUpperCase();
        
        // cek apakah kode ada atau tidak
        boolean checkCode = checkItemCode(kode_barang);
        if(checkCode == false) {
            setNotification("Kode barang tidak ditemukan.");
            pages(current_page);
        }
        
        System.out.print("Masukan jumlah barang masuk: "); jumlah_barang = inp.nextInt();
        
        try {
            PreparedStatement ps = db.prepareStatement("insert into t_barang_stok (t_barang_kode,jumlah) values (?,?)");
            ps.setString(1, kode_barang);
            ps.setInt(2, jumlah_barang);
            ps.executeUpdate();
            ps.close();
            
            setNotification("Stok barang berhasil ditambah");
        } catch (SQLException e) {
            setNotification(e.getMessage());
        } finally {
            pages(current_page);
        }
    }
    
    /**
     * Halaman transaksi barang
     */
    private static void transItemPage()
    {
        clrscr(); // clear screen
        getNotification(); // notifikasi
        
        int choose = chooseOptToContinue();
        
        if (choose == 0) {
            setNotification("Opsi tidak ditemukan");
            pages(current_page);
        } else if (choose == 2) {
            pages(1);
        }
        
        clrscr(); // clear screen
        CommandLineTable cliTable = new CommandLineTable();
        cliTable.addRow("-- Transaksi Barang --");
        cliTable.print();
        
        System.out.println();
        tableItem();
        System.out.println();
        
        Scanner inp = new Scanner(System.in);
        String trans[][];
        int banyak_barang;
        
        System.out.print("Masukan banyak barang: "); banyak_barang = inp.nextInt();
        trans = new String[banyak_barang][2];
        
        for(int i = 0; i < banyak_barang; i++) {
            inp = new Scanner(System.in);
            
            System.out.println();
            cliTable = new CommandLineTable();
            cliTable.addRow("-- Barang Ke "+(i+1)+" --");
            cliTable.print();
            System.out.println();
            
            System.out.print("Masukan kode barang: "); trans[i][0] = inp.nextLine().toUpperCase();
            
            // cek apakah kode ada atau tidak
            boolean checkCode = checkItemCode(trans[i][0]);
            if(checkCode == false) {
                setNotification("Kode barang tidak ditemukan.");
                pages(current_page);
            }
            
            System.out.print("Masukan jumlah barang yang dibeli: "); trans[i][1] = Integer.toString(inp.nextInt());
        }
        
        inp = new Scanner(System.in);
        
        try {
            String nomor_struk = currentDatetime("yyyyMMddHHmmss");
            PreparedStatement ps;
            ps = db.prepareStatement("insert into t_transaksi_barang (struk) values (?)");
            ps.setString(1, nomor_struk);
            ps.executeUpdate();
            ps.close();
            
            for(int i = 0; i < banyak_barang; i++) {
                ps = db.prepareStatement("insert into t_transaksi_barang_detail (t_transaksi_barang_struk, t_barang_kode, jumlah) values (?,?,?)");
                ps.setString(1, nomor_struk);
                ps.setString(2, trans[i][0]);
                ps.setInt(3, Integer.parseInt(trans[i][1]));
                ps.executeUpdate();
                ps.close();
            }
            
            setNotification("Transaksi barang berhasil ditambah");
        } catch (SQLException e) {
            setNotification(e.getMessage());
        } finally {
            pages(current_page);
        }
    }
    
    /**
     * Halama Riwayat Transaksi
     */
    private static void historyTransItemPage()
    {
        clrscr(); // clear screen
        getNotification(); // notifikasi
        
        //table daftar riwayat transaksi
        tableTransaction();
        
        // enter untuk kembali kehalaman sebelumnya
        backOption(1);
    }
    
    /**
     * Halaman Keluar Aplikasi
     */
    private static void exitPage()
    {
        clrscr();
        System.out.println("Keluar Aplikasi.");
        disconnect(); // disconnect database
        System.exit(0);
    }
}
