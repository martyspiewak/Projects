import java.util.HashMap;

@SuppressWarnings("serial")
public class Row implements java.io.Serializable{
	
	private HashMap<String, Object> content;
	
	protected Row() {
		content = new HashMap<String, Object>();
	}

	protected HashMap<String, Object> getContent() {
		return content;
	}

	protected void setContent(HashMap<String, Object> c) {
		this.content = c;
	}
	
	protected void addContent(String str, Object obj) {
		content.put(str, obj);
	}
	
	public String toString() {
		String str = "";
		for(String k : content.keySet()) {
			str += k + ": ";
			if(content.get(k) != null) {
				str += content.get(k).toString() + ", ";
			}
			else {
				str += "null, ";
			}
		}
		return str;
	}

}
