package org.joychou.controller.jsonp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.joychou.security.SecurityUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


/**
 * @author  JoyChou (joychou@joychou.org) @ 2018.10.24
 * https://github.com/JoyChou93/java-sec-code/wiki/JSONP
 */

@RestController
@RequestMapping("/jsonp")
public class JSONP {

    private static String[] urlwhitelist = {"joychou.com", "joychou.org"};


    // get current login username
    public static String getUserInfo(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        String username = principal.getName();

        Map m = new HashMap();
        m.put("Username", username);

        return JSON.toJSONString(m);
    }
    /**
     * Set the response content-type to application/javascript.
     *
     * http://localhost:8080/jsonp/referer?callback=test
     *
     */
    @RequestMapping(value = "/referer", produces = "application/javascript")
    private String referer(HttpServletRequest request, HttpServletResponse response) {
        String callback = request.getParameter("callback");
        return callback + "(" + getUserInfo(request) + ")";
    }

    /**
     * Direct access does not check Referer, non-direct access check referer.
     * Developer like to do jsonp testing like this.
     *
     * http://localhost:8080/jsonp/emptyReferer?callback=test
     *
     */
    @RequestMapping(value = "/emptyReferer", produces = "application/javascript")
    private String emptyReferer(HttpServletRequest request, HttpServletResponse response) {
        String referer = request.getHeader("referer");

        if (null != referer && !SecurityUtil.checkURLbyEndsWith(referer, urlwhitelist)) {
            return "error";
        }

        String callback = request.getParameter("callback");
        return callback + "(" + getUserInfo(request) + ")";
    }

    /**
     * Adding callback or cback on parameter can automatically return jsonp data.
     * http://localhost:8080/jsonp/advice?callback=test
     * http://localhost:8080/jsonp/advice?_callback=test
     *
     * @return Only return object, AbstractJsonpResponseBodyAdvice can be used successfully.
     *         Such as JSONOjbect or JavaBean. String type cannot be used.
     */
    @RequestMapping(value = "/advice", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject advice(HttpServletRequest request) {
        return JSON.parseObject(getUserInfo(request));

    }

    /**
     * Safe code.
     * http://localhost:8080/jsonp/sec?callback=test
     */
    @RequestMapping(value = "/sec", produces = "application/javascript")
    private String safecode(HttpServletRequest request, HttpServletResponse response) {
        String referer = request.getHeader("referer");

        if (!SecurityUtil.checkURLbyEndsWith(referer, urlwhitelist)) {
            return "error";
        }

        String callback = request.getParameter("callback");
        return callback + "(" + getUserInfo(request) + ")";
    }



}