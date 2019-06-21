package build.dream.catering.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ThreadNameHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String className = handlerMethod.getBeanType().getName();
        String methodName = handlerMethod.getMethod().getName();
        Thread thread = Thread.currentThread();
        thread.setName(className + "." + methodName + "@" + thread.getId());
        return true;
    }
}
