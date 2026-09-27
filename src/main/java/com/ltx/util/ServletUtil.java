package com.ltx.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Servlet工具类
 *
 * @author tianxing
 */
public class ServletUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 返回JSON格式的数据给前端
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param obj      对象
     */
    public static void write(HttpServletResponse response, Object obj) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(OBJECT_MAPPER.writeValueAsString(obj));
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
