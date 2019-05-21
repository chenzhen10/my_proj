import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Loader extends ClassLoader {
	private ClassLoader parent;
    private String connectionString;

    public Loader(String connectionString) {
        this(ClassLoader.getSystemClassLoader(), connectionString);
    }

    public Loader(ClassLoader parent, String connectionString) {
        super(parent);
        this.parent = parent;
        this.connectionString = connectionString;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class cls = null;
        try {       	
            cls = parent.loadClass(name);
        } catch (ClassNotFoundException cnfe) {
            byte[] bytes = new byte[0];
            try {
                bytes = loadClassFromDatabase(name);
            } catch (SQLException sqle) {
                throw new ClassNotFoundException("Unable to load class", sqle);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
        return cls;
    }

    private byte[] loadClassFromDatabase(String name) throws SQLException, ClassNotFoundException {
        PreparedStatement pstmt = null;
        Connection connection = null;
        byte[] data = null;
        try {
            connection = DriverManager.getConnection(connectionString,"root","root");
            String sql = "select class from class where class_name= ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Blob blob = rs.getBlob(1);
                data = blob.getBytes(1, (int) blob.length());
                return data;
            }
        } catch (SQLException sqlex) {
            System.out.println("Unexpected exception: " + sqlex.toString());
        } catch (Exception ex) {
            System.out.println("Unexpected exception: " + ex.toString());
        } finally {
            if (pstmt != null) pstmt.close();
            if (connection != null) connection.close();
        }

        if(data == null){
            throw new ClassNotFoundException();
        }
        return data;
    }
}
	


