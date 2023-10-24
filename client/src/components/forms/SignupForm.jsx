// Imports
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Row, Col } from "react-bootstrap";
import { Formik, Form } from "formik";
import * as Yup from "yup";
import YupPassword from "yup-password";

import fields from "./fields";

// Components
import { InputField, SubmitButton } from "@components/forms";

// Services
import api from "@services";

// Hooks
import { useNotification } from "@hooks";

YupPassword(Yup);

const SignupForm = () => {
	const [loading, setLoading] = useState(false)

	const notify = useNotification()
	const navigate = useNavigate()

	const initialValues = {
		username: "",
		email: "",
		password: "",
		confirmPassword: "",
		name: "",
		surname: "",
	}

	const SignupSchema = Yup.object().shape({
		username: Yup.string().required(),
		email: Yup.string().email().required(),
		password: Yup.string().password().required(),
		confirmPassword: Yup.string()
			.oneOf([Yup.ref("password"), null], "Passwords must match")
			.required(),
		name: Yup.string().required(),
		surname: Yup.string().required()
	})

	const handleSubmit = ({ username, email, password, name, surname }) => {
		setLoading(true);
		api.auth
			.signup(username, email, password, name, surname)
			.then(() => {
				notify.success("You have been successfully registered");
				navigate("/login", { replace: true });
			})
			.catch((err) => notify.error(err.detail ?? err))
			.finally(() => setLoading(false));
	};

	return (
		<Formik initialValues={initialValues} validationSchema={SignupSchema} onSubmit={handleSubmit}>
			{({ touched, isValid }) => {
				const disableSubmit = (Object.values(touched).length === 0 && isValid) || !isValid || loading

				return (
					<Form>
						<Row>
							{fields.signup.map(props => {
								return (
									<Col key={props.name} xs={12} sm={6}>
										<InputField {...props} />
									</Col>
								);
							})}
						</Row>
						<SubmitButton loading={loading} disabled={disableSubmit}>
							Sign Up
						</SubmitButton>
					</Form>
				);
			}}
		</Formik>
	);
};

export default SignupForm;
