import java.util.concurrent.locks.ReentrantLock
import kotlin.random.Random

val lock1 = ReentrantLock()
val lock2 = ReentrantLock()

data class Warrior(
    var attack: Float
)

data class Teacher(
    var intelligence: Float
)

fun calculateProcess(warrior: Warrior, teacher: Teacher, k: Int) {
    lock1.lock()
    for (i in 0 until k) {
        warrior.attack = warrior.attack + Random.nextInt(10, 15)
    }
    lock1.unlock()

    lock2.lock()
    for (i in 0 until k) {
        teacher.intelligence = teacher.intelligence + Random.nextInt(2, 11)
    }
    lock2.unlock()
}

fun main() {
    val startTime = System.currentTimeMillis()

    val warrior = Warrior(attack = 15F)
    val teacher = Teacher(intelligence = 5F)

    val N = Random.nextInt(10, 21)
    val k1 = Random.nextInt(10000, 20001)
    val k2 = Random.nextInt(10000, 20001)

    val threads = mutableListOf<Thread>()
    val times = N - 1
    for (i in 0 until times) {
        val thread = if (i >= (N / 2) - 1) {
            Thread { calculateProcess(warrior, teacher, k1) }
        } else {
            Thread { calculateProcess(warrior, teacher, k2) }
        }
        threads.add(thread)
    }

    for (i in 0 until times) {
        threads[i].start()
    }
    for (i in 0 until times) {
        threads[i].join()
    }

    val endTime = System.currentTimeMillis()

    print("Time in millis: ${endTime - startTime}")
}