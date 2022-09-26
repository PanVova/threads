import kotlin.system.measureTimeMillis

fun sleepThread() {
    Thread.sleep(5000)
}


fun main() {
    val timeInMilliseconds = measureTimeMillis {
        val t1 = Thread(::sleepThread)
        val t2 = Thread(::sleepThread)
        t1.start()
        t2.start()
    }
    print(timeInMilliseconds)
}