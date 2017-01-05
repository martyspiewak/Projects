import java.util.HashMap;

@SuppressWarnings("serial")
public class Database implements java.io.Serializable {
	
	private HashMap<String, Table> tables;
	
	protected Database() {
		tables = new HashMap<String, Table>();
	}

	protected HashMap<String, Table> getTables() {
		return tables;
	}

	protected void setTables(HashMap<String, Table> tables) {
		this.tables = tables;
	}
	
	protected void addTable(String str, Table table) {
		tables.put(str, table);
	}
	
	protected Table getTable(String name) {
		return tables.get(name);
	}

}
