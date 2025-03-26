package com.example.mariadb_demo.exception;

public class CustomExceptions {

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class EmailNotVerifiedException extends RuntimeException {
        public EmailNotVerifiedException(String message) {
            super(message);
        }
    }

    public static class PhoneNotVerifiedException extends RuntimeException {
        public PhoneNotVerifiedException(String message) {
            super(message);
        }
    }

    public static class AdminModificationNotAllowedException  extends RuntimeException {
        public AdminModificationNotAllowedException (String message) {
            super(message);
        }
    }

}
