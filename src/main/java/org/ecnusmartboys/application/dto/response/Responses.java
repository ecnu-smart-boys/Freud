package org.ecnusmartboys.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一接口返回数据类型。
 *
 * @param <T> 任意数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Responses<T> {
    private static final int DEFAULT_ERROR_STATUS = -1;
    /**
     * 业务状态码，与http状态码不相同。
     */
    private Integer status;
    /**
     * 返回信息，抛出异常时会附带异常信息，正常返回任意值（"ok", null）皆可。
     */
    private String message;
    /**
     * 返回数据，正常返回时返回的数据，异常时返回null。
     */
    private T data;

    /**
     * 返回成功，不带数据。
     *
     * @param <T> /
     * @return /
     */
    public static <T> Responses<T> ok() {
        return ok("ok", null);
    }

    /**
     * 如果返回数据的泛型为{@link String}，请使用{@link #ok(String, Object)}。<br/>
     * <code>
     * BaseResponse&lt;String&gt; resp = BaseResponse.ok("ok", str);
     * </code>
     *
     * @param data /
     * @param <T>  /
     * @return /
     */
    public static <T> Responses<T> ok(T data) {
        return ok("ok", data);
    }

    public static <T> Responses<T> ok(String message, T data) {
        return new Responses<>(200, message, data);
    }

    /**
     * {@inheritDoc}
     *
     * @see #ok(Object)
     */
    public static <T> Responses<T> ok(String message) {
        return Responses.ok(message, null);
    }

    /**
     * 请求异常返回，{@link #status}为400。
     *
     * @param message 异常信息
     * @param <T>     /
     * @return /
     */
    public static <T> Responses<T> error(String message) {
        return error(DEFAULT_ERROR_STATUS, message);
    }

    /**
     * 请求异常返回。
     *
     * @param status  业务状态码
     * @param message 异常信息
     * @param <T>     /
     * @return /
     */
    public static <T> Responses<T> error(int status, String message) {
        return new Responses<>(status, message, null);
    }
}
