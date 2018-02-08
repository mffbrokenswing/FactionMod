package factionmod.chat;

import java.util.function.BiFunction;

public class LogicalOperator<A> {

    protected final BiFunction<A, A, A> operator;
    protected final int                 priority;

    public LogicalOperator(BiFunction<A, A, A> operator, int priority) {
        this.operator = operator;
        this.priority = priority;
    }

    public BiFunction<A, A, A> getOperator() {
        return operator;
    }

    public int getPriority() {
        return priority;
    }

}
