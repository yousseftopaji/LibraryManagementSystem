package dk.via.sep3.model.utils.validation;

public interface Validator<T>
{
    void validate(T target);

}