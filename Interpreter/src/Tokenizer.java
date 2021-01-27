import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/**
 * Created by Marty on 10/29/2017.
 */
public class Tokenizer {

    private Stack<Character> ungetStack;
    private BufferedReader reader;
    private HashMap<String, Token.TokenType> keywords;
    private HashSet<Character> hexSet;
    private HashSet<Character> suffixes;

    public Tokenizer(String fileName) {
        ungetStack = new Stack<>();
        try {
            reader = new BufferedReader(new FileReader(new File(fileName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        keywords = new HashMap<>();
        keywords.put("auto", Token.TokenType.AUTO);
        keywords.put("double", Token.TokenType.DOUBLE);
        keywords.put("int", Token.TokenType.INT);
        keywords.put("struct", Token.TokenType.STRUCT);
        keywords.put("const", Token.TokenType.CONST);
        keywords.put("float", Token.TokenType.FLOAT);
        keywords.put("short", Token.TokenType.SHORT);
        keywords.put("unsigned", Token.TokenType.UNSIGNED);
        keywords.put("break", Token.TokenType.BREAK);
        keywords.put("else", Token.TokenType.ELSE);
        keywords.put("long", Token.TokenType.LONG);
        keywords.put("switch", Token.TokenType.SWITCH);
        keywords.put("continue", Token.TokenType.CONTINUE);
        keywords.put("for", Token.TokenType.FOR);
        keywords.put("signed", Token.TokenType.SIGNED);
        keywords.put("void", Token.TokenType.VOID);
        keywords.put("case", Token.TokenType.CASE);
        keywords.put("enum", Token.TokenType.ENUM);
        keywords.put("register", Token.TokenType.REGISTER);
        keywords.put("typedef", Token.TokenType.TYPEDEF);
        keywords.put("default", Token.TokenType.DEFAULT);
        keywords.put("goto", Token.TokenType.GOTO);
        keywords.put("sizeof", Token.TokenType.SIZEOF);
        keywords.put("volatile", Token.TokenType.VOLATILE);
        keywords.put("char", Token.TokenType.CHAR);
        keywords.put("extern", Token.TokenType.EXTERN);
        keywords.put("return", Token.TokenType.RETURN);
        keywords.put("union", Token.TokenType.UNION);
        keywords.put("do", Token.TokenType.DO);
        keywords.put("if", Token.TokenType.IF);
        keywords.put("static", Token.TokenType.STATIC);
        keywords.put("while", Token.TokenType.WHILE);
        keywords.put("alignof", Token.TokenType.ALIGNOF);

        hexSet = new HashSet<>();
        hexSet.add('a');
        hexSet.add('b');
        hexSet.add('c');
        hexSet.add('d');
        hexSet.add('e');
        hexSet.add('f');
        hexSet.add('A');
        hexSet.add('B');
        hexSet.add('C');
        hexSet.add('D');
        hexSet.add('E');
        hexSet.add('F');

        suffixes = new HashSet<>();
        suffixes.add('u');
        suffixes.add('U');
        suffixes.add('l');
        suffixes.add('L');
        suffixes.add('f');
        suffixes.add('F');
    }

    public Token nextToken() {
        char c;
        //index of token
        int i, state;
        char tk[] = new char[10000];
        char nextTest;

        //skip whitespace
        while (Character.isWhitespace(c = getNextChar()) || c == '\r') ;

        //if at end of input stream
        if (c == (char) (-1)) {
            return new Token(Token.TokenType.END, "EOF".toCharArray(), 3);
        }

        i = 0;
        tk[i++] = c;

        //if an integer or float
        if (Character.isDigit(c) || c == '.') {
            if(c == 0) {
                char second = getNextChar();
                switch(second) {
                    case 'x' :
                    case 'X' :
                        state = 9;
                        break;
                    default:
                        state = 11;
                }
            }
            if (c == '.') {
                char second = getNextChar();
                char third = getNextChar();
                if(second == '.') {
                    if(third == '.') {
                        tk[1] = second;
                        tk[2] = third;
                        return new Token(Token.TokenType.VARIADIC, tk ,i + 2);
                    }
                    ungetChar(third);
                    tk[1] = second;
                    return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                }
                ungetChar(third);
                ungetChar(second);
                state = 4;
                c = getNextChar();
                if (!Character.isDigit(c)) {
                    ungetChar(c);
                    return new Token(Token.TokenType.PERIOD, ".".toCharArray(), 1);
                }
                ungetChar(c);
            } else state = 2;
            c = getNextChar();
            while (state > 0) {
                switch (state) {
                    case 2:
                        if (Character.isDigit(c)) state = 2;
                        else if (c == 'e' || c == 'E') state = 5;
                        else if (c == '.') state = 3;
                        else if(suffixes.contains(c)) {
                            if(c == 'L') {
                                state = 12;
                            }
                            else if(c == 'l') {
                                state = 13;
                            }
                            else if(c == 'u' || c == 'U') {
                                state = 14;
                            }
                        }

                        else if(!Character.isLetter(c)){
                            state = -2;
                        }
                        else {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }
                        break;
                    case 3:
                        if (Character.isDigit(c)) state = 8;
                        else if(suffixes.contains(c)) {
                            if(c == 'L') {
                                state = 12;
                            }
                            else if(c == 'l') {
                                state = 13;
                            }
                            else if(c == 'u' || c == 'U') {
                                state = 14;
                            }
                        }
                        else if(!Character.isLetter(c)){
                            state = -3;
                        }
                        else {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }
                        break;
                    case 4:
                        if (Character.isDigit(c)) state = 3;
                        else if(suffixes.contains(c)) {
                            if(c == 'L') {
                                state = 12;
                            }
                            else if(c == 'l') {
                                state = 13;
                            }
                            else if(c == 'u' || c == 'U') {
                                state = 14;
                            }
                        }
                        else state = -4;
                        break;
                    case 5:
                        if (Character.isDigit(c)) state = 6;
                        else if (c == '-' || c == '+') state = 7;
                        else if(suffixes.contains(c)) {
                            if(c == 'L') {
                                state = 12;
                            }
                            else if(c == 'l') {
                                state = 13;
                            }
                            else if(c == 'u' || c == 'U') {
                                state = 14;
                            }
                        }
                        else state = -5;
                        break;
                    case 6:
                        if (Character.isDigit(c)) state = 6;
                        else if(suffixes.contains(c)) {
                            if(c == 'L') {
                                state = 12;
                            }
                            else if(c == 'l') {
                                state = 13;
                            }
                            else if(c == 'u' || c == 'U') {
                                state = 14;
                            }
                        }
                        else state = -6;
                        break;

                    case 7:
                        if (Character.isDigit(c)) state = 6;
                        else if(suffixes.contains(c)) {
                            if(c == 'L') {
                                state = 12;
                            }
                            else if(c == 'l') {
                                state = 13;
                            }
                            else if(c == 'u' || c == 'U') {
                                state = 14;
                            }
                        }
                        else state = -7;
                        break;
                    case 8:
                        if (Character.isDigit(c)) state = 8;
                        else if (c == 'e' || c == 'E') state = 5;
                        else if(suffixes.contains(c)) {
                            if(c == 'L') {
                                state = 12;
                            }
                            else if(c == 'l') {
                                state = 13;
                            }
                            else if(c == 'u' || c == 'U') {
                                state = 14;
                            }
                        }
                        else if(!Character.isLetter(c)){
                            state = -2;
                        }
                        else {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }

                        break;

                    case 9 :
                        if(Character.isDigit(c) || hexSet.contains(c)) {
                            state = 9;
                        }
                        else if(suffixes.contains(c)) {
                            if(c == 'L') {
                                state = 12;
                            }
                            else if(c == 'l') {
                                state = 13;
                            }
                            else if(c == 'u' || c == 'U') {
                                state = 14;
                            }
                        }
                        break;

                    case 10:
                        if(!suffixes.contains(c)) {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }

                        break;

                    case 11:
                        int x = c - '0';
                        if(x <= 7) {
                            state = 11;
                        }
                        else {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }
                        break;
                    //seen one L
                    case 12:
                        if(!suffixes.contains(c) || c == 'l') {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }
                        if(c == 'u' || c == 'U') {
                            state = 15;
                            break;
                        }
                        else {
                            state = 16;
                            break;
                        }
                        //seen one l
                    case 13:
                        if(!suffixes.contains(c) || c == 'L') {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }
                        if(c == 'u' || c == 'U') {
                            //seen a u after l
                            state = 15;
                            break;
                        }
                        else {
                            state = 16;
                            break;
                        }
                        //seen a u or U before L
                    case 14:
                        if(c == 'l') {
                            state = 17;
                        }
                        else if(c == 'L') {
                            state = 18;
                        }
                        else {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }
                        break;
                    case 15:
                        if(!Character.isLetter(c)){
                            state = -2;
                        }
                        else {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }
                        break;
                    //seen two L's or two l's
                    case 16:
                        if(c == 'u' || c == 'U') {
                            state = 15;
                        }
                        else {
                            state = -15;
                        }
                        break;
                    //seen u/U then l
                    case 17:
                        if(c == 'l') {
                            state = 15;
                        }
                        else {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }
                        break;
                    //seen u/u then L
                    case 18:
                        if(c == 'L') {
                            state = 15;
                        }
                        else {
                            tk[i] = c;
                            return new Token(Token.TokenType.BAD_TOKEN, tk, i + 1);
                        }
                        break;


                }
                if (state > 0) {
                    tk[i++] = c;
                    tk[i] = '\0';
                    c = getNextChar();
                }
            }
            ungetChar(c);
            if (state == -3) ungetChar('.');
            switch (-state) {
                case 2:
                    return new Token(Token.TokenType.INT, tk, i);
                case 3:
                    return new Token(Token.TokenType.INT, tk, i - 1);
                case 6:
                case 8:
                    return new Token(Token.TokenType.FLOAT, tk, i);
                case 4:
                    break;
                case 15:
                    return new Token(Token.TokenType.INT, tk, i);

                default:
                    return new Token(Token.TokenType.BAD_TOKEN, tk, i);
            }
        }

        //ident starting with #
        if (c == '#') {
            i=0;
            while (Character.isLetterOrDigit(c = getNextChar())) {
                tk[i++] = c;

            }
            ungetChar(c);
            return new Token(Token.TokenType.IDENT, tk, i);
        }

        //identifier
        if (Character.isLetter(c) || c == '_' || c == '$') {
            while (Character.isLetterOrDigit(c = getNextChar()) || c == '_' || c == '$') {
                tk[i++] = c;
            }
            String testKey = new String(tk, 0, i);
            Token.TokenType type = keywords.get(testKey);
            if(type == null) {
                type = Token.TokenType.IDENT;
            }
            ungetChar(c);
            return new Token(type, tk, i);
        }

        //identifier starting with a @
        if ( c=='@') {
            while (Character.isDigit(c = getNextChar()) || c == ';') {
                tk[i++] = c;
            }
            ungetChar(c);
            return new Token(Token.TokenType.IDENT,tk,i);
        }

        //OLDCOMMENT
        if (tk[0] == '/' && (c = getNextChar()) == '*') {
            tk[1] = '*';

            while ((c = getNextChar()) != '*' || (c = getNextChar()) != '/') {

            }
            return new Token(Token.TokenType.LONGCOMMENT, tk, i + 1);
        }
        if (tk[0] == '/') ungetChar(c);

        //LINECOMMENT
        if (tk[0] == '/' && (c = getNextChar()) == '/') {
            tk[1] = '/';

            while ((c = getNextChar()) != '\n'  && c != (char) -1) {

            }
            return new Token(Token.TokenType.LINECOMMENT, tk, i + 1);
        }
        if (tk[0] == '/') {
            ungetChar(c);
        }

        //CONSTSTRING
        if (tk[0] == '"') {
            boolean badString = false;
            while ((c = getNextChar()) != '"' || (tk[i - 1] == '\\' && tk[i - 2] != '\\')) {
                if(c == '"' || c == -1) {
                    badString = true;
                }
                if (c == (char) (-1)) {
                    break;
                }
                tk[i++] = c;
            }
            if(c == '"') {
                badString = false;
            }
            if(badString) {
                return new Token(Token.TokenType.BAD_TOKEN, tk, i);
            }
            char next = getNextChar();
            if(!Character.isLetter(next)){
                ungetChar(next);
                tk[i++] = c;
                return new Token(Token.TokenType.STRING, tk, i);
            }
            else {
                tk[i] = c;
                tk[i+1] = next;
                return new Token(Token.TokenType.BAD_TOKEN, tk, i + 2);
            }
        }

        //CONSTCHAR

        if (tk[0] == '\'') {
            boolean badChar = false;
            while ((c = getNextChar()) != '\'' || (tk[i - 1] == '\\' && tk[i - 2] != '\\')) {
                if(c == '\'' || c == -1) {
                    badChar = true;
                }
                if (c == (char) (-1)) {
                    break;
                }
                tk[i++] = c;
            }
            if(c == '\'') {
                badChar = false;
            }
            if(badChar) {
                return new Token(Token.TokenType.BAD_TOKEN, tk, i);
            }
            char next = getNextChar();
            if(!Character.isLetter(next)){
                ungetChar(next);
                tk[i++] = c;
                return new Token(Token.TokenType.CHAR, tk, i);
            }
            else {
                tk[i] = c;
                tk[i+1] = next;
                return new Token(Token.TokenType.BAD_TOKEN, tk, i + 2);
            }
        }

        //punctation

        char first = c;
        char second;
        char third;

        tk[0] = first;

        switch(first) {

            case '(' :
                return new Token(Token.TokenType.OPEN_PARENT, tk, i);

            case ')' :
                return new Token(Token.TokenType.CLOSE_PARENT, tk, i);

            case '{' :
                return new Token(Token.TokenType.OPEN_BRACE, tk, i);

            case '}' :
                return new Token(Token.TokenType.CLOSE_BRACE, tk, i);

            case '[' :
                return new Token(Token.TokenType.OPEN_BRACKET, tk, i);

            case ']' :
                return new Token(Token.TokenType.CLOSE_BRACKET, tk, i);

            case ',' :
                return new Token(Token.TokenType.COMMA, tk, i);

            case ':' :
                second = getNextChar();
                if(second == '>') {
                    tk[1] = second;
                    return new Token(Token.TokenType.CLOSE_BRACKET, tk, i + 1);
                }
                ungetChar(second);
                return new Token(Token.TokenType.COLON, tk, i);

            case ';' :
                return new Token(Token.TokenType.SEMI_COLON, tk, i);

            case '+' :
                second = getNextChar();
                if(second == '+') {
                    tk[1] = second;
                    return new Token(Token.TokenType.INCREMENT, tk, i + 1);
                }
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.ASSIGN_SUM, tk, i + 1);
                }
                ungetChar(second);
                return new Token(Token.TokenType.ADD_OP, tk, i);

            case '-' :
                second = getNextChar();
                if(second == '>') {
                    tk[1] = second;
                    return new Token(Token.TokenType.ARROW, tk, i + 1);
                }
                if(second == '-') {
                    tk[1] = second;
                    return new Token(Token.TokenType.DECREMENT, tk, i + 1);
                }
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.ASSIGN_DIF, tk, i + 1);
                }
                ungetChar(second);
                return new Token(Token.TokenType.SUB_OP, tk, i);

            case '!' :
                second = getNextChar();
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.NOT_EQUAL, tk, i + 1);

                }
                ungetChar(second);
                return new Token(Token.TokenType.LOG_NOT, tk, i);

