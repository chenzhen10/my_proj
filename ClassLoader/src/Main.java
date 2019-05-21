import by.IMemory;
import by.Memory;

public class Main {

	public static void main(String[] args) {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Loader cl = new Loader(
					"jdbc:mysql://localhost/test");
			Class<?> clazz = cl.findClass("by.Memory");

			IMemory mem = (IMemory) clazz.newInstance();
			mem.print();		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

}
