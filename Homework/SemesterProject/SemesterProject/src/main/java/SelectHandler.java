import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription.DataType;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.FunctionInstance;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.OrderBy;

public class SelectHandler {
	
	private Database database;
	
	protected SelectHandler() {
		database = null;
	}
	
	protected Table parse(Database d, SelectQuery q) {
		this.database = d;
		Table result = new Table("Result");
		Table[] tables = new Table[q.getFromTableNames().length];
		int i = 0;
		for(String str : q.getFromTableNames()) {
			tables[i] = database.getTable(str);
			i++;
		}
		ColumnID[] columns = q.getSelectedColumnNames();
		if(q.getWhereCondition() != null) {
			result = selectWithWhere(q, tables, columns);
		}
		else if(tables.length > 1) {
			result = makeCartesianProduct(tables);
			result = selectWithoutWhereOrFunc(q, result, columns);
		}
		else if(tables.length == 1) {
			result = selectWithoutWhereOrFunc(q, tables[0], columns);
		}
		//JD: it would make sense to break out from here to line 69 as a separate method - it's a bunch of lines that do one
		//coherent unit of work, and this method is long right now
		HashMap<String, Object> values = new HashMap<String, Object>();
		boolean hasFunc = false;
		for(ColumnID column : q.getFunctionMap().keySet()) {
			if(q.getFunctionMap().get(column) != null) {
				hasFunc = true;
				String colName = "";
				Table t = null;
				colName = tables[0].getName() + "." + column.getColumnName();
				t = result;
				switch(q.getFunctionMap().get(column).function) {
				case MAX:
					values.put(colName, getMax(q.getFunctionMap().get(column), t, colName));
					break;
				case MIN:
					values.put(colName, getMin(q.getFunctionMap().get(column), t, colName));
					break;
				case SUM:
					values.put(colName, getSum(q.getFunctionMap().get(column), t, colName));
					break;
				case AVG:
					values.put(colName, getAvg(q.getFunctionMap().get(column), t, colName));
					break;
				case COUNT:
					values.put(colName, getCount(q.getFunctionMap().get(column), t, colName));
					break;
				}
			}
		}
		if(hasFunc) {
			Table functionRes = new Table("Result");
			Row r = new Row();
			for(String str : values.keySet()) {
				r.addContent(str, values.get(str));
			}
			functionRes.getList().add(r);
			result = functionRes;
		}
		if(q.getOrderBys().length != 0) {
			result = setOrder(q.getOrderBys(), result.getList());
		}
		return result;
	}
	
	private Table selectWithWhere(SelectQuery q, Table[] tables, ColumnID[] columns) {
		Table result = new Table("Result");
		ConditionParser p = new ConditionParser();
		if(tables.length > 1) {
			result = makeCartesianProduct(tables);
			result = p.parse(result, q.getWhereCondition(), null);
			result = selectWithoutWhereOrFunc(q, result, columns);
		}
		else {
			result = p.parse(tables[0], q.getWhereCondition(), null);
			result = selectWithoutWhereOrFunc(q, result, columns);
		}
		return result;
	}
	//JD: same comment - break it down into smaller methods - better for comprehension and for maintenance 
	private Table selectWithoutWhereOrFunc(SelectQuery q, Table table, ColumnID[] columns) {
		Table result = new Table("Result");
		HashMap<String, ArrayList<Object>> rowInfo = new HashMap<String, ArrayList<Object>>();
		int rows = 0;
		Table tbl = table;
		//get the column names and types and store the info
		//check if selecting all and handle differently if so
		if(columns[0].getColumnName().equals("*")) {
			for(String col : tbl.getList().get(0).getContent().keySet()) {
				DataType type = tbl.getColumnTypes().get(col);
				result.addColType(col, type);
				ArrayList<Object> colInfo = new ArrayList<Object>();
				for(Row r : tbl.getList()) {
					colInfo.add(r.getContent().get(col));
					if(tbl.getList().size() > rows) {
						rows = tbl.getList().size();
					}
				}
				if(q.getFromTableNames().length == 1) {
					col = q.getFromTableNames()[0] + "." + col;
				}
				rowInfo.put(col, colInfo);
			}
		}
		else {
			for(ColumnID column : columns) {
				String colName = column.getColumnName();
				String col = "";
				if((tbl.getName().equals("Result") || tbl.getName().equals("leftIDResult")) && !(colName.contains(".")) && q.getFromTableNames().length != 1) {
					col = column.getTableName() + "." + colName;
				}
				else {
					col = colName;
				}
				if(!tbl.getColumnTypes().containsKey(col) && !tbl.getList().get(0).getContent().containsKey(col)) {
					System.out.println("This column doesn't exist.");
					ResultGenerator r = new ResultGenerator();
					return r.generate(false);
				}
				DataType type = tbl.getColumnTypes().get(col);
				result.addColType(col, type);
				ArrayList<Object> colInfo = new ArrayList<Object>();
				for(Row r : tbl.getList()) {
					colInfo.add(r.getContent().get(col));
					if(tbl.getList().size() > rows) {
						rows = tbl.getList().size();
					}
				}
				if(q.getFromTableNames().length == 1) {
					col = q.getFromTableNames()[0] + "." + colName;
				}
				rowInfo.put(col, colInfo);
			}
		}
		//make rows out of values we are looking for
		//if distinct first put into hashset
		HashSet<HashMap<String, Object>> distinctCheck = null;
		if(q.isDistinct()) {
			distinctCheck = new HashSet<HashMap<String, Object>>();
		}
		for(int i = 0; i < rows; i++) {
			Row resRow = new Row();
			for(String str : rowInfo.keySet()) {
				resRow.addContent(str, rowInfo.get(str).get(i));
			}
			if(q.isDistinct()) {
				if(distinctCheck.add(resRow.getContent())) {
					result.getList().add(resRow);
				}
				else {
					continue;
				}
			}
			else {
				result.getList().add(resRow);
			}
		}
		//testing
		System.out.println(result.getName());
		for(Row r : result.getList()) {
			System.out.println(r);
		}
		return result;
	}
	
