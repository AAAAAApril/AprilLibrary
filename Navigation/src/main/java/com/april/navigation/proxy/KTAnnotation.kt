package com.april.navigation.proxy

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

private fun trash() {
    Bundle().apply {

    }
}

public class TrashFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments.apply {

        }
    }
}

//==================================================================================================

object Keys {
    const val Name = "Name"
    const val Age = "Age"
    const val Email = "Email"
}

interface FragmentApi {

    fun createTrashFragment(
        @ArgumentKey(Keys.Name) name: String,
        @ArgumentKey(Keys.Age) age: Int,
        @ArgumentKey(Keys.Email) email: String
    ): TrashFragment

}

//==================================================================================================

/**
 * 参数 key 注解
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ArgumentKey(
    val value: String
)

/**
 * 模仿 Retrofit，利用动态代理，实现 Fragment 的创建以及传值约束。—— by April丶
 */
object FragmentCreator {

    fun <T> create(clazz: Class<T>): T {
        return Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf<Class<*>>(clazz),
            InvocationHandler { proxy, method, args ->
                if (method.declaringClass == Object::class.java) {
                    return@InvocationHandler method.invoke(this, args)
                } else {
                    return@InvocationHandler createFragment(method)
                }
            }
        ) as T
    }

    /**
     * 创建 Fragment 实例
     */
    private fun createFragment(method: Method): Any {
        val instance = method.returnType.newInstance()
        if (instance is Fragment) {
            setArguments(instance, method)
        } else {
            throw IllegalArgumentException("只能创建 Fragment 及其子类的实例")
        }
        return instance
    }

    /**
     * 给 Fragment 设置参数
     */
    private fun <T : Fragment> setArguments(fragment: T, method: Method) {
        //参数列表里面，参数上的注解列表（这是一个二维数组，第一维是参数的类型列表，第二维是该参数上的注解列表）
//        for (annotations in method.parameterAnnotations) {
//
//            for (parameter in parameters) {
//
//            }
//        }
    }

    private fun getArgumentKey() {

    }

}

//==================================================================================================

