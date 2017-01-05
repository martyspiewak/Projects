import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateIndexQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLParser;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;
import net.sf.jsqlparser.JSQLParserException;

public class Query {
	
	private static  boolean haveDatabase;
	private static Database database;
	private static Integer queryCounter = 0;
	private static Serializer serializer = new Serializer();
	private static boolean haveSerialized = false;

	protected Query() {
		haveDatabase = false;
	}
	
	public static void main(String[] args) throws JSQLParserException {
//		Scanner s = new Scanner(System.in);
//		while(s.hasNextLine()) {
//			String str = s.nextLine();
//			execute(str);
//		}
//		s.close();
		
		try {
			//should work
			execute("CREATE TABLE table1 (Int int, Doub decimal(1,1), Str varchar(255), Bool boolean, Int2 int, PRIMARY KEY (Int))");
			
			//should work
			execute("CREATE TABLE table2 (Num int, Dec decimal(1,1), Word varchar(255), Truth boolean, Num2 int, PRIMARY KEY (Num))");
			
			//shouldn't work- already have a table with this name
			execute("CREATE TABLE table2 (Num int, Dec decimal(1,1), Word varchar(255), Truth boolean, Num2 int, PRIMARY KEY (Num))");			
			
			//the following inserts should all work
			execute("INSERT INTO table1 (Int, Str, Bool, Doub, Int2) VALUES (5, 'test', 'true', 2.3, 6)");
			
			execute("INSERT INTO table1 (Int, Str, Bool, Doub, Int2) VALUES (4, 'this', 'false', 2.7, 5)");
			
			execute("INSERT INTO table1 (Int, Str, Bool, Doub, Int2) VALUES (6, 'must', 'true', 2.3, 6)");
			
			execute("INSERT INTO table2 (Num, Word, Truth, Dec, Num2) VALUES (7, 'test', 'true', 2.3, 5)");
			
			execute("INSERT INTO table2 (Num, Word, Truth, Dec, Num2) VALUES (5, 'with', 'true', 1.3, 9)");
			
			//these should not work
			//Int is unique and that value exists
			execute("INSERT INTO table1 (Int, Str, Bool, Doub, Int2) VALUES (5, 'not', 'true', 2.8, 4)");
			
			//int is not null
			execute("INSERT INTO table1 (Str, Bool, Doub, Int2) VALUES ('not', 'true', 2.8, 4)");
			
			//these should work
			execute("UPDATE table1 SET Str = 'work'");
			
			execute("UPDATE table1 SET Str = 'work' WHERE Int = Int2");
			
			execute("UPDATE table1 SET Str = 'work' WHERE Int < Int2 AND Bool = 'true'");
			
			execute("UPDATE table1 SET Str = 'work' WHERE Int = Int2 AND (Str = 'tent' OR Bool = 'true')");
			
			//this should not work- no such row where str is table
			execute("UPDATE table1 SET Str = 'work' WHERE Int = Int2 AND Str = 'table'");
			
			//this should work
			execute("CREATE INDEX Doub_Index on table1 (Doub)");
			
			//these should work
			execute("SELECT table1.Int FROM table1 WHERE Int > 4");
			
			execute("SELECT table1.Int, table2.Num FROM table1, table2");
			
			execute("SELECT table1.Int, table2.Num FROM table1, table2 WHERE table1.Int > 4 AND table2.Num > 3");
			
			execute("SELECT * FROM table1 ORDER BY Int ASC");
			
			execute("SELECT * FROM table1 ORDER BY Doub DESC, Int DESC");
			
			execute("SELECT AVG(Int) FROM table1");
			
			execute("SELECT COUNT(Int) FROM table1");
			
			execute("SELECT MAX(Int) FROM table1");
			
			execute("SELECT MIN(Int) FROM table1");
			
			execute("SELECT SUM(Int) FROM table1");
			
			//these should not work
			execute("SELECT table1.Fail, table2.Num FROM table1, table2");
			
			execute("SELECT SUM(Fail) FROM table1");
			
			//these should work
			execute("DELETE FROM table1 WHERE Doub > 2.3");
			
			execute("DELETE FROM table1");
			
		}
		catch (Exception e) {
			System.out.println("Failed.");
		}
	}
	
	protected static void test() {
		if(!haveSerialized) {
			Object[] queries = serializer.deserialize();
			haveSerialized = true;
			Long dbTime = null;
			if(queries[0] != null) {
				database = (Database) queries[0];
				haveDatabase = true;
				dbTime = Long.parseLong((String) queries[1]);
			}
			if(queries[2] != null) {
				for(int i = 2; i < queries.length; i += 2) {
					if(queries[i] != null) {
						Long time = Long.parseLong((String) queries[i]);
						if(time > dbTime) {
							System.out.println((String) queries[i + 1]);
							try {
								execute((String) queries[i + 1]);
							} catch (JSQLParserException e) {
								System.out.println("Execute didn't work.");
							}
						}
					}
				}
			}
		}
	}
	
