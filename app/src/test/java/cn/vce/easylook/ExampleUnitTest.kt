package cn.vce.easylook

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        val qq = QQObservable()
        qq.addObserver(UserObserver())
        qq.pushMessage("User message num 1!")
    }
}


interface Observer{
    val Tag: String
        get() = javaClass.simpleName

    fun update(message: String)
}

class UserObserver: Observer{
    override fun update(message: String) {
        //println("$Tag:$message")
        System.err.println("$Tag:$message")
    }
}
interface Observable{
    fun addObserver(observer: Observer)
    fun removeObserver(observer: Observer)
    fun notifyObserver()

    fun pushMessage(message: String)
}

class QQObservable: Observable{
    private val observerList: ArrayList<Observer> = ArrayList()
    private var message: String = ""
    override fun addObserver(observer: Observer) {
        observerList.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observerList.remove(observer)
    }

    override fun notifyObserver() {
        observerList.forEach {
            it.update(message)
        }
    }

    override fun pushMessage(message: String) {
        this.message = message
        notifyObserver()
    }

}

