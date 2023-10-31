// Imports
import {useNavigate} from "react-router-dom";
import {Col} from "react-bootstrap";
import {Form, Formik} from "formik";
import * as Yup from "yup";

// Components
import {InputField, SubmitButton} from "@components/forms";

const SearchProduct = () => {
    const navigate = useNavigate();

    const SearchProductSchema = Yup.object().shape({
        ean: Yup.number().integer().positive().typeError("ean must be a number").required(),
    });

    const handleSubmit = ({ean}) => {
        navigate(`/products/${ean}`)
    }

    return (
        <Col xs={12} lg={4}>
            <h1 className="text-center fw-bold my-5">Search product</h1>
            <Formik initialValues={{ean: ""}} validationSchema={SearchProductSchema} onSubmit={handleSubmit}>
                {({values, isValid}) => {
                    const disableSubmit = !values.ean || !isValid;

                    return (
                        <Form>
                            <InputField
                                id={"search-ean"}
                                type={"text"}
                                name={"ean"}
                                placeholder={"Product EAN"}
                            />
                            <SubmitButton disabled={disableSubmit}>Search</SubmitButton>
                        </Form>
                    );
                }}
            </Formik>
        </Col>
    );
};

export default SearchProduct;
