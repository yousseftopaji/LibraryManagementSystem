package dk.via.sep3.application.services.validation;

public interface Validator<T>
{
    void validate(T target);

}