package quest.prography.lh.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record ApiResponse<T>(
        Integer code,
        String message,
        T result
) {
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "API 요청이 성공했습니다.", null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "API 요청이 성공했습니다.", data);
    }

    public static <T> ApiResponse<T> fail() {
        return new ApiResponse<>(201, "불가능한 요청입니다.", null);
    }

    public static <T> ApiResponse<T> error() {
        return new ApiResponse<>(500, "에러가 발생했습니다.", null);
    }

}
