package proglang

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ProgramExecutor(threadbody: Stmt, val pause: Long, val lock: Lock, val store: MutableMap<String, Int>): Runnable {

    private var runthreadbody: Stmt? = threadbody
    override fun run() {
        while (runthreadbody != null) {
            Thread.sleep(pause)
            lock.withLock {
                runthreadbody = runthreadbody!!.step(store)
            }
        }

    }

}