	public static Table execute(String sql) throws JSQLParserException {
		if(!haveSerialized) {
			test();
		}
		Table result = null;
		SQLParser parser = new SQLParser();
		SQLQuery sq = parser.parse(sql);
		if(sq instanceof CreateTableQuery) {
			if(!haveDatabase) {
				database = new Database();
				haveDatabase = true;
			}
			result = createTable((CreateTableQuery) sq);
		}
		if(sq instanceof InsertQuery) {
			result = insertRow((InsertQuery) sq);
		}
		if(sq instanceof CreateIndexQuery) {
			result = createIndex((CreateIndexQuery) sq);
		}
		if(sq instanceof UpdateQuery) {
			result = update((UpdateQuery) sq);
		}
		if(sq instanceof DeleteQuery) {
			result = delete((DeleteQuery) sq);
		}
		if(sq instanceof SelectQuery) {
			result = select((SelectQuery) sq);
		}
		return result;
	}
	
	protected static void log(String q, Integer val) {
		if(queryCounter + val < 5) {
			queryCounter += val;
			serializer.updateLog(q);
		}
		else {
			queryCounter = 0;
			serializer.serialize(database);
		}
	}
		
	private static Table createTable(CreateTableQuery q) {
		if(database.getTables().containsKey(q.getTableName())) {
			System.out.println("A table with that name already exists.");
			ResultGenerator failed = new ResultGenerator();
			return failed.generate(false);
		}
		Table tbl = new Table(q.getTableName());
		String prime = q.getPrimaryKeyColumn().getColumnName();
		tbl.setPrimary(prime);
		ColumnDescription[] descriptions = q.getColumnDescriptions();
		for(int i = 0; i < descriptions.length; i++) {
			ColumnDescription column = descriptions[i];
			String name = column.getColumnName();
			tbl.getColumnTypes().put(name, column.getColumnType());
			if(column.isUnique() == false && (q.getPrimaryKeyColumn() != column)) {
				tbl.getUnique().put(name, null);
			}
			else {
				tbl.getUnique().put(name, new HashSet<Object>());
			}
			if(column.isNotNull() == true || q.getPrimaryKeyColumn() == column) {
				tbl.getNotNull().put(name, true);
			}
			else {
				tbl.getNotNull().put(name, false);
			}
			if(column.getHasDefault() == true) {
				tbl.getDefau().put(name, column.getDefaultValue());
			}
			else {
				tbl.getDefau().put(name, null);
			}
		}
		tbl.addBTree(prime);
		database.addTable(q.getTableName(), tbl);
		System.out.println(tbl);
		return tbl;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Table insertRow(InsertQuery q) {
		ResultGenerator result = new ResultGenerator();
		boolean worked = true;
		if(!(database.getTables().containsKey(q.getTableName()))) {
			worked = false;
			System.out.println("That table does not exist.");
		}
		else {
			Table tbl = database.getTable(q.getTableName());
			Row row = new Row();
			ColumnValuePair[] cvp = q.getColumnValuePairs();
			Set<String> columns = new HashSet<String>();
			for(String n : tbl.getColumnTypes().keySet()) {
				columns.add(n);
			}
			for(int i = 0; i < cvp.length; i++) {
				String name = cvp[i].getColumnID().getColumnName();
				String value = cvp[i].getValue();
				if(!(columns.contains(name))) {
					worked = false;
					return result.generate(worked);
				}
				columns.remove(name);
				boolean proceed = true;
				//see if column has to be not null
				if(tbl.getNotNull().get(name) == true) {
					if(value.equals("'null'") || value.equals("")) {
						proceed = false;
						System.out.println("You cannot add a null value to the column " + name + ".");
						worked = false;
						return result.generate(worked);
					}
				}
				//see if column has default value
				if(tbl.getDefau().get(name) != null) {
					if(value.equals("'null'") || value.equals("")) {
						value = tbl.getDefau().get(name).toString();
					}
				}
				//see if column is unique
				boolean unique = false;
				if(tbl.getUnique().get(name) != null) {
					unique = true;
				}
				
				Object obj = null;
				if(proceed == true) {
					switch(tbl.getColumnTypes().get(name)) {
					case INT:
						Integer num = Integer.parseInt(value);
						obj = num;
						if(unique) {
							if(tbl.getUnique().get(name).add(obj) == false) {
								proceed = false;
								System.out.println("The column " + name
										+ " is unique and the value entered is already in this column.");
								worked = false;
								return result.generate(worked);
							}
						}
						if(proceed) {
							row.addContent(name, obj);
						}
						break;
					case VARCHAR:
						obj = value;
						if(unique) {
							if(tbl.getUnique().get(name).add(obj) == false) {
								proceed = false;
								System.out.println("The column " + name 
										+ " is unique and the value entered is already in this column.");
								worked = false;
								return result.generate(worked);
							}
						}
						if(proceed) {
							row.addContent(name, obj);
						}
						break;
					case DECIMAL:
						Double dec = Double.parseDouble(value);
						obj = dec;
						if(unique) {
							if(tbl.getUnique().get(name).add(obj) == false) {
								proceed = false;
								System.out.println("The column " + name
										+ " is unique and the value entered is already in this column.");
								worked = false;
								return result.generate(worked);
							}
						}
						if(proceed) {
							row.addContent(name, obj);
						}
						break;
					case BOOLEAN: 
						Boolean bool = Boolean.valueOf((value.replaceAll("'", "")));
						obj = bool;
						if(unique) {
							if(tbl.getUnique().get(name).add(obj) == false) {
								proceed = false;
								System.out.println("The column " + name 
										+ " is unique and the value entered is already in the column.");
								worked = false;
								return result.generate(worked);
							}
						}
						if(proceed) {
							row.addContent(name, obj);
						}
						break;
					}
				}
				//check if column has index, if so add value to tree
				if(tbl.hasIndex(name)) {
					if(tbl.getTree(name) == null) {
						tbl.addBTree(name);
					}
					BTree tree = tbl.getTree(name);
					if(tree.get((Comparable) obj) == null) {
						LinkedList<Row> rows = new LinkedList<Row>();
						rows.add(row);
						tree.put((Comparable) obj, rows);
					}
					else {
						LinkedList<Row> temp = (LinkedList<Row>) tree.get((Comparable) obj);
						temp.add(row);
						tree.put((Comparable) obj, temp);
					}
					String str = String.valueOf(tree.height()) + " ";
					for(Object e : tree.getOrderedEntries()) {
						str += e.toString();
					}
					System.out.println(str);
				}
			}
			if(!columns.isEmpty()) {
				for(String col : columns) {
					if(tbl.getDefau().get(col) == null) {
						if(tbl.getNotNull().get(col) == null) {
							row.addContent(col, null);
						}
						else {
							System.out.println("Can't add null to a not null row.");
							worked = false;
							return result.generate(worked);
						}
					}
					else {
						row.addContent(col, tbl.getDefau().get(col));
					}
				}
			}
			tbl.getList().add(row);
			System.out.println(row);
		}
		if(worked) {
			log(q.toString(), 1);
		}
		return result.generate(worked);
	}
	
	private static Table createIndex(CreateIndexQuery q) {
		ResultGenerator result = new ResultGenerator();
		boolean worked = true;
		if(!(database.getTables().containsKey(q.getTableName()))) {
			worked = false;
		}
		else {
			Table tbl = database.getTable(q.getTableName());
			worked = tbl.addBTree(q.getColumnName());
			worked = tbl.fillBTree(q.getColumnName());
		}
		if(worked) {
			System.out.println("Index created.");
		}
		if(worked) {
			log(q.toString(), 1);
		}
		return result.generate(worked);
	}
	
	private static Table update(UpdateQuery q) {
		ResultGenerator result = new ResultGenerator();
		Boolean worked = true;
		Integer logNum = 0;
		if(!(database.getTables().containsKey(q.getTableName()))) {
			worked = false;
		}
		else {
			Table tbl = database.getTable(q.getTableName());
			if(q.getWhereCondition() == null) {
				Object[] answer = tbl.updateWithoutCond(q);
				worked = (Boolean) answer[0];
				logNum = (Integer) answer[1];
			}
			else {
				Object[] answer = tbl.updateRow(q);
				worked = (Boolean) answer[0];
				logNum = (Integer) answer[1];
			}
		}
		if(worked) {
			log(q.toString(), logNum);
		}
		return result.generate(worked);
	}
	
	private static Table delete(DeleteQuery q) {
		ResultGenerator result = new ResultGenerator();
		Integer logNum = 0;
		Boolean worked = true;
		if(!(database.getTables().containsKey(q.getTableName()))) {
			worked = false;
		}
		else {
			Table tbl = database.getTable(q.getTableName());
			if(q.getWhereCondition() == null) {
				logNum = tbl.getList().size();
				tbl.deleteAll(q);
				worked = true;
			}
			else {
				Object[] answer = tbl.deleteRow(q);
				worked = (Boolean) answer[0];
				logNum = (Integer) answer[1];
			}
		}
		if(worked) {
			log(q.toString(), logNum);
		}
		return result.generate(worked);
	}
		
	private static Table select(SelectQuery q) {
		Table result = new Table("Result");
		q.getFromTableNames();
		boolean worked = true;
		for(String t : q.getFromTableNames()) {
			if(!(database.getTables().containsKey(t))) {
				worked = false;
				ResultGenerator checker = new ResultGenerator();
				result = checker.generate(worked);
				System.out.println("The table " + t + " is not in this database.");
				return result;
			}
		}
		SelectHandler results = new SelectHandler();
		result = results.parse(database, q);
		return result;
	}
}