package com.hzc.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yinbin on 2015/5/2.
 */
public class AuthFilter implements Filter {
    public void destroy() {
    }

    private static final Set<String> EXCLUDE_PAGE = new HashSet<String>();

    static {
        EXCLUDE_PAGE.add("login_lp.jsp");
    }


    private static boolean isContinue(String url) {
//        if (EXCLUDE_PAGE.contains(url)) {
//            return true;
//        }else{
//            return false;
//        }
        if (url.endsWith(".js")) {
            return true;
        }
        if (url.endsWith(".css")) {
            return true;
        }
        if (url.endsWith(".jpg")) {
            return true;
        }
        if (url.endsWith(".png")) {
            return true;
        }
        for (String s : EXCLUDE_PAGE) {
            if (url.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean judgeIsMoblie(HttpServletRequest request) {
        boolean isMoblie = false;
        String[] mobileAgents = {"iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi",
                "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod",
                "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma",
                "docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos",
                "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem",
                "wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos",
                "pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320",
                "240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac",
                "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs",
                "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi",
                "mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port",
                "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem",
                "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v",
                "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-",
                "Googlebot-Mobile"};
        if (request.getHeader("User-Agent") != null) {
            for (String mobileAgent : mobileAgents) {
                if (request.getHeader("User-Agent").toLowerCase().indexOf(mobileAgent) >= 0) {
                    isMoblie = true;
                    break;
                }
            }
        }
        return isMoblie;
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (judgeIsMoblie(request)) {
            chain.doFilter(req, resp);
            return;
        }
        Object userId = request.getSession().getAttribute("userId");
        String requestURI = request.getRequestURI();

        if (isContinue(requestURI)) {
            chain.doFilter(req, resp);
            return;
        }

        if (null != userId) {// 只有登录验证成功（session还存在时），才继续
            chain.doFilter(req, resp);
        } else {//        否则 用户界面，自动弹出窗口提示用户输入登录账号，登录成功后可以继续使用本系统
            HttpServletResponse response = (HttpServletResponse) resp;
            String header = request.getHeader("x-requested-with");
            if ("XMLHttpRequest".equals(header)) {
                response.setStatus(401);
//                response.setHeader("status", "401");
                PrintWriter writer = response.getWriter();
                writer.print("请重新登录");
                writer.flush();
//                Map<String, Object> map = new HashMap<String, Object>();
//                W.writeJsonObject(map);
            } else {
                chain.doFilter(req, resp);
            }
        }
    }

    public void init(FilterConfig config) throws ServletException {


    }

/*
    *//** 要检查的 session 的名称 *//*
    private String sessionKey;

    *//** 需要排除（不拦截）的URL的正则表达式 *//*
    private Pattern excepUrlPattern;

    *//** 检查不通过时，转发的URL *//*
    private String forwardUrl;

    @Override
    public void init(FilterConfig cfg) throws ServletException {
        sessionKey = cfg.getInitParameter("sessionKey");

        String excepUrlRegex = cfg.getInitParameter("excepUrlRegex");
        if (!StringUtils.isBlank(excepUrlRegex)) {
            excepUrlPattern = Pattern.compile(excepUrlRegex);
        }

        forwardUrl = cfg.getInitParameter("forwardUrl");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // 如果 sessionKey 为空，则直接放行
        if (StringUtils.isBlank(sessionKey)) {
            chain.doFilter(req, res);
            return;
        }

//         * 请求 http://127.0.0.1:8080/webApp/home.jsp?&a=1&b=2 时
//          * request.getRequestURL()： http://127.0.0.1:8080/webApp/home.jsp
//         * request.getContextPath()： /webApp
//         * request.getServletPath()：/home.jsp
//         * request.getRequestURI()： /webApp/home.jsp
//         * request.getQueryString()：a=1&b=2
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String servletPath = request.getServletPath();

        // 如果请求的路径与forwardUrl相同，或请求的路径是排除的URL时，则直接放行
        if (servletPath.equals(forwardUrl) || excepUrlPattern.matcher(servletPath).matches()) {
            chain.doFilter(req, res);
            return;
        }

        Object sessionObj = request.getSession().getAttribute(sessionKey);
        // 如果Session为空，则跳转到指定页面
        if (sessionObj == null) {
            String contextPath = request.getContextPath();
            String redirect = servletPath + "?" + StringUtils.defaultString(request.getQueryString());
            *//*
             * login.jsp 的 <form> 表单中新增一个隐藏表单域：
             * <input type="hidden" name="redirect" value="${param.redirect }">
             *
             *  LoginServlet.java 的 service 的方法中新增如下代码：
             *  String redirect = request.getParamter("redirect");
             *  if(loginSuccess){
             *      if(redirect == null || redirect.length() == 0){
             *          // 跳转到项目主页（home.jsp）
             *      }else{
             *          // 跳转到登录前访问的页面（java.net.URLDecoder.decode(s, "UTF-8")）
             *      }
             *  }
             *//*
//            response.sendRedirect(contextPath + StringUtils.defaultIfEmpty(forwardUrl, "/")
//                    + "?redirect=" + URLEncoder.encode(redirect, "UTF-8"));
            response.sendRedirect(redirect);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
    }*/
}
