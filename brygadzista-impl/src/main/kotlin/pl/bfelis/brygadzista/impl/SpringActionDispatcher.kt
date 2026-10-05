package pl.bfelis.brygadzista.impl

import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.core.MethodParameter
import org.springframework.core.ResolvableType
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.util.ReflectionUtils
import pl.bfelis.brygadzista.Action
import pl.bfelis.brygadzista.ActionContext
import pl.bfelis.brygadzista.ActionContextFactory
import pl.bfelis.brygadzista.ActionDispatcher
import pl.bfelis.brygadzista.ActionHandler
import pl.bfelis.brygadzista.ActionInterceptor
import pl.bfelis.brygadzista.UnsupportedActionException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier

internal class SpringActionDispatcher(
    private val beanFactory: ListableBeanFactory,
    private val contextFactories: List<ActionContextFactory>,
    private val interceptors: List<ActionInterceptor>,
) : ActionDispatcher,
    SmartInitializingSingleton {
    private val handlers = mutableMapOf<Class<*>, RegisteredHandler>()

    override fun afterSingletonsInstantiated() {
        beanFactory.beanDefinitionNames.forEach { beanName ->
            val bean = beanFactory.getBean(beanName)
            val targetClass = AopUtils.getTargetClass(bean)
            ReflectionUtils
                .getAllDeclaredMethods(targetClass)
                .filter { method -> AnnotatedElementUtils.findMergedAnnotation(method, ActionHandler::class.java) != null }
                .forEach { method ->
                    register(bean, method)
                }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R> dispatch(action: Action<R>): R {
        val handler =
            handlers[action.javaClass]
                ?: throw UnsupportedActionException(action.javaClass as Class<out Action<*>>)
        val context = contextFactories.asSequence().mapNotNull { it.create(action) }.firstOrNull() ?: ActionContext(action)
        require(handler.contextClass.isInstance(context)) {
            "@ActionHandler method ${handler.method.qualifiedName()} requires context ${handler.contextClass.name}, " +
                "but ${context.javaClass.name} was created"
        }

        val interceptorContext = context as ActionContext<Action<R>>
        var proceed: () -> R = { handler.invoke(context) as R }
        interceptors.asReversed().forEach { interceptor ->
            val next = proceed
            proceed = { interceptor.intercept(interceptorContext, next) }
        }
        return proceed()
    }

    private fun register(
        bean: Any,
        method: Method,
    ) {
        require(method.parameterCount == 1) {
            "@ActionHandler method ${method.qualifiedName()} must have exactly one parameter"
        }

        val parameter = MethodParameter(method, 0)
        val contextType = ResolvableType.forMethodParameter(parameter)
        val contextClass = contextType.rawClass
        require(contextClass != null && ActionContext::class.java.isAssignableFrom(contextClass)) {
            "@ActionHandler method ${method.qualifiedName()} must accept ActionContext<A>"
        }

        val actionContextType = contextType.`as`(ActionContext::class.java)
        val actionType = actionContextType.getGeneric(0).resolve()
        require(actionType != null && Action::class.java.isAssignableFrom(actionType)) {
            "@ActionHandler method ${method.qualifiedName()} must declare a concrete Action type"
        }
        require(!actionType.isInterface && !Modifier.isAbstract(actionType.modifiers)) {
            "@ActionHandler method ${method.qualifiedName()} must declare a concrete Action type"
        }

        val invocableMethod = AopUtils.selectInvocableMethod(method, bean.javaClass)
        check(handlers.putIfAbsent(actionType, RegisteredHandler(bean, invocableMethod, contextClass)) == null) {
            "Multiple @ActionHandler methods registered for ${actionType.name}"
        }
    }

    private data class RegisteredHandler(
        val bean: Any,
        val method: Method,
        val contextClass: Class<*>,
    ) {
        fun invoke(context: ActionContext<*>): Any? =
            try {
                method.invoke(bean, context)
            } catch (exception: InvocationTargetException) {
                throw exception.targetException
            }
    }
}

private fun Method.qualifiedName(): String = "${declaringClass.name}#$name"
