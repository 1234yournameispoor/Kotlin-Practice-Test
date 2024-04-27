package proglang

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ConcurrentProgram(private val listS: List<Stmt>, private val listL: List<Long> ) {
    init {
        if (listS.size != listL.size) throw IllegalArgumentException("Lists are not same size")
    }

    val lock: Lock = ReentrantLock()

    fun execute(initstore: Map<String, Int>): Map<String, Int> {
        val store = HashMap(initstore)
        val pairs = listS.zip(listL)
        val programs = pairs.map { x -> ProgramExecutor(x.first, x.second, lock, store) }
        val threads = programs.map { Thread(it) }
        threads.forEach {
            it.start()
        }
        threads.forEach {
                it.join()
        }
        return store
    }
}