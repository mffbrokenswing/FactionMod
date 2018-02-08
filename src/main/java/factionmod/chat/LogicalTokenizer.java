package factionmod.chat;

import java.util.NoSuchElementException;
import java.util.Stack;

import javax.annotation.Nullable;

public class LogicalTokenizer {

    private Stack<String> leftStack    = new Stack<>();
    private String        currentToken = null;
    private Stack<String> rightStack   = new Stack<>();
    private int           size;

    public LogicalTokenizer(String str) {
        String token = "";
        for (char c : str.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                if (!token.isEmpty()) {
                    leftStack.push(token);
                    token = "";
                }
            } else if (c == '(') {
                if (!token.isEmpty()) {
                    leftStack.push(token);
                    token = "";
                }
                leftStack.push("(");
            } else if (c == ')') {
                if (!token.isEmpty()) {
                    leftStack.push(token);
                    token = "";
                }
                leftStack.push(")");
            } else {
                token += c;
            }
        }

        if (!token.isEmpty())
            leftStack.add(token);

        while (!leftStack.isEmpty())
            rightStack.push(leftStack.pop());

        this.size = rightStack.size();
    }

    public int getTokenCount() {
        return size;
    }

    public String next() {
        if (rightStack.isEmpty())
            throw new NoSuchElementException("There's no more elements.");

        if (this.currentToken != null)
            leftStack.push(this.currentToken);

        this.currentToken = rightStack.pop();
        return this.currentToken;
    }

    public boolean hasNext() {
        return !this.rightStack.isEmpty();
    }

    @Nullable
    public String previous() {
        if (leftStack.isEmpty())
            throw new NoSuchElementException("There's no more elements.");

        rightStack.push(this.currentToken);
        this.currentToken = leftStack.pop();
        return this.currentToken;
    }

    public boolean hasPrevious() {
        return !leftStack.isEmpty();
    }

    @Nullable
    public String currentToken() {
        return this.currentToken;
    }

    public void reset() {
        while (!leftStack.isEmpty()) {
            rightStack.push(leftStack.pop());
        }
    }

}
