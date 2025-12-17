package rate.project.ratelimiter.dtos;

/**
 * A response from the API. Can either contain data or an error message.
 * @param <T> The type of data contained in the response (if not an error).
 */
public class ApiResponse<T> {

  private T data;
  private String error;

  public ApiResponse(T data) {
    this.data = data;
  }

  public ApiResponse(String error) {
    this.error = error;
  }

  public T getData() {
    return data;
  }

  public String getError() {
    return error;
  }

}
