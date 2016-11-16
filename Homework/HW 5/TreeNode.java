import java.util.ArrayList;
import java.util.HashMap;

public class TreeNode {
	
	private String tag;
	private HashMap<String, String> attributes;
	private TreeNode parent;
	private ArrayList<TreeNode> children;
	
	public TreeNode(TreeNode parent, String tag) {
		this.parent = parent;
		this.tag = tag;
		children = new ArrayList<TreeNode>();
		attributes = new HashMap<String, String>();
	}
	
	public TreeNode getParent() {
		return this.parent;	
	}
	
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	public boolean addChild(TreeNode child) {
		return this.children.add(child);
	}
	
	public boolean removeChild(TreeNode child) {
		return this.children.remove(child);
	}
	
	public boolean hasChildren() {
		if (!this.children.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public ArrayList<TreeNode> getChildren() {	
		return this.children;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public HashMap<String, String> getAttributes() {
		return this.attributes;
	}
	
	public void addAttribute(String attribute, String value) {
		this.attributes.put(attribute, value);
	}
}