package proglang;

import java.util.HashMap;
import java.util.Map;

public final class SequentialProgram {
    private final Stmt stmt;
    public SequentialProgram(Stmt stmt) {
        this.stmt = stmt;
    }
    public Map<String, Integer> execute(Map<String, Integer> initialStore) {
        Map<String, Integer> store = new HashMap<>(initialStore);
        Stmt state = stmt;
        while (state != null) {
            state = StmtKt.step(state, store);
        }
        return store;
    }

    @Override
    public String toString() {
        return stmt.toString();
    }


}
