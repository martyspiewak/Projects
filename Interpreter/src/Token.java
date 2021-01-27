/**
 * Created by Marty on 10/26/2017.
 */
public class Token {

    private String lexeme;
    private TokenType token;

    public Token(TokenType type, char[] value, int end) {
        this.token = type;
        if(value != null) {
            String temp = "";
            for(int i = 0; i < end; i++) {
                temp += value[i];
            }
            this.lexeme = temp;
        }
        else {
            lexeme = null;
        }
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public TokenType getToken() {
        return token;
    }

    public void setToken(TokenType token) {
        this.token = token;
    }

    @Override
    public String toString() {
        String result = "Token: ";
        result += this.token;
        result += ", LEXEME: ";
        result += this.lexeme;
        return result;
    }

    public enum TokenType {
        AUTO,
        DOUBLE,
        INT,
        STRUCT,
        CONST,
        FLOAT,
        SHORT,
        UNSIGNED,
        BREAK,
        ELSE,
        LONG,
        SWITCH,
        CONTINUE,
        FOR,
        SIGNED,
        VOID,
        CASE,
        ENUM,
        REGISTER,
        TYPEDEF,
        DEFAULT,
        GOTO,
        SIZEOF,
        VOLATILE,
        CHAR,
        STRING,
        EXTERN,
        RETURN,
        UNION,
        DO,
        IF,
        STATIC,
        WHILE,
        IDENT,
        VARIADIC,
        OPEN_BRACKET,
        CLOSE_BRACKET,
        OPEN_PARENT,
        CLOSE_PARENT,
        OPEN_BRACE,
        CLOSE_BRACE,
        ADD_OP,
        SUB_OP,
        MULT_OP,
        DIV_OP,
        MOD_OP,
        INCREMENT,
        DECREMENT,
        PERIOD,
        ARROW,
        LOG_NOT,
        BIT_NOT,
        ALIGNOF,
        BIT_LEFT,
        BIT_RIGHT,
        LESS_THAN,
        GREATER_THAN,
        LESS_THAN_EQUAL,
        GREATER_THAN_EQUAL,
        EQUAL,
        NOT_EQUAL,
        BIT_AND,
        BIT_XOR,
        BIT_OR,
        LOG_AND,
        LOG_OR,
        TERNARY,
        ASSIGN,
        ASSIGN_SUM,
        ASSIGN_DIF,
        ASSIGN_PROD,
        ASSIGN_QUOT,
        ASSIGN_MOD,
        ASSIGN_LEFT,
        ASSIGN_RIGHT,
        ASSIGN_AND,
        ASSIGN_OR,
        ASSIGN_XOR,
        COMMA,
        COLON,
        SEMI_COLON,
        END,
        LONGCOMMENT,
        LINECOMMENT,
        BAD_TOKEN
    }
}