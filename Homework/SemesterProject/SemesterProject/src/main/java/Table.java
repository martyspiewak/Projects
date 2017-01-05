import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription.DataType;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;

@SuppressWarnings("serial")
public class Table implements java.io.Serializable{

	private String name;
	private LinkedList<Row> list;
	private HashMap<String, DataType> columnTypes;
	private HashMap<String, HashSet<Object>> unique;
	private HashMap<String, Boolean> notNull;
	private HashMap<String, Object> defau;
	private String primary;
	@SuppressWarnings("rawtypes")
	private HashMap<String, BTree> trees;
	
	@SuppressWarnings("rawtypes")
	protected Table(String name) {
		this.setName(name);
		this.list = new LinkedList<Row>();
		this.columnTypes = new HashMap<String, DataType>();
		this.unique = new HashMap<String, HashSet<Object>>();
		this.notNull = new HashMap<String, Boolean>();
		this.defau = new HashMap<String, Object>();
		this.primary = "";
		this.trees = new HashMap<String, BTree>();
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected LinkedList<Row> getList() {
		return list;
	}

	protected void setList(LinkedList<Row> list) {
		this.list = list;
	}

	protected HashMap<String, DataType> getColumnTypes() {
		return columnTypes;
	}

	protected void setColumnTypes(HashMap<String, DataType> columnTypes) {
		this.columnTypes = columnTypes;
	}
	
	protected void addColType(String s, DataType d) {
		this.columnTypes.put(s, d);
	}

	protected HashMap<String, HashSet<Object>> getUnique() {
		return unique;
	}

	protected void setUnique(HashMap<String, HashSet<Object>> unique) {
		this.unique = unique;
	}

	protected HashMap<String, Boolean> getNotNull() {
		return notNull;
	}

	protected void setNotNull(HashMap<String, Boolean> notNull) {
		this.notNull = notNull;
	}

	protected HashMap<String, Object> getDefau() {
		return defau;
	}

	protected void setDefau(HashMap<String, Object> defau) {
		this.defau = defau;
	}

	protected String getPrimary() {
		return primary;
	}

	protected void setPrimary(String primary) {
		this.primary = primary;
	}
	
	protected boolean addBTree(String columnName) {
		boolean result = true;
		if(!(this.columnTypes.containsKey(columnName))) {
			result = false;
			return result;
		}
		@SuppressWarnings("rawtypes")
		BTree tree = null;
		switch(columnTypes.get(columnName)) {
		case INT:
			tree = new BTree<Integer, LinkedList<Row>>();
			break;
		case VARCHAR:
			tree = new BTree<String, LinkedList<Row>>();
			break;
		case DECIMAL:
			tree = new BTree<Double, LinkedList<Row>>();
			break;
		case BOOLEAN:
			tree = new BTree<Boolean, LinkedList<Row>>();
			break;
		}
		trees.put(columnName, tree);
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected boolean fillBTree(String column) {
		boolean result = true;
		if(!(this.columnTypes.containsKey(column))) {
			result = false;
			return result;
		}
		BTree tree = this.trees.get(column);
		for(Row row : list) {
			Object value = row.getContent().get(column);
			if(tree.get((Comparable) value) == null) {
				LinkedList<Row> list = new LinkedList<Row>();
				list.add(row);
				tree.put((Comparable) value, list);
			}
			else {
				LinkedList<Row> list = (LinkedList<Row>) tree.get((Comparable) value);
				list.add(row);
			}
		}
		this.trees.put(column, tree);
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	protected BTree getTree(String column) {
		if(this.trees.containsKey(column)) {
			return this.trees.get(column);
		}
		else {
			return null;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addToTree(String columnName, Object value, Row row) {
		BTree tree = trees.get(columnName);
		if(tree.get((Comparable) value) == null) {
			LinkedList<Row> list = new LinkedList<Row>();
			list.add(row);
			tree.put((Comparable) value, list);
		}
		else {
			LinkedList<Row> list = (LinkedList<Row>) trees.get(columnName).get((Comparable) value);
			list.add(row);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void removeFromTree(String columnName, Object value, Row row) {
		BTree tree = trees.get(columnName);
		if(tree.get((Comparable) value) == null) {
			return;
		}
		else {
			LinkedList<Row> list = (LinkedList<Row>) tree.get((Comparable) value);
			list.remove(row);
		}
	}
	
	protected boolean hasIndex(String column) {
		boolean result = false;
		if(trees.containsKey(column)) {
			result = true;
		}
		return result;
	}
	
	protected Object checkAdd(String column, String val) {
		boolean result = true;
		//check that it is the right type
		//first convert it to the proper type
		Object obj = null;
		switch(this.columnTypes.get(column)) {
		case INT:
			try{
				Integer num = Integer.parseInt(val);
				obj = num;
			} catch (Exception e) {
				System.err.println("This is not a valid input.");
				return null;
			}
			break;
		case VARCHAR:
			try {
				obj = val;
			} catch (Exception e) {
				System.err.println("This is not a valid input.");
				return null;
			}
			break;
		case BOOLEAN:
			try {
				Boolean boo = Boolean.valueOf(val.replaceAll("'", ""));
				obj = boo;
			} catch (Exception e) {
				System.err.println("This is not a valid input.");
				return null;
			}
			break;
		case DECIMAL:
			try {
				Double doub = Double.parseDouble(val);
				obj = doub;
			} catch (Exception e) {
				System.err.println("This is not a valid input.");
				return null;
			}
			break;
		}
		//check if column is unique and if so, try adding
		if(this.unique.get(column) != null) {
			if(!this.unique.get(column).add(obj)) {
				result = false;
				return null;
			}
		}
		//check if column is not null and value is null
		if(this.notNull.get(column) == true) {
			if(obj == null) {
				result = false;
				return null;
			}
		}	
		if(result == true) {
			return obj;
		}
		else {
			return null;
		}
	}
	
	protected Object[] updateWithoutCond(UpdateQuery q) {
		Object[] answer = new Object[2];
		Boolean result = true;
		Integer logNum = 0;
		ColumnValuePair[] cvp = q.getColumnValuePairs();
		for(int i = 0; i < cvp.length; i++) {
			String name = cvp[i].getColumnID().getColumnName();
			String value = cvp[i].getValue();
			if(!(this.columnTypes.containsKey(name))) {
				result = false;
			}
			if(this.unique.get(name) != null) {
				System.err.println("You cannot add a value to all rows in a column that is unique.");
				continue;
			}
			Object obj = checkAdd(name, value);
			if(obj == null) {
				System.err.println("This is not a valid input for this column.");
			}
			boolean tree = false;
			if(trees.containsKey(name)) {
				tree = true;
				trees.put(name, null);
			}
			else {
				for(Row row : list) {
					row.getContent().put(name,obj);
					System.out.println(row);
					logNum++;
				}
			}
			if(tree) {
				this.fillBTree(name);
			}
			//for testing
			if(tree) {
				String str = String.valueOf(trees.get(name).height()) + " ";
				for(Object e : trees.get(name).getOrderedEntries()) {
					str += e.toString();
				}
				System.out.println(str);
			}
		}
		System.out.println(this);
		answer[0] = result;
		answer[1] = logNum;
		return answer;
	}
	
	protected Object[] updateRow(UpdateQuery q) {
		Object[] answer = new Object[2];
		Integer logNum = 0;
		Boolean results = true;
		ConditionParser p = new ConditionParser();
		Table result = p.parse(this, q.getWhereCondition(), null);
		if(result == null) {
			results = false;
			answer[0] = results;
			answer[1] = logNum;
			return answer;
		}
		ColumnValuePair[] cvp = q.getColumnValuePairs();
		for(int i = 0; i < cvp.length; i++) {
			String name = cvp[i].getColumnID().getColumnName();
			String value = cvp[i].getValue();
			Object obj = checkAdd(name, value);
			boolean hasTree = false;
			if(trees.containsKey(name)) {
				hasTree = true;
			}
			for(Row row : this.list) {
				for(Row row2 : result.getList()) {
					if(row.getContent().equals(row2.getContent())) {
						if(hasTree) {
							removeFromTree(name, row.getContent().get(name), row);
							addToTree(name, obj, row);
						}
						row.getContent().put(name, obj);
						System.out.println(row);
						logNum++;
					}
				}
			}
		}
		answer[0] = results;
		answer[1] = logNum;
		return answer;
	}
	
	protected void deleteAll(DeleteQuery q) {
		this.list.clear();
		System.out.println("Removed all rows.");
		trees.clear();
		trees.put(primary, null);
		for(String col : this.unique.keySet()) {
			if(unique.get(col) != null) {
				unique.get(col).clear();
			}
		}
	}
	
	protected Object[] deleteRow(DeleteQuery q) {
		Object[] toReturn = new Object[2];
		Boolean results = true;
		int logNum = 0;
		ConditionParser p = new ConditionParser();
		Table result = p.parse(this, q.getWhereCondition(), null);
		if(result == null) {
			results = false;
			toReturn[0] = results;
			toReturn[1] = 0;
			return toReturn;
		}
		for(String col : result.getColumnTypes().keySet()) {
			if(!(this.list.contains(col))) {
				results = false;
			}
		}
		LinkedList<Row> toRemove = new LinkedList<Row>();
		outer:	
		for(Row row : this.list) {
			for(Row row2 : result.getList()) {
				if(row.getContent().equals(row2.getContent())) {	
					for(String column : trees.keySet()) {
						removeFromTree(column, row.getContent().get(column), row);
						if(this.unique.get(column) != null) {
							this.unique.get(column).remove(row.getContent().get(column));
						}
					}
					toRemove.add(row);
					System.out.println("Removed row: " + row);
					continue outer;
				}
			}
		}
		for(Row rows : toRemove) {
			this.list.remove(rows);
			logNum++;
		}
		toReturn[0] = results;
		toReturn[1] = logNum;
		return toReturn;
	}
	
	public String toString() {
		String str = "TableName: " + this.name + " ";
		for(String k : columnTypes.keySet()) {
			str += k + ": ";
			str += columnTypes.get(k).toString() + ", ";
		}
		str += "Trees: ";
		for(String name : trees.keySet()) {
			str += name;
		}
		return str;
	}
	
}