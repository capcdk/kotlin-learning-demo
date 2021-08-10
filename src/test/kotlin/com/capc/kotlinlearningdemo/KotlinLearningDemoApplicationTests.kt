package com.capc.kotlinlearningdemo

import com.capc.kotlinlearningdemo.common.Fund
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.reflect.full.companionObject

@SpringBootTest
class KotlinLearningDemoApplicationTests {

    @Test
    fun contextLoads() {
    }

}

fun main() {
//    val obj = getCompanionObj(Student("",123,true))
    val a =Fund::name
    println(a as String)
}

inline fun <reified T> getCompanionObj(someone: T): Any {
    val t = T::class.companionObject
    val res = t?.objectInstance
    return res!!
}