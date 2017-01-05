import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription.DataType;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;

public class ConditionParser {
	
	protected ConditionParser() {
		
	}
	
	@SuppressWarnings("incomplete-switch")
	protected Table parse(Table tbl, Condition c, Table holder) {
		Table result = new Table("Result");
		switch(c.getOperator()) {
		case AND:
			LinkedList<Row> resList = new LinkedList<Row>();
			Table leftRes = parse(tbl, (Condition) c.getLeftOperand(), result);
			Table rightRes = parse(tbl, (Condition) c.getRightOperand(), result);
			for(Row row : leftRes.getList()) {
				for(Row row2 : rightRes.getList()) {
					if(row.getContent().equals(row2.getContent())) {
						resList.add(row);
					}
				}
			}
			result.setList(resList);
			break;
		case OR:
			LinkedList<Row> resList2 = new LinkedList<Row>();
			Table leftRes2 = parse(tbl, (Condition) c.getLeftOperand(), result);
			Table rightRes2 = parse(tbl, (Condition) c.getRightOperand(), result);
			resList2.addAll(leftRes2.getList());
			resList2.addAll(rightRes2.getList());
			result.setList(resList2);
			break;
		}
		if(!(c.getLeftOperand() instanceof Condition) && !(c.getRightOperand() instanceof Condition)) {
			if(c.getLeftOperand() instanceof ColumnID && c.getRightOperand() instanceof ColumnID) {
				result = testColumnIDs(tbl, c);
			}
			else if(c.getLeftOperand() instanceof ColumnID) {
				result = testLeftID(tbl, c);
			}
		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "incomplete-switch" })
	private Table testColumnIDs(Table tbl, Condition c) {
		Table IDsResult = new Table("IDsResult");
		ColumnID leftCol = (ColumnID) c.getLeftOperand();
		ColumnID rightCol = (ColumnID) c.getRightOperand();
		String left = leftCol.getColumnName();
		String right = rightCol.getColumnName();
		Iterator<Row> iterator = tbl.getList().iterator();
		while(iterator.hasNext()) {
			Row r = new Row();
			r.setContent(iterator.next().getContent());
			Object leftOb = r.getContent().get(left);
			Object rightOb = r.getContent().get(right);
			switch(c.getOperator()) {
			case EQUALS:
				if(((Comparable) leftOb).compareTo(rightOb) == 0) {
					IDsResult.getList().add(r);
				}
				break;
			case NOT_EQUALS:
				if(!leftOb.equals(rightOb)) {
					IDsResult.getList().add(r);
				}
				break;
			case LESS_THAN:
				if(((Comparable) leftOb).compareTo((Comparable) rightOb) < 0) {
					IDsResult.getList().add(r);
				}
				break;
			case lESS_THAN_OR_EQUALS:
				if(((Comparable) leftOb).compareTo((Comparable) rightOb) <= 0) {
					IDsResult.getList().add(r);
				}
				break;
			case GREATER_THAN:
				if(((Comparable) leftOb).compareTo((Comparable) rightOb) > 0) {
					IDsResult.getList().add(r);
				}
				break;
			case GREATER_THAN_OR_EQUALS:
				if(((Comparable) leftOb).compareTo((Comparable) rightOb) >= 0) {
					IDsResult.getList().add(r);
				}
				break;
			}
		}
		if(IDsResult.getList().isEmpty()) {
			System.out.println("There were no rows matching those conditions.");
			return null;
		}
		else {
			return IDsResult;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "incomplete-switch" })
	private Table testLeftID(Table tbl, Condition c) {
		Table leftIDResult = new Table("leftIDResult");
		ColumnID leftCol = (ColumnID) c.getLeftOperand();
		String rightOp = (String) c.getRightOperand();
		String leftColName = leftCol.getColumnName();
		if(tbl.getColumnTypes().get(leftColName) == null) {
			leftColName = leftCol.getTableName() + "." + leftCol.getColumnName();
		}
		DataType type = tbl.getColumnTypes().get(leftColName);
		Object right = null;
		switch(type) {
		case INT:
			Integer num = Integer.parseInt(rightOp);
			right = num;
			break;
		case BOOLEAN:
			Boolean boo = Boolean.valueOf(rightOp.replaceAll("'", ""));
			right = boo;
			break;
		case VARCHAR:
			right = rightOp;
			break;
		case DECIMAL:
			Double doub = Double.parseDouble(rightOp);
			right = doub;
			break;
		}
		if(tbl.getTree(leftColName) != null) {
			BTree tree = tbl.getTree(leftColName);
			leftIDResult = oneIDWithTree(c, tree, right);
			System.out.println("Used tree.");
		}
		else {
			Iterator<Row> iterator = tbl.getList().iterator();
			while(iterator.hasNext()) {
				Row r = new Row();
				r.setContent(iterator.next().getContent());
				if(r.getContent().get(leftColName) == null) {
					leftColName = leftCol.getTableName() + "." + leftCol.getColumnName();
				}
				Object leftOb = r.getContent().get(leftColName);
				if(right instanceof String) {
					((String) leftOb).replaceAll("'", "\"");
				}
				switch(c.getOperator()) {
				case EQUALS:
					if(((Comparable) leftOb).compareTo(right) == 0) {
						leftIDResult.getList().add(r);
					}
					break;
				case NOT_EQUALS:
					if(!leftOb.equals(right)) {
						leftIDResult.getList().add(r);
					}
					break;
				case LESS_THAN:
					if(((Comparable) leftOb).compareTo(right) < 0) {
						leftIDResult.getList().add(r);
					}
					break;
				case lESS_THAN_OR_EQUALS:
					if(((Comparable) leftOb).compareTo(right) <= 0) {
						leftIDResult.getList().add(r);
					}
					break;
				case GREATER_THAN:
					if(((Comparable) leftOb).compareTo(right) > 0) {
						leftIDResult.getList().add(r);
					}
					break;
				case GREATER_THAN_OR_EQUALS:
					if(((Comparable) leftOb).compareTo(right) >= 0) {
						leftIDResult.getList().add(r);
					}
					break;
				}
			}
		}
		return leftIDResult;
	}
		
