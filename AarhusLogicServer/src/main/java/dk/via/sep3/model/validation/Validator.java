package dk.via.sep3.model.validation;

public interface Validator<T>
{
    void validate(T target);

}