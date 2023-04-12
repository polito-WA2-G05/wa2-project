// Import
import {useState} from "react";

// Components
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

const DEFAULT_INITIAL_VALUES = {
    name: "",
    surname: "",
    email: ""
}

const ProfileForm = ({handleSubmit, initialValues = DEFAULT_INITIAL_VALUES}) => {

    const [values, setValues] = useState(initialValues)

    const handleReset = () => setValues(initialValues)

    const handleSubmitForm = (e) => {
        e.preventDefault()
        handleSubmit(values)
    }

    return (
        <Form onSubmit={handleSubmitForm}>
            <Form.Group className="mb-3" controlId="profile-form-name">
                <Form.Label><span className="fw-semibold">Name</span></Form.Label>
                <Form.Control type="text" placeholder="Insert your name" value={values.name}
                              onChange={(event) => setValues((old) => ({...old, name: event.target.value}))}/>
            </Form.Group>
            <Form.Group className="mb-3" controlId="profile-form-surname">
                <Form.Label><span className="fw-semibold">Surname</span></Form.Label>
                <Form.Control type="text" placeholder="Insert your surname" value={values.surname}
                              onChange={(event) => setValues((old) => ({...old, surname: event.target.value}))}/>
            </Form.Group>
            <Form.Group className="mb-5" controlId="profile-form-email">
                <Form.Label><span className="fw-semibold">Email Address</span></Form.Label>
                <Form.Control type="text" placeholder="Insert your email" value={values.email}
                              onChange={(event) => setValues((old) => ({...old, email: event.target.value}))}/>
            </Form.Group>
            <div>
                <Button variant={"primary"} type={"submit"}>Submit</Button>
                <Button className={"ms-2"} variant={"danger"} onClick={handleReset}>Reset</Button>
            </div>
        </Form>
    );
};

export default ProfileForm;
