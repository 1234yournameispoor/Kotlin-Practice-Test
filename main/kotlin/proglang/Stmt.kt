package proglang

import java.util.concurrent.locks.Condition

sealed interface Stmt {
    var next: Stmt?

    val lastInSequence: Stmt? get() {
        var cs = this
        while (cs.next != null) {
            cs = cs.next!!
        }
        return cs
    }

    fun toString(indent: Int): String

    fun clone(): Stmt

    class Assign(val name: String, val expr: IntExpr, override var next: Stmt? = null): Stmt {

        override fun toString(indent: Int): String {
            val firstline = "${" ".repeat(indent)}$name = $expr\n"
            return if (next != null) {
                firstline + next!!.toString(indent)
            } else {
                firstline
            }
        }

        override fun toString(): String = this.toString(0)

        override fun clone(): Stmt = Assign(name, expr, next?.clone())
    }

     class If(val condition: BoolExpr, val thenStmt: Stmt, val elseStmt: Stmt? = null, override var next: Stmt? = null): Stmt {

         override fun toString(indent: Int): String {
            val firstline = "${" ".repeat(indent)}if ($condition) {\n"
            val secondline = "${thenStmt.toString(indent + 4)}${" ".repeat(indent)}}"
            if (elseStmt != null) {
                val text = firstline + secondline + " else {\n${elseStmt.toString(indent + 4)}${" ".repeat(indent)}}\n"
                return if (next != null) {
                    text + next!!.toString(indent)
                } else {
                    text
                }
            }
            else {
                val text = firstline + secondline + "\n"
                return if (next != null) {
                    text + next!!.toString(indent)
                } else {
                    text
                }
            }
        }

         override fun toString(): String = this.toString(0)

         override fun clone(): Stmt = If(condition, thenStmt.clone(), elseStmt?.clone(), next?.clone())
    }

    class While(val condition: BoolExpr, val body: Stmt?, override var next: Stmt? = null): Stmt {
        override fun toString(indent: Int): String {
            val firstline = "${" ".repeat(indent)}while ($condition) {"
            return if (body != null) {
                val text = firstline + "\n${body.toString(indent + 4)}${" ".repeat(indent)}}\n"
                if (next != null) {
                    text + next!!.toString(indent)
                } else {
                    text
                }
            } else {
                if (next != null) {
                    firstline + "}\n" + next!!.toString(indent)
                } else {
                    "$firstline}\n"
                }
            }
        }

        override fun toString(): String = this.toString(0)

        override fun clone(): Stmt = While(condition, body?.clone(), next?.clone())
    }


}

fun Stmt.step(store: MutableMap<String, Int>): Stmt? {
    if (this is Stmt.Assign) {
        try {
            val ans = expr.eval(store)
            store[name] = ans
        } catch (_:UndefinedBehaviourException) {
            throw UndefinedBehaviourException("")
        }
        return next
    }

    if (this is Stmt.If) {
        val boolval = condition.eval(store)
        return if (boolval) {
            thenStmt.lastInSequence?.next = next
            thenStmt
        } else {
            if (elseStmt == null) {
                next
            } else {
                elseStmt.lastInSequence?.next = next
                elseStmt
            }
        }
    }
    if (this is Stmt.While) {
        val boolval = condition.eval(store)
        if (boolval && body != null) {
            val returned = body.clone()
            returned.lastInSequence?.next = this
            return returned
        }
        return if (boolval) {
            this
        } else {
            next
        }
    }
    else return null
}