	@SuppressWarnings({ "unchecked", "rawtypes", "incomplete-switch" })
	private Table oneIDWithTree(Condition c, BTree tree, Object value) {
		Table result = new Table("Result");
		ArrayList<BTree.Entry> orders = tree.getOrderedEntries();
		int index = -1;
		boolean found = false;
		if(tree.get((Comparable) value) != null) {
			for(BTree.Entry e : orders) {
				if(e.getKey().equals(value)) {
				index = orders.indexOf(e);
				found = true;
				}
			}
		}
		switch(c.getOperator()) {
		case EQUALS:
			if(!found) {
				System.out.println("That value does not exist.");
				result = null;
				return result;
			}
			for(Row r : (LinkedList<Row>) orders.get(index).getValue()) {
				result.getList().add(r);
			}
			break;
		case NOT_EQUALS:
			for(int i = 0; i < orders.size(); i++) {
				if(i == index) {
					continue;
				}
				for(Row r : (LinkedList<Row>) orders.get(i).getValue()) {
					result.getList().add(r);
				}
			}
			break;
		case LESS_THAN:
			if(!found) {
				//start at end of orders
				for(int i = orders.size() - 1; i > 0; i--) {
					if(orders.get(i).getKey().compareTo((Comparable) value) < 0) {
						index = i;
						found = true;
						break;
					}
					else if ((orders.get(i - 1).getKey().compareTo((Comparable) value) > 0)) {
						continue;
					}
				}
				if(!found) {
					if (orders.get(0).getKey().compareTo((Comparable) value) < 0) {
						index = 0;
					}
					else {
						System.out.println("There are no values less than " + value + ".");
						return null;
					}
				}
			}
			else {
				if(index != 0) {
					index--;
				}
				else {
					System.out.println("There are no values less than " + value + ".");
					return null;
				}
			}
			for(int i = 0; i <= index; i++) {
				for(Row r : (LinkedList<Row>) orders.get(i).getValue()) {
					result.getList().add(r);
				}
			}
			break;
		case lESS_THAN_OR_EQUALS:
			if(!found) {
				//start at end of orders
				for(int i = orders.size() - 1; i > 0; i--) {
					if(orders.get(i).getKey().compareTo((Comparable) value) < 0) {
						index = i;
						found = true;
						break;
					}
					else if ((orders.get(i - 1).getKey().compareTo((Comparable) value) > 0)) {
						continue;
					}
				}
				if(!found) {
					if (orders.get(0).getKey().compareTo((Comparable) value) < 0) {
						index = 0;
					}
					else {
						System.out.println("There are no values less than " + value + ".");
						return null;
					}
				}
			}
			else {
				if(index == 0) {
					System.out.println("There are no values less than " + value + ".");
					return null;
				}
			}
			for(int i = 0; i <= index; i++) {
				for(Row r : (LinkedList<Row>) orders.get(i).getValue()) {
					result.getList().add(r);
				}
			}
			break;
		case GREATER_THAN:
			if(!found) {
				//start at end of orders
				for(int i = 0; i < orders.size() - 1; i++) {
					if(orders.get(i).getKey().compareTo((Comparable) value) > 0) {
						index = i;
						found = true;
						break;
					}
					else if ((orders.get(i + 1).getKey().compareTo((Comparable) value) < 0)) {
						continue;
					}
				}
				if(!found) {
					if (orders.get(orders.size() - 1).getKey().compareTo((Comparable) value) > 0) {
						index = orders.size() - 1;
					}
					else {
						System.out.println("There are no values greater than " + value + ".");
						return null;
					}
				}
			}
			else {
				if(index != orders.size() - 1) {
					index++;
				}
				else {
					System.out.println("There are no values greater than " + value + ".");
					return null;
				}
			}
			for(int i = index; i < orders.size(); i++) {
				for(Row r : (LinkedList<Row>) orders.get(i).getValue()) {
					result.getList().add(r);
				}
			}
			break;
		case GREATER_THAN_OR_EQUALS:
			if(!found) {
				//start at beginning of orders
				for(int i = 0; i < orders.size() - 1; i++) {
					if(orders.get(i).getKey().compareTo((Comparable) value) > 0) {
						index = i;
						found = true;
						break;
					}
					else if ((orders.get(i + 1).getKey().compareTo((Comparable) value) < 0)) {
						continue;
					}
				}
				if(!found) {
					if (orders.get(orders.size() - 1).getKey().compareTo((Comparable) value) > 0) {
						index = orders.size() - 1;
					}
					else {
						System.out.println("There are no values greater than " + value + ".");
						return null;
					}
				}
			}
			else {
				if(index == orders.size() - 1) {
					System.out.println("There are no values greater than " + value + ".");
					return null;
				}
			}
			for(int i = index; i < orders.size(); i++) {
				for(Row r : (LinkedList<Row>) orders.get(i).getValue()) {
					result.getList().add(r);
				}
			}
			break;
		}
		System.out.println("Used tree");
		return result;
	}
	
}
