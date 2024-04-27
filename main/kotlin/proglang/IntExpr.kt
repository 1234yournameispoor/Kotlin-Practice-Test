package proglang

sealed interface IntExpr {
    class Add(val lhs: IntExpr, val rhs: IntExpr) : IntExpr {
        override fun toString(): String = "$lhs + $rhs"
    }
    class Literal(val value: Int): IntExpr {
        override fun toString(): String = "$value"
    }
    class Var(val name: String): IntExpr {
        override fun toString(): String = name
    }
    class Mul(val lhs: IntExpr, val rhs: IntExpr): IntExpr {
        override fun toString(): String = "$lhs * $rhs"
    }
    class Sub(val lhs: IntExpr, val rhs: IntExpr): IntExpr {
        override fun toString(): String = "$lhs - $rhs"
    }
    class Div(val lhs: IntExpr, val rhs: IntExpr): IntExpr {
        override fun toString(): String = "$lhs / $rhs"
    }
    class Fact(val expr: IntExpr): IntExpr {
        override fun toString(): String = "$expr!"
    }
    class Paren(val expr: IntExpr): IntExpr {
        override fun toString(): String = "($expr)"
    }
}

fun IntExpr.eval(store: Map<String, Int>): Int = when (this) {
    is IntExpr.Add -> lhs.eval(store) + rhs.eval(store)
    is IntExpr.Literal -> value
    is IntExpr.Var -> if (name !in store.keys) throw UndefinedBehaviourException("value not in store")
    else {
        store.get(name)!!
    }
    is IntExpr.Mul -> lhs.eval(store) * rhs.eval(store)
    is IntExpr.Sub -> lhs.eval(store) - rhs.eval(store)
    is IntExpr.Div -> runCatching { lhs.eval(store) / rhs.eval(store) }.getOrElse { throw UndefinedBehaviourException("cannot divide by 0") }
    is IntExpr.Fact -> if (expr.eval(store) < 0) throw UndefinedBehaviourException("cannot find factorial of number less than 0")
    else {
        when {
            (expr.eval(store)) == 0 -> 1
            else -> IntExpr.Mul(expr, IntExpr.Fact(IntExpr.Sub(expr, IntExpr.Literal(1)))).eval(store)
        }
    }
    is IntExpr.Paren -> expr.eval(store)
}

