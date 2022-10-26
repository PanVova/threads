import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

// data class overrides equals and hashcode automatically
// and set, get for each variable
data class QueueElement(
    val initialValue: Int,
    var currentValue: Int,
    var steps: Int
) {
    override fun toString(): String {
        return "start: $initialValue steps: $steps"
    }
}

fun collatz(number: Int): Int {
    return if (number % 2 == 0) number / 2 else 3 * number + 1
}


// variables
const val n = 10000
const val threadsSize = 5
var activeThreadsSize = 5
val elements = ArrayDeque<QueueElement>()
val result = mutableListOf<QueueElement>()

// using ReentrantLock wanted to use Mutex but for this we need to make function suspended and coroutines
val numbersMutex = ReentrantLock()
val resultMutex = ReentrantLock()

fun calculateCollatz() {
    var number: QueueElement

    while (result.size < n - 1) {
        numbersMutex.withLock {
            number = elements.pop()
        }

        number.apply {
            currentValue = collatz(number.currentValue)
            steps += 1
        }

        if (number.currentValue == 1) {
            resultMutex.lock()
            result.add(number)
            println(number.toString())

            /** so basically we check if the value is 1 for some number and in this case we decrease [activeThreadsSize] value */
            if (n - 1 - result.size < activeThreadsSize) {
                activeThreadsSize--
                resultMutex.unlock()
                break
            }
            resultMutex.unlock()

        } else {
            numbersMutex.withLock { elements.add(number) }
        }
    }
}

fun main() {
    repeat(n - 1) {
        elements.add(QueueElement(it + 2, it + 2, 0))
    }

    val threads = MutableList(threadsSize) {
        Thread { calculateCollatz() }
    }

    val timeStart = System.currentTimeMillis()

    repeat(threadsSize) {
        threads[it].start()
    }

    repeat(threadsSize) {
        threads[it].join()
    }

    val endTime = System.currentTimeMillis()
    val timeInMillis = (endTime - timeStart)

    println("Execution time: ${timeInMillis}ms")
}

