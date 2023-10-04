// Imports
import {Formik, Form as FormikForm, Field} from "formik";
import {Spinner, Button, Form} from 'react-bootstrap';
import * as Yup from 'yup';
import {useState} from "react";
import {useNavigate} from "react-router-dom";

// Services
import api from '../services/api';

// Hooks
import useNotification from '../hooks/useNotification';

const LoginForm = () => {
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    const notify = useNotification();

    const LoginSchema = Yup.object().shape({
        username: Yup.string().required('Username is mandatory'),
        password: Yup.string().required('Password is mandatory')
    })

    const handleSubmit = (values) => {
        const { username, password } = values
        setLoading(true)

        api.login(username, password)
            .then((user) => {
                notify.success(`Welcome back`);
                navigate('/', {replace: true});
            })
            .catch(err => notify.error(err.detail))
            .finally(() => setLoading(false))
    }

    return (
        <Formik
            initialValues={{username: '', password: ''}}
            validationSchema={LoginSchema}
            onSubmit={(values) => handleSubmit(values)}
        >
            {({touched, isValid}) => {
                const disableSubmit = (!touched.username && !touched.password) || !isValid || loading;
                return (
                    <FormikForm className={"d-flex flex-column align-items-center justify-content-center my-5 w-100"}>
                        <Form.Group className={"mb-4 w-100"} controlId="email">
                            <Field
                                className={"bg-light"}
                                name={"username"}
                                type="text"
                                placeholder="Enter Username"
                            />
                        </Form.Group>
                        <Form.Group className={"w-100"} controlId="password">
                            <Field
                                name={"password"}
                                type="password"
                                placeholder="Enter Password"
                            />
                        </Form.Group>
                        <Button variant="primary" type="submit" className='p-2 rounded-3 my-4 w-100 fw-semibold'
                                disabled={disableSubmit}>
                            {loading &&
                                <Spinner animation='grow' size='sm' as='span' role='status' aria-hidden='true'
                                         className='me-2'/>}
                            Sign In
                        </Button>
                    </FormikForm>
                )
            }}
        </Formik>
    );
}

export default LoginForm;