	private Table makeCartesianProduct(Table[] tables) {
		Table result = new Table("Result");
		Table table1 = tables[1];
		Table table2 = tables[0];
		for(String colu : table1.getColumnTypes().keySet()) {
			String n = table1.getName() + "." + colu;
			result.addColType(n, table1.getColumnTypes().get(colu));
		}
		for(String col : table2.getColumnTypes().keySet()) {
			String na = table2.getName() + "." + col;
			result.addColType(na, table2.getColumnTypes().get(col));
		}
		for(Row r : table1.getList()) {
			for(Row r2 : table2.getList()) {
				Row resRow = new Row();
				HashMap<String, Object> resCon = new HashMap<String, Object>(r.getContent());
				for(String s : resCon.keySet()) {
					String colN = table1.getName() + "." + s;
					resRow.addContent(colN, resCon.get(s));
				}
				for(String str : r2.getContent().keySet()) {
					String colName = table2.getName() +  "." + str;
					resRow.addContent(colName, r2.getContent().get(str));
				}
				result.getList().add(resRow);
			}
		}
		System.out.println(result);
//		for(Row row : result.getList()) {
//			System.out.println(row);
//		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object getMax(FunctionInstance f, Table tbl, String column) {
		Object max = null;
		boolean found = false;
		for(Row r : tbl.getList()) {
			if(!found) {
				max = r.getContent().get(column);
				found = true;
			}
			else {
				if(((Comparable) r.getContent().get(column)).compareTo((Comparable) max) > 0) {
					max = r.getContent().get(column);
				}
			}
		}
		System.out.println(max);
		return max;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object getMin(FunctionInstance f, Table tbl, String column) {
		Object min = null;
		boolean found = false;
		for(Row r : tbl.getList()) {
			if(!found) {
				min = r.getContent().get(column);
				found = true;
			}
			else {
				if(((Comparable) r.getContent().get(column)).compareTo((Comparable) min) < 0) {
					min = r.getContent().get(column);
				}
			}
		}
		System.out.println(min);
		return min;
	}
	
	private Object getSum(FunctionInstance f, Table tbl, String column) {
		Integer sum1 = null;
		Double sum2 = null;
		boolean found = false;
		for(Row r : tbl.getList()) {
			if(!found) {
				if(r.getContent().get(column) == null) {
					System.out.println("That column doesn't exist.");
					return null;
				}
				if(r.getContent().get(column) instanceof Integer) {
					sum1 = (Integer) r.getContent().get(column);
				}
				else if(r.getContent().get(column) instanceof Double) {
					sum2 = (Double) r.getContent().get(column);
				}
				else {
					System.out.println("That type is not supported for this function.");
				}
				found = true;
			}
			else {
				if(sum1 != null) {
					sum1 += (Integer) r.getContent().get(column);
				}
				else {
					sum2 += (Double) r.getContent().get(column);
				}
			}
		}
		if(sum1 != null) {
			System.out.println(sum1);
			return sum1;
		}
		else {
			System.out.println(sum2);
			return sum2;
		}
	}
	
	private Object getAvg(FunctionInstance f, Table tbl, String column) {
		Integer sum1 = null;
		Double sum2 = null;
		Double count = 0.0;
		boolean found = false;
		for(Row r : tbl.getList()) {
			count++;
			if(!found) {
				if(r.getContent().get(column) instanceof Integer) {
					sum1 = (Integer) r.getContent().get(column);
				}
				else if(r.getContent().get(column) instanceof Double) {
					sum2 = (Double) r.getContent().get(column);
				}
				else {
					System.out.println("That type is not supported for this function.");
				}
				found = true;
			}
			else {
				if(sum1 != null) {
					sum1 += (Integer) r.getContent().get(column);
				}
				else {
					sum2 += (Double) r.getContent().get(column);
				}
			}
		}
		Double avg = 0.0;
		if(sum1 != null) {
			Double sum11 = sum1.doubleValue();
			avg = sum11 / count;
		}
		else {
			avg = sum2 / count;
		}
		System.out.println(avg);
		return avg;
	}
	
	private Object getCount(FunctionInstance f, Table tbl, String column) {
		Integer count = 0;
		if(!f.isDistinct) {
			count = tbl.getList().size();
		}
		else {
			HashSet<Object> checker = new HashSet<Object>();
			for(Row r : tbl.getList()) {
				if(checker.add(r.getContent().get(column))) {
					count++;
				}
			}
		}
		System.out.println(count);
		return count;
	}
	
	protected Table setOrder(OrderBy[] orders, LinkedList<Row> list) {
		Table result = new Table("Result");
		Comparer comp = new Comparer(orders);
		result.setList(mergeSort(list, comp));
		//testing
		for(Row r : result.getList()) {
			System.out.println(r);
		}
		return result;
	}
	
	class Comparer
	{
		private OrderBy[] orders;
		
		protected Comparer(OrderBy[] ord) {
			this.orders = ord;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		protected int compare(Row row1, Row row2, int num) {
			int result = -1;
			OrderBy order = this.orders[num];
			String colName = order.getColumnID().getColumnName();
			boolean ascending = order.isAscending();
			//JD: same comment - make ascending and descending separate methods
			if(ascending) {
				ArrayList<String> test1 = new ArrayList<String>(row1.getContent().keySet());
				ArrayList<String> test2 = new ArrayList<String>(row2.getContent().keySet());
				String beg1 = test1.get(0);
				String beg2 = test2.get(0);
				int i1 = beg1.indexOf(".");
				int i2 = beg2.indexOf(".");
				beg1 = beg1.substring(0, i1 + 1);
				beg2 = beg2.substring(0, i2 + 1);
				Object obj1 = row1.getContent().get(beg1 + colName);
				Object obj2 = row2.getContent().get(beg2 + colName);
				if(((Comparable) obj1).compareTo((Comparable) obj2) > 0) {
					result = 1;
				}
				else if(((Comparable) obj1).compareTo((Comparable) obj2) == 0) {
					if(orders[num + 1] != null) {
						result = compare(row1, row2, num + 1);
					}
					else {
						result = 0;
					}
				}
				else if(((Comparable) obj1).compareTo((Comparable) obj2) < 0) {
					result = -1;
				}
			}
			else {
				ArrayList<String> test1 = new ArrayList<String>(row1.getContent().keySet());
				ArrayList<String> test2 = new ArrayList<String>(row2.getContent().keySet());
				String beg1 = test1.get(0);
				String beg2 = test2.get(0);
				int i1 = beg1.indexOf(".");
				int i2 = beg2.indexOf(".");
				beg1 = beg1.substring(0, i1 + 1);
				beg2 = beg2.substring(0, i2 + 1);
				Object obj1 = row1.getContent().get(beg1 + colName);
				Object obj2 = row2.getContent().get(beg2 + colName);
				if(((Comparable) obj1).compareTo((Comparable) obj2) < 0) {
					result = 1;
				}
				else if(((Comparable) obj1).compareTo((Comparable) obj2) == 0) {
					if(orders[num + 1] != null) {
						result = compare(row1, row2, num + 1);
					}
					else {
						result = 0;
					}
				}
				else if(((Comparable) obj1).compareTo((Comparable) obj2) > 0) {
					result = -1;
				}
			}
			return result;
		}
	}
	
	private static LinkedList<Row> mergeSort(LinkedList<Row> data, Comparer comp) {
		if (data.size() < 2) {
			return data; //one or zero elements - already sorted!
		}
		// divide
		int mid = data.size() / 2;
		List<Row> firstHalf = data.subList(0, mid); // copy of first half
		LinkedList<Row> firstHalf2 = new LinkedList<Row>(firstHalf);
		List<Row> secondHalf = data.subList(mid, data.size()); //copy of second half
		LinkedList<Row> secondHalf2 = new LinkedList<Row>(secondHalf);
		// conquer (with recursion)
		mergeSort(firstHalf2, comp); // sort copy of first half
		mergeSort(secondHalf2, comp); // sort copy of second half
		// merge results
		return merge(firstHalf2, secondHalf2, data, comp); // merge sorted
		//halves back into original
	}
	
	private static LinkedList<Row> merge(LinkedList<Row> list1, LinkedList<Row> list2, LinkedList<Row> mergedList, Comparer comp) {
		int i = 0, j = 0;
		while (i + j < mergedList.size()) {
			//if we've already copied all of array2 OR (we're NOT done with array1
			//AND the current element of array1 < current element of array2)
			if (j == list2.size() || (i < list1.size() && comp.compare(list1.get(i), list2.get(j), 0) < 0)) {
				mergedList.set(i + j, list1.get(i++)); // copy ith element of array1
				//and increment i
			}
			//if we are not done with array2 and
			//(we're done with array1 OR current array2 element >= current
			//array1 element)
			else {
				mergedList.set(i + j, list2.get(j++));// copy jth element of array2 and
				//increment j
				}
		}
		return mergedList;
	}

}