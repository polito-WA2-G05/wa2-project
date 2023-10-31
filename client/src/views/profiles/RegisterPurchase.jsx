// Imports
import {useContext, useState} from "react";
import {Col} from "react-bootstrap"
import {useNavigate} from "react-router-dom";
import {Form, Formik} from "formik";
import * as Yup from "yup";

// Components
import {InputField, SubmitButton} from "@components/forms";

// Services
import api from "@services";

// Hooks
import {useNotification} from "@hooks";
import {SessionContext} from "@contexts";

const RegisterPurchase = () => {
    const {onError} = useContext(SessionContext)

    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    const notify = useNotification();

    const RegisterPurchaseSchema = Yup.object().shape({
        code: Yup.string().uuid("Invalid purchase code").required()
    });

    const handleSubmit = ({code}) => {
        setLoading(true);
        api.product.registerPurchase(code)
            .then(() => {
                notify.success("Purchase correctly inserted")
                navigate(`/me/purchases`)
            })
            .catch(onError)
            .finally(() => setLoading(false));
    }
    return (
        <Col xs={12} lg={4}>
            <h1 className="my-5 fw-bold text-center">Register a new purchase</h1>
            <Formik initialValues={{code: ""}} validationSchema={RegisterPurchaseSchema} onSubmit={handleSubmit}>
                {({values, isValid}) => {
                    const disableSubmit = !values.code || !isValid || loading;

                    return (
                        <Form>
                            <InputField
                                id={"register-purchase"}
                                type={"text"}
                                name={"code"}
                                placeholder={"Purchase code"}
                            />
                            <SubmitButton loading={loading} disabled={disableSubmit}>
                                Register
                            </SubmitButton>
                        </Form>
                    );
                }}
            </Formik>
        </Col>
    );
}

export default RegisterPurchase