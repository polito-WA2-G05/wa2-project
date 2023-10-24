// Imports
import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {Col} from "react-bootstrap";
import {Form, Formik} from "formik";
import * as Yup from "yup";

// Components
import {InputField, SubmitButton} from "@components/forms";
import {SwitchButton} from "@components";

const SearchTicket = () => {
    const [searchByProduct, setSearchByProduct] = useState(false);

    const navigate = useNavigate();

    const initialValues = {id: "", ean: ""}

    const SearchTicketSchema = Yup.object().shape({
        id: Yup.number().integer().positive()
            .typeError("ticket id must be a number")
            .when({
                is: () => !searchByProduct,
                then: (schema) => schema.required(),
                otherwise: (schema) => schema.notRequired(),
            }),
        ean: Yup.number().integer().positive()
            .typeError("ean must be a number")
            .when({
                is: () => searchByProduct,
                then: (schema) => schema.required(),
                otherwise: (schema) => schema.notRequired(),
            })
    });

    const handleSubmit = ({id, ean}) => {
        const destination = "/tickets" +
            (ean ? `?product=${ean}` : `/${id}`)
        navigate(destination)
    }

    return (
        <Col xs={12} lg={4}>
            <h1 className="text-center fw-bold my-5">Search tickets</h1>
            <Formik initialValues={initialValues} validationSchema={SearchTicketSchema} onSubmit={handleSubmit}>
                {({values, isValid, resetForm}) => {
                    const disableSubmit = (!values.id && !values.ean) || !isValid

                    const handleToggle = () => {
                        setSearchByProduct(old => !old)
                        resetForm()
                    }

                    return (
                        <Form>
                            <InputField
                                id={"search-tickets"}
                                type={"text"}
                                name={!searchByProduct ? "id" : "ean"}
                                placeholder={!searchByProduct ? "Ticket Id" : "Product EAN"}
                            />
                            <SwitchButton
                                label={"Search by product EAN"}
                                onToggle={handleToggle}
                            />
                            <SubmitButton disabled={disableSubmit}>Search</SubmitButton>
                        </Form>
                    );
                }}
            </Formik>
        </Col>
    );
};

export default SearchTicket;
