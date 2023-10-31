// Imports
import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Formik, Form } from "formik";
import * as Yup from 'yup';

import { Role } from "@utils"

import fields from "./fields"

// Components
import { InputField, SubmitButton } from "@components/forms";

// Services
import api from '@services';

// Hooks
import { useNotification } from "@hooks"
import { SessionContext } from "@contexts";

const LoginForm = () => {
    const [loading, setLoading] = useState(false);

    const { saveSession } = useContext(SessionContext)

    const navigate = useNavigate();
    const notify = useNotification();

    const initialValues = { username: "", password: "" }

    const LoginSchema = Yup.object().shape({
        username: Yup.string().required(),
        password: Yup.string().required()
    })

    const handleSubmit = ({ username, password }) => {
        setLoading(true)
        api.auth.login(username.trim(), password)
            .then((user) => {
                saveSession(user)
                notify.success(`Welcome back, ${user.info.name ?? user.details.username}`);
                navigate(user.details.authorities[0] === Role.EXPERT ? '/tickets' : '/tickets/search', { replace: true });
            })
            .catch(err => notify.error(err.detail ?? err))
            .finally(() => setLoading(false))
    }

    return (
        <Formik initialValues={initialValues} validationSchema={LoginSchema} onSubmit={handleSubmit}>
            {({ touched, isValid }) => {
                const disableSubmit = (Object.values(touched).length === 0 && isValid) || !isValid || loading;

                return <Form>
                    {fields.login.map(props => (
                        <InputField key={props.name} {...props} />
                    ))}
                    <SubmitButton loading={loading} disabled={disableSubmit}>Login</SubmitButton>
                </Form>

            }}
        </Formik>
    );
}

export default LoginForm;