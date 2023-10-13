// Imports
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Spinner, Button } from 'react-bootstrap';
import { Formik, Form } from "formik";
import * as Yup from 'yup';

import fields from "./fields"

// Components
import { InputField } from "@components/forms";

// Services
import api from '@services';

// Hooks
import { useSessionStorage, useNotification } from "@hooks"

const LoginForm = () => {
    const { setSession } = useSessionStorage()
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    const notify = useNotification();

    const LoginSchema = Yup.object().shape({
        username: Yup.string().required('Username is mandatory'),
        password: Yup.string().required('Password is mandatory')
    })

    const handleSubmit = (values) => {
        const { username, password } = values;
        setLoading(true)

        api.auth.login(username.trim(), password)
            .then((user) => {
                setSession(user)
                notify.success(`Welcome back, ${user.info.name ?? user.details.username}`);
                navigate('/tickets/search', { replace: true });
            })
            .catch(err => notify.error(err.detail ?? err))
            .finally(() => setLoading(false))
    }

    return (
        <Formik
            initialValues={{ username: '', password: '' }}
            validationSchema={LoginSchema}
            onSubmit={(values) => handleSubmit(values)}
        >
            {({ touched, isValid }) => {
                const disableSubmit = (!touched.username && !touched.password) || !isValid || loading;
                return (
                    <Form>
                        {fields.login.map((props, idx) => (
                            <InputField key={idx} {...props} />
                        ))}
                        <Button variant="primary" type="submit" className='p-2 rounded-3 my-4 w-100 fw-semibold'
                            disabled={disableSubmit}>
                            {loading &&
                                <Spinner
                                    animation='grow'
                                    size='sm'
                                    as='span'
                                    role='status'
                                    aria-hidden='true'
                                    className='me-2' />
                            }
                            Sign In
                        </Button>
                    </Form>
                )
            }}
        </Formik>
    );
}

export default LoginForm;