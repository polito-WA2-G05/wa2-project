// Imports
import { Form } from 'react-bootstrap';
import { ErrorMessage, Field, useField } from 'formik';
import classNames from 'classnames';

const InputField = ({ id, name, type, placeholder, ...props }) => {
    const [field, meta] = useField(name);

    const groupClass = classNames({
        'my-3 rounded-3': true,
        'form-check form-switch': type === 'checkbox'
    })

    const fieldClass = classNames({
        'form-control': type === 'text' || props.as ==='textarea' || type === 'date' || type === 'email' || type === 'password',
        'form-select': props.as === 'select',
        'form-check-input': type === 'checkbox',
        'is-invalid': type !== 'checkbox' && meta.touched && meta.error,
        'is-valid': type !== 'checkbox' && meta.touched && !meta.error
    })

    return (
        <Form.Group className={groupClass}>
            <Form.Label className="fw-semibold">{props.label}</Form.Label>
            {
                props.as === 'select' ? (
                    <Field
                        id={id}
                        name={field.name}
                        type={type}
                        placeholder={placeholder}
                        className={fieldClass}
                        as={props.as}
                        rows={props.rows}
                        disabled={props.disabled}
                        value={meta.value}
                    >
                        <option value={-1}>
                            {props.defaultOptionLabel || "Select an option..."}
                        </option>
                        {
                            props.options.map((option, index) => {
                                return (
                                    <option key={index} value={option.id}>
                                        {option.name}
                                    </option>
                                );
                            })
                        }
                    </Field>
                ) : <Field
                    id={id}
                    name={field.name}
                    type={type}
                    placeholder={placeholder}
                    className={fieldClass}
                    as={props.as}
                    rows={props.rows}
                    disabled={props.disabled}
                />
            }
            <Form.Text className='text-danger'>
                {
                    props.max &&
                    <small className={`me-2 ${meta.touched && meta.error ? 'text-danger' : 'text-muted'}`}>
                        {meta.value.length}/{props.max} characters
                    </small>
                }
                <ErrorMessage name={field.name} />
            </Form.Text>
        </Form.Group>
    );
}

export default InputField