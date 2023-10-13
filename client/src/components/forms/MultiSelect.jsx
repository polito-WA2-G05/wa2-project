import { Field } from "formik"
import Select from 'react-select'

const MultiSelect = ({ className, placeholder, field, form, options }) => {
    const onChange = (option) => {
        form.setFieldValue(
            field.name,
            option.map((item) => item.value)
        );
    };

    const getValue = () => {
        if (options) {
            return options.filter(option => field.value.indexOf(option.value) >= 0)
        } else { return [] }
    };

    return <Select
        className={className}
        name={field.name}
        value={getValue()}
        onChange={onChange}
        placeholder={placeholder}
        options={options}
        isMulti={true}
    />
};

const MultiSelectField = ({ name, options, placeholder }) => {
    return <>
        <Field
            className={"my-5"}
            name={name}
            options={options}
            component={MultiSelect}
            placeholder={placeholder}
            isMulti={true}
        />
    </>

}

export default MultiSelectField