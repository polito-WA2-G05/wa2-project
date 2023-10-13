import { Form } from "react-bootstrap"

const SwitchButton = ({ label, onToggle }) => {
    return (
        <Form.Check
        className="my-4"
            type="switch"
            label={label}
            onChange={onToggle}
        />
    )
}

export default SwitchButton