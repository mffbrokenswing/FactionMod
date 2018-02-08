package factionmod.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LogicalParser<A> {

    protected final HashMap<String, A>                  functions;
    protected final HashMap<String, LogicalOperator<A>> operators;

    private final LogicalTokenizer tokenizer;

    public LogicalParser(LogicalTokenizer tokenizer) {
        this.functions = new HashMap<>();
        this.operators = new HashMap<>();
        this.tokenizer = tokenizer;
    }

    public void addFunction(String name, A func) {
        this.functions.put(name, func);
    }

    public void addFunctions(Map<String, A> functions) {
        this.functions.putAll(functions);
    }

    public void addOperators(Map<String, LogicalOperator<A>> operators) {
        this.operators.putAll(operators);
    }

    public void addOperator(String name, LogicalOperator<A> operator) {
        this.operators.put(name, operator);
    }

    public A parse() throws Exception {
        Stack<LogicalOperator<A>> parsedOperators = new Stack<>();
        Stack<A> parsedFunctions = new Stack<>();
        while (tokenizer.hasNext()) {
            String token = tokenizer.next();
            if (functions.containsKey(token)) {
                parsedFunctions.push(functions.get(token));
            } else if (operators.containsKey(token)) {
                if (parsedFunctions.isEmpty())
                    throw new Exception("Operators must have left assignment.");

                LogicalOperator<A> currentOp = operators.get(token);
                if (!parsedOperators.isEmpty()) {
                    if (parsedOperators.peek().getPriority() >= currentOp.getPriority()) {
                        A func = parsedOperators.pop().getOperator().apply(parsedFunctions.pop(), parsedFunctions.pop());
                        parsedFunctions.push(func);
                    }
                }
                parsedOperators.push(currentOp);
            } else if (token.equals("(")) {
                LogicalParser<A> parser = new LogicalParser<>(tokenizer);
                parser.addFunctions(this.functions);
                parser.addOperators(this.operators);
                parsedFunctions.push(parser.parse());
            } else if (token.equals(")")) {
                break;
            }
        }

        if (parsedFunctions.size() - 1 != parsedOperators.size())
            throw new Exception("Wrong parity operator/functions (" + parsedOperators.size() + "/" + parsedFunctions.size() + ").");

        while (!parsedOperators.isEmpty()) {
            A value = parsedOperators.pop().getOperator().apply(parsedFunctions.pop(), parsedFunctions.pop());
            parsedFunctions.push(value);
        }

        return parsedFunctions.pop();
    }

}
