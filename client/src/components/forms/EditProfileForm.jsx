// Imports
import {useContext, useState} from "react";
import {useNavigate} from "react-router-dom";
import {Button, Col, Row} from "react-bootstrap";
import {Form, Formik} from "formik";
import * as Yup from "yup";

import fields from "./fields";

// Components
import {InputField, SubmitButton} from "@components/forms";

// Services
import api from "@services";

// Hooks
import {useNotification} from "@hooks";

// Contexts
import {SessionContext} from "@contexts";

const EditProfileForm = () => {
    const {session, saveSession, onError} = useContext(SessionContext);
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    const notify = useNotification();

    const initialValues = {
        name: session.info.name,
        surname: session.info.surname
    }

    const EditProfileSchema = Yup.object().shape({
        name: Yup.string().required(),
        surname: Yup.string().required(),
    });

    const handleSubmit = ({name, surname}) => {
        setLoading(true);
        api.profile.editProfile(name, surname)
            .then((user) => {
                const updatedSession = {
                    ...session,
                    info: {
                        ...session.info,
                        name: user.name,
                        surname: user.surname,
                    },
                };
                saveSession(updatedSession);
                navigate(-1, {replace: true});
                notify.success("Your info has been successfully edited!");
            })
            .catch(onError)
            .finally(() => setLoading(false))
    };

    return <Formik initialValues={initialValues} validationSchema={EditProfileSchema} onSubmit={handleSubmit}>
        {({values, isValid}) => {
            const disableSubmit = (!values.name && !values.surname) || !isValid || loading;

            return (
                <Form>
                    {fields.editProfile.map((props, idx) => (
                        <InputField key={`input-${props.name}`} {...props} />
                    ))}
                    <FormActions
                        disableSubmit={disableSubmit}
                        loading={loading}
                        onCancel={() => navigate(-1, {replace: true})}
                    />
                </Form>
            );
        }}
    </Formik>
};

const FormActions = ({disableSubmit, loading, onCancel}) => <Row>
    <Col xs={12} lg={6}>
        <Button
            variant="secondary"
            className="py-2 px-5 rounded-3 my-5 fw-semibold w-100"
            onClick={onCancel}
        >
            Go back
        </Button>
    </Col>

    <Col xs={12} lg={6}>
        <SubmitButton loading={loading} disabled={disableSubmit}>
            Edit profile
        </SubmitButton>
    </Col>
</Row>

export default EditProfileForm;
