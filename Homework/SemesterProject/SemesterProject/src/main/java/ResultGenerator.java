import java.util.HashMap;

public class ResultGenerator {

	protected ResultGenerator() {
		
	}
	
	protected Table generate(Boolean bool) {
		Table result = new Table("Result");
		Row r = new Row();
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("Result", bool);
		r.setContent(resultMap);
		result.getList().add(r);
		return result;
	}
}
