package grace.controller.traits

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import java.text.SimpleDateFormat

/**
 * 基本变量
 */
trait RequestBase {
    HttpServletRequest request
    HttpServletResponse response
    Params params
    Map<String, String> headers

    /**
     * session
     * @return
     */
    HttpSession getSession() {
        request.getSession(true)
    }

    /**
     * context
     * @return
     */
    ServletContext getContext() {
        request.getServletContext()
    }

    /**
     * headers 延时加载
     * @return
     */
    Map<String, String> getHeaders() {
        if (headers) return headers

        headers = new LinkedHashMap<String, String>();
        for (Enumeration names = request.getHeaderNames(); names.hasMoreElements();) {
            String headerName = (String) names.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }

        return headers
    }
    /**
     * 获取参数，延时加载
     * @return
     */
    Params getParams() {
        if (params) return params

        params = new Params();
        for (Enumeration names = request.getParameterNames(); names.hasMoreElements();) {
            String name = (String) names.nextElement();
            if (!params.containsKey(name)) {
                String[] values = request.getParameterValues(name);
                if (values.length == 1) {
                    params.put(name, values[0]);
                } else {
                    params.put(name, values);
                }
            }
        }

        return params;
    }

    /**
     * 参数类，并提供一些方便的转换方法。异常需要自己处理
     */
    static class Params extends HashMap<String, Object> {
        Date date(String key, String format = 'yyyy-MM-dd') {
            def value = super.get(key)
            if (value instanceof Date) return value
            return new SimpleDateFormat(format).parse(value.toString())
        }
    }

    Map toMap(){
        return [request:request,response:response,session:session,context:context,params:params,headers:headers]
    }
}