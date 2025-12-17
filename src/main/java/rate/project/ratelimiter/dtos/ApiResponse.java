package rate.project.ratelimiter.dtos;

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
