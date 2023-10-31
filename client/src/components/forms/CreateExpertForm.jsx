// Imports
import {useContext, useState} from "react";
import {useNavigate} from "react-router-dom";
import {Button, Col, Row} from "react-bootstrap";
import {Form, Formik} from "formik";
import * as Yup from "yup";
import YupPassword from "yup-password";

import fields from "./fields"

// Components
import {InputField, MultiSelect, SubmitButton} from "@components/forms";

// Services
import api from "@services";

// Hooks
import {useNotification} from "@hooks";
import {SessionContext} from "@contexts";

YupPassword(Yup);

const CreateExpertForm = ({specializations}) => {
    const [loading, setLoading] = useState(false);

    const {onError} = useContext(SessionContext)

    const notify = useNotification();
    const navigate = useNavigate();

    const specializationOptions = specializations
        .map(specialization => ({
            label: specialization.name,
            value: specialization.id,
        }))

    const initialValues = {
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
        specializations: []
    }

    const CreateExpertSchema = Yup.object().shape({
        username: Yup.string().required(),
        email: Yup.string().email().required(),
        password: Yup.string().password().required(),
        confirmPassword: Yup.string()
            .oneOf([Yup.ref("password"), null], "Passwords must match")
            .required(),
        specializations: Yup.array()
            .of(Yup.number().integer())
            .min(1, "Select at least 1 specialization")
    })

    const handleCancel = () => {
        navigate(-1, {replace: true})
    }

    const handleSubmit = ({username, email, password, specializations}) => {
        setLoading(true);
        api.manager
            .createExpert(username, email, password, specializations)
            .then(() => {
                notify.success("Expert successfully created");
                navigate("/manager/experts");
            })
            .catch((err) => console.log(err))
            .finally(() => setLoading(false));
    };

    return <Formik initialValues={initialValues} validationSchema={CreateExpertSchema} onSubmit={handleSubmit}>
        {({touched, isValid}) => {
            const disableSubmit = (Object.values(touched).length === 0 && isValid) || !isValid || loading;

            return (
                <Form>
                    <Row>
                        {fields.createExpert.map(props =>
                            <Col key={`input-${props.name}`} xs={12} sm={6}>
                                <InputField {...props} />
                            </Col>
                        )}
                    </Row>

                    <MultiSelect
                        name={"specializations"}
                        options={specializationOptions}
                        placeholder={"Select specializations..."}
                    />

                    <FormActions disableSubmit={disableSubmit} loading={loading} onCancel={handleCancel}/>
                </Form>
            );
        }}
    </Formik>

}

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
            Create expert
        </SubmitButton>
    </Col>
</Row>

export default CreateExpertForm;
