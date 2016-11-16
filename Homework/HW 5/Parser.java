import java.util.ArrayList;
import java.util.UUID;

public class Parser {

    private String[] content;
    private TreeNode currentParent;
    private TreeNode current;
    private static TreeNode root;
    private int nodes;

    public Parser() {
        content = null;
        currentParent = null;
        current = null;
        root = null;
        nodes = 0;
    }

    public void parse(String str) {    
        //add dollar sign before "<" so that doesnt get cut off when splitting string
    	str = str.replaceAll("<", "\\$<");
    	content = str.split("\\$|>");
        this.build(content);
    }

    private void build(String[] array) {
        boolean hasRoot = false;

        for (String line : array) {
            if (!line.equals("")) {
                //have to set id unless we find one or ends a tag
                boolean hasId = false;
                //is this an end tag
                boolean end = false;
                //is this text
                boolean text = false;
                //if it ends a tag
                if (line.contains("</")) {
                    currentParent = currentParent.getParent();
                    //don't want to create id for end of a tag
                    hasId = true;
                    end = true;

                }
                //if it starts a new tag
                else if (line.startsWith("<")) {
                    nodes++;
                    //if there are attributes
                    if (line.contains(" ")) {
                        //separate tag from attributes
                        String[] removeTag = line.split("\\s+", 2);
                        //create node; tag starts after "<"
                        current = new TreeNode(currentParent, removeTag[0].substring(1));
                        //separate the attributes
                        String[] sepAttributes = removeTag[1].split("\"|\'");
                        int key = 0;
                        int value = 1;
                        //add attributes- every even will be a key and odd a value
                        for (int i = 0; i < sepAttributes.length; i += 2) {
                            if (sepAttributes[key].contains("id")) {
                                hasId = true;
                            }
                            current.addAttribute(sepAttributes[key].substring(0, sepAttributes[key].length() - 1), sepAttributes[value]);
                            key += 2;
                            value += 2;
                        }   
                    }
                    //there are no attributes
                    else {
                        current = new TreeNode(currentParent, line.substring(1));
                    }
                    if (!hasRoot) {
                        root = current;
                        hasRoot = true;
                    }
                }
                //it is text
                else {
                    //check to make sure it's not just whitespace
                    if (!line.matches("^.*\\S.*$")) {
                        end = true;
                    }
                    else {
                        text = true;
                        nodes++;
                        current = new TreeNode(currentParent, "Text");
                        current.addAttribute("Content", line);
                    }
                }
                //we made a new node and didn't find an id
                if (hasId == false) {
                    current.addAttribute("id", UUID.randomUUID().toString());
                }
                //if this isn't an end of a tag or a whitespace
                if (end == false) {
                    //if this isn't the root, add as child of parent
                    if (currentParent != null) {
                        currentParent.addChild(current);
                    }
                    if (text == false) {
                        currentParent = current;
                    }
                }          
            }
        }
    }

    public void breadthFirst() { 
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        temp.add(root);
        int loc = 0;
        while (!temp.isEmpty() && loc != nodes) {
            TreeNode node = (TreeNode) temp.get(loc);
            System.out.println("Visited node: " + node.getTag() + node.getAttributes());
            for (TreeNode c : node.getChildren()) {
                temp.add(c);
            }
            loc++;
        }
    }

    public void depthFirst() {
        depthFirst(root);
    }

    private void depthFirst(TreeNode n) {
        System.out.println("Visited node: " + n.getTag() + n.getAttributes());
        for(TreeNode c : n.getChildren()) {
            depthFirst(c);
        }
    }

}