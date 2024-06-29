package dto;

public record SSEResponse<T>(String type, T data)
{
}
