package test.sdc.resilience4j;

public final class CatFactServiceAccessException extends RuntimeException {
    public CatFactServiceAccessException(String message) {
        super(message);
    }
}