            case '~' :
                return new Token(Token.TokenType.BIT_NOT, tk, i);

            case '*' :
                second = getNextChar();
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.ASSIGN_PROD, tk, i + 1);
                }
                ungetChar(second);
                return new Token(Token.TokenType.MULT_OP, tk, i);

            case '/' :
                second = getNextChar();
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.ASSIGN_QUOT, tk, i + 1);
                }
                ungetChar(second);
                return new Token(Token.TokenType.DIV_OP, tk, i);

            case '%' :
                second = getNextChar();
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.ASSIGN_MOD, tk, i + 1);
                }
                if(second == '>') {
                    tk[1] = second;
                    return new Token(Token.TokenType.CLOSE_BRACE, tk, i + 1);
                }
                ungetChar(second);
                return new Token(Token.TokenType.MOD_OP, tk, i);

            case '<' :
                second = getNextChar();
                third = getNextChar();
                if(second == ':') {
                    tk[1] = second;
                    ungetChar(third);
                    return new Token(Token.TokenType.OPEN_BRACKET, tk, i + 1);
                }
                if(second == '%') {
                    tk[1] = second;
                    ungetChar(third);
                    return new Token(Token.TokenType.OPEN_BRACE, tk, i + 1);
                }
                if(second == '=') {
                    tk[1] = second;
                    ungetChar(third);
                    return new Token(Token.TokenType.LESS_THAN_EQUAL, tk, i + 1);
                }
                if(second == '<') {
                    if(third == '=') {
                        tk[1] = second;
                        tk[2] = third;
                        return new Token(Token.TokenType.ASSIGN_LEFT, tk, i + 2);
                    }
                    ungetChar(third);
                    tk[1] = second;
                    return new Token(Token.TokenType.BIT_LEFT, tk, i + 1);
                }
                ungetChar(third);
                ungetChar(second);
                return new Token(Token.TokenType.LESS_THAN, tk, i);

            case '>' :
                second = getNextChar();
                third = getNextChar();
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.GREATER_THAN_EQUAL, tk, i + 1);
                }
                if(second == '>') {
                    if(third == '=') {
                        tk[1] = second;
                        tk[2] = third;
                        return new Token(Token.TokenType.ASSIGN_RIGHT, tk, i + 2);
                    }
                    ungetChar(third);
                    tk[1] = second;
                    return new Token(Token.TokenType.BIT_RIGHT, tk, i + 1);
                }
                ungetChar(third);
                ungetChar(second);
                return new Token(Token.TokenType.GREATER_THAN, tk, i);

            case '&' :
                second = getNextChar();
                if(second == '&') {
                    tk[1] = second;
                    return new Token(Token.TokenType.LOG_AND, tk, i + 1);
                }
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.ASSIGN_AND, tk, i + 1);
                }
                ungetChar(second);
                return new Token(Token.TokenType.BIT_AND, tk, i);

            case '^' :
                second = getNextChar();
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.ASSIGN_XOR, tk, i + 1);
                }
                ungetChar(second);
                return new Token(Token.TokenType.BIT_XOR, tk, i);

            case '|' :
                second = getNextChar();
                if(second == '|') {
                    tk[1] = second;
                    return new Token(Token.TokenType.LOG_OR, tk, i + 1);
                }
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.ASSIGN_OR, tk, i + 1);
                }
                ungetChar(second);
                return new Token(Token.TokenType.BIT_OR, tk, i);

            case '?' :
                return new Token(Token.TokenType.TERNARY, tk, i);

            case '=' :
                second = getNextChar();
                if(second == '=') {
                    tk[1] = second;
                    return new Token(Token.TokenType.EQUAL, tk, i + 1);
                }
                ungetChar(second);
                return new Token(Token.TokenType.ASSIGN, tk, i);

        }
        return new Token(Token.TokenType.BAD_TOKEN, tk, i);

    }

    private char getNextChar() {
        if(ungetStack.isEmpty()) {
            try {
                return (char) reader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            return ungetStack.pop();
        }
        return (char) -1;
    }

    private void ungetChar(char c) {
        this.ungetStack.push(c);
    }

    public static void main(String[] args) {
        Tokenizer t = new Tokenizer("src/TestFile.c");
        Token tk = t.nextToken();
        while (!tk.getToken().equals(Token.TokenType.END)) {
            System.out.println(tk);
            tk = t.nextToken();
        }
        System.out.println(tk);
    }

}